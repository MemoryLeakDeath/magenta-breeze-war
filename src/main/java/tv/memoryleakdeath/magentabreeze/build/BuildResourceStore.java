package tv.memoryleakdeath.magentabreeze.build;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

public final class BuildResourceStore {
    private static final int CODE_POINT = 0x301;
    private static final int KEY_LENGTH = 64;

    private BuildResourceStore() {
    }

    public static void main(String[] args) {
        String keysFile = args[0];
        String storeFile = args[1];
        String passFile = args[2];
        if (StringUtils.isEmpty(keysFile) || StringUtils.isEmpty(storeFile) || StringUtils.isEmpty(passFile)) {
            System.out.println("Usage: java -jar BuildResourceStore.jar <keysFile> <outputFile> <outputKeyFile>");
            return;
        }
        System.out.println("Building resource store...");
        Properties properties = loadProperties(keysFile);
        String storageKey = generateKey();
        storeProperties(properties, storeFile, storageKey);
        storeKeyToFile(storageKey, passFile);
    }

    private static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void storeProperties(Properties properties, String outputFilename, String key) {
        MVStore store = new MVStore.Builder().fileName(outputFilename).encryptionKey(key.toCharArray()).compress()
                .open();
        MVMap<String, String> storedProperties = store.openMap("keys");
        properties.forEach((k, v) -> storedProperties.put(k.toString(), v.toString()));
        store.close();
    }

    private static String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[KEY_LENGTH];
        random.nextBytes(key);
        return new String(key);
    }

    private static void storeKeyToFile(String key, String filename) {
        String storedKey = key.chars().map(c -> c ^ CODE_POINT)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        try {
            Files.write(Paths.get(filename), storedKey.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
