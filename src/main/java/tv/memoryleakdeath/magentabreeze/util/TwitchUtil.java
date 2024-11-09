package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.socket.conduit.TwitchConduitSocketPool;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.common.pojo.TwitchOAuthAppTokenResponse;

@Component
public class TwitchUtil {
    private static final Logger logger = LoggerFactory.getLogger(TwitchUtil.class);
    private static final String AUTH_URL_BASE = "https://id.twitch.tv/oauth2/authorize";
    private static final String REDIRECT_URI = "/oauth/authenticate";
    private static final String REAUTH_REDIRECT_URI = "/oauth/reauthenticate";
    private static final String OAUTH_RESPONSE_TYPE = "token";
    private static final String[] OAUTH_SCOPES = { "user:read:broadcast", "chat:read", "chat:edit", "user:read:chat",
            "user:write:chat", "user:bot" };
    private static final String CLIENT_FLOW_APP_TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String CLIENT_FLOW_GRANT_TYPE = "client_credentials";
    private static final String VALIDATE_TOKEN_URL = "https://id.twitch.tv/oauth2/validate";
    private static final String TWITCH_APP_AUTH_KEY = "twitchappkey";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CloseableHttpClient httpClient;

    private TwitchClient twitchClient;
    private IEventSubConduit conduit;

    public TwitchClient getClient() {
        if (twitchClient == null) {
            twitchClient = TwitchClientBuilder.builder().withEnableHelix(true).withEnableChat(true).build();
        }
        return twitchClient;
    }

    private void initConduit() {
        String accessToken = getAccessToken();
        Assert.notNull(accessToken, "Unable to retreive new app access token from Twitch!");
        try {
            conduit = TwitchConduitSocketPool.create(spec -> {
                spec.appAccessToken(new OAuth2Credential("twitch", accessToken));
                spec.poolShards(4);
            });
        } catch (Exception e) {
            logger.error("Unable to initialize twitch conduit pool!", e);
        }
    }

    public IEventSubConduit getConduit() {
        if (conduit == null) {
            initConduit();
        }
        return conduit;
    }

    public String buildTwitchAuthUrl(HttpServletRequest request, String state) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, REDIRECT_URI);
        return "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s".formatted(AUTH_URL_BASE,
                SecureStorageUtil.getValueKeyFromSecureStorage("twitchapikey", resourceLoader), redirectUrl,
                OAUTH_RESPONSE_TYPE, StringUtils.join(OAUTH_SCOPES, " "), state);
    }

    public String buildTwitchReAuthUrl(HttpServletRequest request, String state, Long accountId) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, REAUTH_REDIRECT_URI);
        return "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s_%d&force_verify=true".formatted(
                AUTH_URL_BASE, SecureStorageUtil.getValueKeyFromSecureStorage("twitchapikey", resourceLoader),
                redirectUrl, OAUTH_RESPONSE_TYPE, StringUtils.join(OAUTH_SCOPES, " "), state, accountId);
    }

    public User getTwitchLoggedInUser(String accessToken) {
        UserList users = getClient().getHelix().getUsers(accessToken, null, null).execute();
        return users.getUsers().stream().findFirst().orElse(null);
    }

    public void joinChat(String chatChannel) {
        if (!getClient().getChat().isChannelJoined(chatChannel) && conduit != null) {
            getClient().getChat().joinChannel(chatChannel);
        }
    }

    private TwitchOAuthAppTokenResponse parseAppTokenResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response, TwitchOAuthAppTokenResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse app token response from twitch!", e);
        }
        return null;
    }

    public String getAccessToken() {
        TwitchOAuthAppTokenResponse response = getValidToken();
        if (response != null && response.getAccessToken() != null) {
            return response.getAccessToken();
        }
        return null;
    }

    private TwitchOAuthAppTokenResponse getValidToken() {
        TwitchOAuthAppTokenResponse tokenResponse = null;
        // step 1 - check storage
        String token = SecureStorageUtil.getValueKeyFromSecureStorage(TWITCH_APP_AUTH_KEY, resourceLoader);
        if (token == null) {
            logger.debug("No twitch app token found in storage, retrieving new one!");
            tokenResponse = getAndStoreNewAppToken();
        } else {
            // step 2 - check validity
            if (isTokenValid(token)) {
                tokenResponse = new TwitchOAuthAppTokenResponse();
                tokenResponse.setAccessToken(token);
                logger.debug("Twitch app token found and valid, returning...");
            } else {
                logger.debug("Twitch app token not valid, retrieving new one!");
                tokenResponse = getAndStoreNewAppToken();
            }
        }
        return tokenResponse;
    }

    private boolean isTokenValid(String token) {
        HttpGet get = new HttpGet(VALIDATE_TOKEN_URL);
        get.addHeader("Authorization", "Bearer " + token);
        try {
            boolean valid = httpClient.execute(get, new HttpClientResponseHandler<Boolean>() {
                @Override
                public Boolean handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
                    return (response.getCode() == 200);
                }
            });
            return valid;
        } catch (Exception e) {
            logger.error("Unable to validate twitch bearer token!", e);
        }
        return false;
    }

    private TwitchOAuthAppTokenResponse getAndStoreNewAppToken() {
        TwitchOAuthAppTokenResponse tokenResponse = getNewAppToken();
        Assert.notNull(tokenResponse, "App Token response was null from Twitch!");
        Assert.notNull(tokenResponse.getAccessToken(), "App access token was not received from Twitch!");
        SecureStorageUtil.saveValueKeyInSecureStorage(TWITCH_APP_AUTH_KEY, tokenResponse.getAccessToken(),
                resourceLoader);
        return tokenResponse;
    }

    private TwitchOAuthAppTokenResponse getNewAppToken() {
        HttpPost post = new HttpPost(CLIENT_FLOW_APP_TOKEN_URL);
        List<NameValuePair> values = new ArrayList<>();
        values.add(new BasicNameValuePair("client_id",
                SecureStorageUtil.getValueKeyFromSecureStorage("twitchapikey", resourceLoader)));
        values.add(new BasicNameValuePair("client_secret",
                SecureStorageUtil.getValueKeyFromSecureStorage("twitchapisecret", resourceLoader)));
        values.add(new BasicNameValuePair("grant_type", CLIENT_FLOW_GRANT_TYPE));
        post.setEntity(new UrlEncodedFormEntity(values));
        try {
            String result = httpClient.execute(post, new HttpClientResponseHandler<String>() {
                @Override
                public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
                    HttpEntity body = response.getEntity();
                    return EntityUtils.toString(body, StandardCharsets.UTF_8);
                }
            });
            return parseAppTokenResponse(result);
        } catch (Exception e) {
            logger.error("Unable to contact youtube to exchange initial auth token!", e);
        }
        return null;
    }
}
