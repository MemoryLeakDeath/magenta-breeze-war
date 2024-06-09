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

public final class SecureStorageUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureStorageUtil.class);
    private static final String STORAGE_FILE = "resources.db";
    private static final String KEY_FILE = "classpath:index.mstore";
    private static final int KEY_LENGTH = 64;
    private static final int CODE_POINT = 0x301;

    private SecureStorageUtil() {
    }

    private static String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[KEY_LENGTH];
        random.nextBytes(key);
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

    public static void rotateSecureStorage(ResourceLoader loader) {
        String rotatedKey = generateKey();
        String oldKey = getCurrentKey(loader);
        if (oldKey == null) {
            logger.error("Failed to rotate secure storage, no key found!");
            return;
        }
        try {
            String resourcePath = loader.getResource("classpath:" + STORAGE_FILE).getURL().getPath();
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
            String currentKey = getCurrentKey(loader);
            String resourcePath = loader.getResource("classpath:" + STORAGE_FILE).getURL().getPath();
            MVStore store = new MVStore.Builder().fileName(resourcePath).encryptionKey(currentKey.toCharArray())
                    .compress().open();
            MVMap<String, String> storedValues = store.openMap(mapName);
            value = storedValues.get(key);
            store.close();
        } catch (Exception e) {
            logger.error("Failed to read value from secure storage!", e);
        }
        return value;
    }
}
