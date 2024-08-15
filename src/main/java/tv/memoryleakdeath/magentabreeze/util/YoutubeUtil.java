package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

public final class YoutubeUtil {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeUtil.class);

    private YoutubeUtil() {
    }

    public static YouTube getService(Credential cred) {
        try {
            YouTube yt = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    cred).setApplicationName("Magenta Breeze").build();
            return yt;
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Unable to create a youtube api object!", e);
        }
        return null;
    }
}
