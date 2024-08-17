package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import tv.memoryleakdeath.magentabreeze.frontend.oauth.YoutubeOAuthCallbackController;

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

    public static Credential buildCredential(String accessToken, String refreshToken, Long expiresIn, ResourceLoader resourceLoader) {
        Credential cred = null;
        String clientId = SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientid", resourceLoader);
        String clientSecret = SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientsecret", resourceLoader);
        try {
            cred = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setTokenServerUrl(new GenericUrl(YoutubeOAuthCallbackController.TOKEN_ENDPOINT)).build()
                    .setAccessToken(accessToken).setRefreshToken(refreshToken).setExpiresInSeconds(expiresIn);
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Unable to build youtube credential for access!", e);
        }
        return cred;
    }
}
