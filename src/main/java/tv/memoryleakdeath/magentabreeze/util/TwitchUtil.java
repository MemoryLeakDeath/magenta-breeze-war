package tv.memoryleakdeath.magentabreeze.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
    private static final String OAUTH_RESPONSE_TYPE = "token";
    private static final String[] OAUTH_SCOPES = { "user:read:broadcast", "chat:read", "chat:edit", "user:bot" };
    private static final String CLIENT_FLOW_APP_TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String CLIENT_FLOW_GRANT_TYPE = "client_credentials";

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
        TwitchOAuthAppTokenResponse appToken = getNewAppToken();
        Assert.notNull(appToken, "Unable to retrieve new app token from Twitch!");
        Assert.notNull(appToken.getAccessToken(), "Unable to retreive new app access token from Twitch!");
        try {
            conduit = TwitchConduitSocketPool.create(spec -> {
                spec.appAccessToken(new OAuth2Credential("twitch", appToken.getAccessToken()));
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

    public TwitchOAuthAppTokenResponse getNewAppToken() {
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
        } catch (IOException e) {
            logger.error("Unable to contact youtube to exchange initial auth token!", e);
        }
        return null;
    }
}
