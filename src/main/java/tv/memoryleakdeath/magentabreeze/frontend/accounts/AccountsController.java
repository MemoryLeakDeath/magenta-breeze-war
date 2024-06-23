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

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AccountsDao;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.Account;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;

@Controller
@RequestMapping("/settings/accounts")
public class AccountsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    private static final String TWITCH_AUTH_URL_BASE = "https://id.twitch.tv/oauth2/authorize";
    private static final String AUTH_REDIRECT_URI = "https://localhost:6443/oauth/authenticate";
    private static final String OAUTH_RESPONSE_TYPE = "token";
    private static final String[] TWITCH_OAUTH_SCOPES = { "user:read:broadcast" };

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourceLoader resourceLoader;

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
        String twitchAuthUrl = buildTwitchAuthUrl(uuid.toString());
        return "redirect:" + twitchAuthUrl;
    }

    @GetMapping("/linkyoutube")
    public String startYoutubeAuth(HttpServletRequest request) {
        // TODO: implement this!
        addErrorMessage(request, "text.error.systemerror");
        return "redirect:/settings/accounts/";
    }

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
            Account account = null;
            ServiceTypes serviceType = ServiceTypes.TWITCH;
            if (ServiceTypes.TWITCH.name().equals(service)) {
                User twitchUser = getTwitchLoggedInUser(accessToken);
                account = buildAccountObject(serviceType, twitchUser);
            } else if (ServiceTypes.YOUTUBE.name().equals(service)) {
                // TODO: implement this!
                serviceType = ServiceTypes.YOUTUBE;
                logger.debug("YOUTUBE not yet implemented!");
            }
            if (!accountsDao.createAccount(account)) {
                logger.error("Unable to create account for linkage!");
                addErrorMessage(request, "text.error.systemerror");
            } else {
                SecureStorageUtil.saveOAuthTokenInSecureStorage(accessToken, "access_token", serviceType,
                        account.getId().intValue(), resourceLoader);
                addSuccessMessage(request, "text.oauth.success");
            }
        } catch (Exception e) {
            logger.error("Unable to connect oauth account for service: %s".formatted(service), e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/accounts/";
    }

    private String buildTwitchAuthUrl(String state) {
        return "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s".formatted(TWITCH_AUTH_URL_BASE,
                SecureStorageUtil.getValueFromSecureStorage("keys", "twitchapikey", resourceLoader), AUTH_REDIRECT_URI,
                OAUTH_RESPONSE_TYPE, StringUtils.join(TWITCH_OAUTH_SCOPES, ","), state);
    }

    private User getTwitchLoggedInUser(String accessToken) {
        TwitchClient twitchClient = TwitchClientBuilder.builder().withEnableHelix(true).build();
        UserList users = twitchClient.getHelix().getUsers(accessToken, null, null).execute();
        return users.getUsers().stream().findFirst().orElse(null);
    }

    private Account buildAccountObject(ServiceTypes service, User twitchUser) {
        Account account = new Account();
        account.setService(service.name());
        account.setDisplayName(twitchUser.getDisplayName());
        account.setProfileUrl(twitchUser.getProfileImageUrl());
        return account;
    }
}
