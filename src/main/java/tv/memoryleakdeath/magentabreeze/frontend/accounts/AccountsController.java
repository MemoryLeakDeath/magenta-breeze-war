package tv.memoryleakdeath.magentabreeze.frontend.accounts;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AccountsDao;
import tv.memoryleakdeath.magentabreeze.backend.service.AccountService;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;
import tv.memoryleakdeath.magentabreeze.util.OAuthUtil;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;

@Controller
@RequestMapping("/settings/accounts")
public class AccountsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    private static final String TWITCH_AUTH_URL_BASE = "https://id.twitch.tv/oauth2/authorize";
    private static final String TWITCH_REDIRECT_URI = "/oauth/authenticate";
    private static final String OAUTH_RESPONSE_TYPE = "token";
    private static final String[] TWITCH_OAUTH_SCOPES = { "user:read:broadcast" };
    private static final String YT_AUTH_URL_BASE = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String YT_OAUTH_RESPONSE_TYPE = "code";
    private static final String[] YT_OAUTH_SCOPES = { "https://www.googleapis.com/auth/youtube.readonly" };
    private static final String YT_REDIRECT_URI = "/oauth/yt_authenticate";

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "nav.accounts");
        try {
            model.addAttribute("accounts", accountsDao.getAllAccounts());
        } catch (Exception e) {
            logger.error("Unable to retrieve accounts!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/accounts/accounts";
    }

    @GetMapping("/linktwitch")
    public String startTwitchAuth(HttpServletRequest request) {
        UUID uuid = UUID.randomUUID();
        String twitchAuthUrl = buildTwitchAuthUrl(request, uuid.toString());
        return "redirect:" + twitchAuthUrl;
    }

    @GetMapping("/linkyoutube")
    public String startYoutubeAuth(HttpServletRequest request) {
        UUID uuid = UUID.randomUUID();
        String youtubeAuthUrl = buildYoutubeAuthUrl(request, uuid.toString());
        return "redirect:" + youtubeAuthUrl;
    }

    /**
     * Called from the TwitchOAuthController only.
     * 
     * @param request
     * @param model
     * @param accessToken
     * @param scope
     * @param state
     * @param tokenType
     * @param service
     * @return The View
     */
    @PostMapping("/create")
    public String createAccount(HttpServletRequest request, Model model,
            @RequestParam(name = "access_token", required = true) String accessToken,
            @RequestParam(name = "scope", required = true) String scope,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "token_type", required = true) String tokenType,
            @RequestParam(name = "service", required = true) String service) {
        if (StringUtils.isBlank(accessToken)) {
            logger.error("NO access token received from {}!", service);
            addErrorMessage(request, "text.error.systemerror");
        }
        try {
            boolean createResult = accountService.createAccount(accessToken, service);
            if (!createResult) {
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.oauth.success");
            }
        } catch (Exception e) {
            logger.error("Unable to connect oauth account for service: %s".formatted(service), e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/accounts/";
    }

    private String buildTwitchAuthUrl(HttpServletRequest request, String state) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, TWITCH_REDIRECT_URI);
        return "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s".formatted(TWITCH_AUTH_URL_BASE,
                SecureStorageUtil.getValueKeyFromSecureStorage("twitchapikey", resourceLoader), redirectUrl,
                OAUTH_RESPONSE_TYPE, StringUtils.join(TWITCH_OAUTH_SCOPES, ","), state);
    }

    private String buildYoutubeAuthUrl(HttpServletRequest request, String state) {
        String redirectUrl = OAuthUtil.buildUrlPath(request, YT_REDIRECT_URI);
        String url = "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&access_type=offline&prompt=select_account&state=%s"
                .formatted(
                YT_AUTH_URL_BASE, SecureStorageUtil.getValueKeyFromSecureStorage("youtubeclientid", resourceLoader),
                redirectUrl, YT_OAUTH_RESPONSE_TYPE, StringUtils.join(YT_OAUTH_SCOPES, ","), state);
        return url;
    }
}
