package tv.memoryleakdeath.magentabreeze.frontend.oauth;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.service.AccountService;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.YoutubeOAuthResponse;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;
import tv.memoryleakdeath.magentabreeze.util.OAuthUtil;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;

@Controller
@RequestMapping("/oauth")
public class YoutubeOAuthCallbackController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeOAuthCallbackController.class);
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String YT_REDIRECT_URI = "/oauth/yt_authenticate";
    private static final String YT_REAUTH_REDIRECT_URI = "/oauth/yt_reauthenticate";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private AccountService accountService;

    @GetMapping("/yt_authenticate")
    public String authenticate(HttpServletRequest request, Model model,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "code", required = false) String initialAuthCode,
            @RequestParam(name = "state", required = false) String state) {
        if (error != null) {
            logger.error("Error during OAuth authorization: {}", error);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        try {
            YoutubeOAuthResponse auth = exchangeToken(request, initialAuthCode, YT_REDIRECT_URI);
            if (auth == null) {
                logger.error("No auth object was created!");
                addErrorMessage(request, "text.error.systemerror");
                return "redirect:/";
            }
            boolean createResult = accountService.createAccount(auth.getAccessToken(), auth.getRefreshToken(),
                    ServiceTypes.YOUTUBE.name(), auth.getExpiresIn());
            if (!createResult) {
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.oauth.success");
            }
        } catch (Exception e) {
            logger.error("Unable to process oauth exchange with youtube!", e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "redirect:/settings/accounts/";
    }

    @GetMapping("/yt_reauthenticate")
    public String reauthenticate(HttpServletRequest request, Model model,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "code", required = false) String initialAuthCode,
            @RequestParam(name = "state", required = false) String state) {
        if (error != null) {
            logger.error("Error during OAuth authorization: {}", error);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        try {
            YoutubeOAuthResponse auth = exchangeToken(request, initialAuthCode, YT_REAUTH_REDIRECT_URI);
            if (auth == null) {
                logger.error("No auth object was created!");
                addErrorMessage(request, "text.error.systemerror");
                return "redirect:/";
            }

            // pull account id off state variable
            String[] stateParts = state.split("_");
            Integer accountId = null;
            if (stateParts.length == 2) {
                accountId = Integer.parseInt(stateParts[1]);
            } else {
                logger.error("Unable to retrieve account id from state variable!");
                addErrorMessage(request, "text.error.systemerror");
                return "redirect:/";
            }

            boolean updateResult = accountService.updateAccount(auth.getAccessToken(), auth.getRefreshToken(),
                    ServiceTypes.YOUTUBE.name(), auth.getExpiresIn(), accountId);
            if (!updateResult) {
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.oauth.success");
            }
        } catch (Exception e) {
            logger.error("Unable to process oauth exchange with youtube!", e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "redirect:/settings/accounts/";
    }

    private YoutubeOAuthResponse exchangeToken(HttpServletRequest request, String initialAuthCode, String redirectUri) {
        HttpPost post = new HttpPost(TOKEN_ENDPOINT);
        List<NameValuePair> values = new ArrayList<>();
        values.add(new BasicNameValuePair("client_id",
                SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientid", resourceLoader)));
        values.add(new BasicNameValuePair("client_secret",
                SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientsecret", resourceLoader)));
        values.add(new BasicNameValuePair("redirect_uri", OAuthUtil.buildUrlPath(request, redirectUri)));
        values.add(new BasicNameValuePair("grant_type", "authorization_code"));
        values.add(new BasicNameValuePair("code", initialAuthCode));
        post.setEntity(new UrlEncodedFormEntity(values));
        try {
            String result = httpClient.execute(post, new HttpClientResponseHandler<String>() {
                @Override
                public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
                    HttpEntity body = response.getEntity();
                    return EntityUtils.toString(body, StandardCharsets.UTF_8);
                }
            });

            // handle error
            if ("invalid_grant".equals(result)) {
                logger.error("Received 'invalid grant' error from Youtube token exchange!");
            } else {
                // clean up string
                String cleanedResult = StringUtils.removeIgnoreCase(result, "\\n");
                return parseAuthResponse(cleanedResult);
            }
        } catch (IOException e) {
            logger.error("Unable to contact youtube to exchange initial auth token!", e);
        }
        return null;
    }

    private YoutubeOAuthResponse parseAuthResponse(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(jsonString, YoutubeOAuthResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse json result from youtube auth exchange!", e);
        }
        return null;
    }
}
