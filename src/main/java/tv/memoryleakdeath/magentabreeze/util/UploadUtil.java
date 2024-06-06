package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import tv.memoryleakdeath.magentabreeze.common.ContentTypeConstants;

public class UploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);

    private UploadUtil() {
    }

    public static boolean isImageContentType(String contentType) {
        return contentType != null && ContentTypeConstants.IMAGE_CONTENT_TYPES.contains(contentType);
    }

    public static boolean isAudioContentType(String contentType) {
        return contentType != null && ContentTypeConstants.AUDIO_CONTENT_TYPES.contains(contentType);
    }

    public static boolean uploadToFileSystem(MultipartFile file, String path, Long id) {
        ensureUploadDirectoryExists(path + "/uploads/");
        try {
            file.transferTo(Paths.get(path + "/uploads/", id + "_" + file.getOriginalFilename()));
        } catch (IOException e) {
            logger.error("Failed to upload file to filesystem!", e);
            return false;
        }
        return true;
    }

    private static void ensureUploadDirectoryExists(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                logger.error("Failed to create upload directory: " + path, e);
            }
        }
    }

    public static final String getUploadedFilename(Long id, String originalFilename) {
        return id + "_" + originalFilename;
    }
}
