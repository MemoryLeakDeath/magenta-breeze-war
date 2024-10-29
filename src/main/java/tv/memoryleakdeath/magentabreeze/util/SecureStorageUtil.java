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

import tv.memoryleakdeath.magentabreeze.common.OAuthTokenTypes;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;

public final class SecureStorageUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureStorageUtil.class);
    private static final String DATABASE_PATH_PROPERTY = "dbpath";
    private static final String STORAGE_FILE = "resources.db";
    private static final String KEY_FILE = "index.mstore";
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

    private static String getPath(String resource) {
        String path = System.getProperty(DATABASE_PATH_PROPERTY);
        return "%s/%s".formatted(path, resource);
    }

    private static String getCurrentKey(ResourceLoader loader, String keyFilePath) {
        try (FileInputStream fis = new FileInputStream(loader.getResource("file:" + keyFilePath).getFile())) {
            String encryptedKey = new String(fis.readAllBytes());
            return encryptedKey.chars().map(c -> c ^ CODE_POINT)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        } catch (Exception e) {
            logger.error("Failed to read key file!", e);
        }
        return null;
    }

    private static String getCurrentKey(ResourceLoader loader) {
        return getCurrentKey(loader, getPath(KEY_FILE));
    }

    private static String getResourceFilePath(ResourceLoader loader) throws IOException {
        return loader.getResource("file:" + getPath(STORAGE_FILE)).getURL().getPath();
    }

    private static MVStore getMVStore(String resourcePath, String currentKey) {
        return new MVStore.Builder().fileName(resourcePath).encryptionKey(currentKey.toCharArray()).compress().open();
    }

    private static boolean fileExists(String resourceName) {
        String filePath = getPath(resourceName);
        return Files.exists(Paths.get(filePath));
    }

    private static void copyFileFromClasspath(String resourceName, ResourceLoader loader) {
        String destination = getPath(resourceName);
        try {
            Files.copy(Paths.get(loader.getResource("classpath:" + resourceName).getURL().getPath()),
                    Paths.get(destination));
        } catch (IOException e) {
            logger.error("Unable to copy %s to resource destination!".formatted(resourceName), e);
        }
    }

    private static void mergeResourceKeysMapFromClasspath(ResourceLoader loader) {
        String destination = getPath(STORAGE_FILE);
        String source = "";
        try {
            source = loader.getResource("classpath:" + STORAGE_FILE).getURL().getPath();
        } catch (IOException e) {
            logger.error("Unable to open storage file database for merging!", e);
        }
        try (MVStore sourceStore = getMVStore(source, getCurrentKey(loader, "classpath:" + KEY_FILE))) {
            try (MVStore destinationStore = getMVStore(destination, getCurrentKey(loader))) {
                destinationStore.openMap(KEYS_TOKEN_MAP_NAME).putAll(sourceStore.openMap(KEYS_TOKEN_MAP_NAME));
                destinationStore.commit();
            } catch (Exception e) {
                logger.error("Unable to write to destinationstore for merge keys!", e);
            }
        } catch (Exception e) {
            logger.error("unable to read from sourcestore for merge keys!", e);
        }
    }

    private static void createResourcePaths() {
        String path = System.getProperty(DATABASE_PATH_PROPERTY);
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            logger.error("Could not init resource file paths!", e);
        }
    }

    public static void initResourceFiles(ResourceLoader loader) {
        String resourceDatabase = getPath(STORAGE_FILE);
        if (!fileExists(resourceDatabase)) {
            createResourcePaths();
            copyFileFromClasspath(STORAGE_FILE, loader);
            copyFileFromClasspath(KEY_FILE, loader);
        } else {
            mergeResourceKeysMapFromClasspath(loader);
        }
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
            logger.error("Failed to rotate secure storage!", e);
        }
    }

    private static void storeKeyToFile(ResourceLoader loader, String key, String filename) {
        String storedKey = key.chars().map(c -> c ^ CODE_POINT)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        try {
            Files.newBufferedWriter(Paths.get("file:" + loader.getResource(filename).getURI())).write(storedKey);
        } catch (IOException e) {
            logger.error("Failed to store key to file!", e);
        }
    }

    public static String getValueFromSecureStorage(String mapName, String key, ResourceLoader loader) {
        String value = null;
        try (MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader))) {
            MVMap<String, String> storedValues = store.openMap(mapName);
            value = storedValues.get(key);
        } catch (Exception e) {
            logger.error("Failed to read value from secure storage!", e);
        }
        return value;
    }

    public static String getValueKeyFromSecureStorage(String key, ResourceLoader loader) {
        return getValueFromSecureStorage(KEYS_TOKEN_MAP_NAME, key, loader);
    }

    public static boolean saveOAuthTokenInSecureStorage(String token, OAuthTokenTypes tokenType, ServiceTypes service,
            int accountId,
            ResourceLoader loader) {
        boolean success = false;
        String tokenKey = "%s_%s_%d".formatted(service.name(), tokenType.getTokenKey(), accountId);
        if (token == null) {
            logger.error("attempted to save null token value for key: {}", tokenKey);
            return false;
        }
        try (MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader))) {
            MVMap<String, String> storedTokens = store.openMap(OAUTH_TOKEN_MAP_NAME);
            storedTokens.put(tokenKey, token);
            logger.debug("Storing token for account: {}", tokenKey);
            store.commit();
            success = true;
        } catch (Exception e) {
            logger.error("Failed to open secure storage to save new value!", e);
        }
        return success;
    }

    public static String getOAuthTokenFromSecureStorage(ServiceTypes service, OAuthTokenTypes tokenType, int accountId,
            ResourceLoader loader) {
        String value = null;
        try (MVStore store = getMVStore(getResourceFilePath(loader), getCurrentKey(loader))) {
            MVMap<String, String> storedTokens = store.openMap(OAUTH_TOKEN_MAP_NAME);
            value = storedTokens.get("%s_%s_%d".formatted(service.name(), tokenType.getTokenKey(), accountId));
        } catch (Exception e) {
            logger.error("Unable to open secure storage to retrieve value!", e);
        }
        return value;
    }
}
