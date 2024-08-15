package tv.memoryleakdeath.magentabreeze.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.tools.ChangeFileEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;

public final class SecureStorageUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureStorageUtil.class);
    private static final String STORAGE_FILE = "resources.db";
    private static final String KEY_FILE = "classpath:index.mstore";
    private static final int KEY_LENGTH = 64;
    private static final int CODE_POINT = 0x301;
    private static final String OAUTH_TOKEN_MAP_NAME = "tokens";
    private static final String KEYS_TOKEN_MAP_NAME = "keys";

    private SecureStorageUtil() {
    }

    private static String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[KEY_LENGTH];
        random.nextBytes(key);
        for (byte b : key) {
            if (b == 0x20) {
                b += random.nextInt(50); // passwords should not contain spaces
            }
        }
        return new String(key);
    }

    private static String getCurrentKey(ResourceLoader loader) {
        try (FileInputStream fis = new FileInputStream(loader.getResource(KEY_FILE).getFile())) {
            String encryptedKey = new String(fis.readAllBytes());
            return encryptedKey.chars().map(c -> c ^ CODE_POINT)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        } catch (Exception e) {
            logger.error("Failed to read key file!", e);
        }
        return null;
    }

    private static String getResourceFilePath(ResourceLoader loader) throws IOException {
        return loader.getResource("classpath:" + STORAGE_FILE).getURL().getPath();
    }

    private static MVStore getMVStore(String resourcePath, String currentKey) {
        return new MVStore.Builder().fileName(resourcePath).encryptionKey(currentKey.toCharArray()).compress().open();
    }

    public static void rotateSecureStorage(ResourceLoader loader) {
        String rotatedKey = generateKey();
        String oldKey = getCurrentKey(loader);
        if (oldKey == null) {
            logger.error("Failed to rotate secure storage, no key found!");
            return;
        }
        try {
            String resourcePath = getResourceFilePath(loader);
            ChangeFileEncryption.execute(resourcePath, STORAGE_FILE, "AES", oldKey.toCharArray(),
                    rotatedKey.toCharArray(), true);
            storeKeyToFile(loader, rotatedKey, KEY_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void storeKeyToFile(ResourceLoader loader, String key, String filename) {
        String storedKey = key.chars().map(c -> c ^ CODE_POINT)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        try {
            Files.newBufferedWriter(Paths.get(loader.getResource(filename).getURI())).write(storedKey);
        } catch (IOException e) {
            logger.error("Failed to store key to file!", e);
        }
    }

    public static String getValueFromSecureStorage(String mapName, String key, ResourceLoader loader) {
        String value = null;
        try {
            MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader));
            MVMap<String, String> storedValues = store.openMap(mapName);
            value = storedValues.get(key);
            store.close();
        } catch (Exception e) {
            logger.error("Failed to read value from secure storage!", e);
        }
        return value;
    }

    public static String getValueKeyFromSecureStorage(String key, ResourceLoader loader) {
        return getValueFromSecureStorage(KEYS_TOKEN_MAP_NAME, key, loader);
    }

    public static boolean saveOAuthTokenInSecureStorage(String token, String tokenType, ServiceTypes service, int accountId,
            ResourceLoader loader) {
        boolean success = false;
        try {
            MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader));
            MVMap<String, String> storedTokens = store.openMap(OAUTH_TOKEN_MAP_NAME);
            storedTokens.put("%s_%s_%d".formatted(service.name(), tokenType, accountId), token);
            logger.debug("Storing token for account: {}", "%s_%s_%d".formatted(service.name(), tokenType, accountId));
            store.close();
            success = true;
        } catch (Exception e) {
            logger.error("Failed to open secure storage to save new value!", e);
        }
        return success;
    }

    public static String getOAuthTokenFromSecureStorage(ServiceTypes service, String tokenType, int accountId,
            ResourceLoader loader) {
        String value = null;
        try {
            MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader));
            MVMap<String, String> storedTokens = store.openMap(OAUTH_TOKEN_MAP_NAME);
            value = storedTokens.get("%s_%s_%d".formatted(service.name(), tokenType, accountId));
            store.close();
        } catch (Exception e) {
            logger.error("Unable to open secure storage to retrieve value!", e);
        }
        return value;
    }
}
