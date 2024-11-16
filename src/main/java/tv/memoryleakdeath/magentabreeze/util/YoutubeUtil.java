package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.frontend.oauth.YoutubeOAuthCallbackController;

@Component
public class YoutubeUtil {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeUtil.class);
    private static final String AUTH_URL_BASE = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String OAUTH_RESPONSE_TYPE = "code";
    private static final String[] OAUTH_SCOPES = { "https://www.googleapis.com/auth/youtube.readonly" };
    private static final String REDIRECT_URI = "/oauth/yt_authenticate";
    private static final String REAUTH_REDIRECT_URI = "/oauth/yt_reauthenticate";

    @Autowired
    private ResourceLoader resourceLoader;

    private YouTube yt;

    public YouTube getService(Credential cred) {
        if (yt == null) {
            try {
                yt = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                        cred).setApplicationName("Magenta Breeze").build();
            } catch (GeneralSecurityException | IOException e) {
                logger.error("Unable to create a youtube api object!", e);
            }
        }
        return yt;
    }

    public Credential buildCredential(String accessToken, String refreshToken, Long expiresIn) {
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

    public String buildYoutubeAuthUrl(HttpServletRequest request, String state) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, REDIRECT_URI);
        return buildAuthUrl(redirectUrl, state, "offline", "select_account");
    }

    public String buildYoutubeReauthUrl(HttpServletRequest request, String state, Long accountId) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, REAUTH_REDIRECT_URI);
        String reauth_state = "%s_%d".formatted(state, accountId);
        return buildAuthUrl(redirectUrl, reauth_state, "offline", "consent");
    }

    private String buildAuthUrl(String redirectUrl, String state, String accessType, String prompt) {
        String url = "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&access_type=%s&prompt=%s&state=%s"
                .formatted(AUTH_URL_BASE,
                        SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientid", resourceLoader), redirectUrl,
                        OAUTH_RESPONSE_TYPE, StringUtils.join(OAUTH_SCOPES, ","), accessType, prompt, state);
        return url;
    }
}
