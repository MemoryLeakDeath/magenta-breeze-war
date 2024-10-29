package tv.memoryleakdeath.magentabreeze.frontend.accounts;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import tv.memoryleakdeath.magentabreeze.util.TwitchUtil;
import tv.memoryleakdeath.magentabreeze.util.YoutubeUtil;

@Controller
@RequestMapping("/settings/accounts")
public class AccountsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TwitchUtil twitchUtil;

    @Autowired
    private YoutubeUtil youtubeUtil;

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
        String twitchAuthUrl = twitchUtil.buildTwitchAuthUrl(request, uuid.toString());
        return "redirect:" + twitchAuthUrl;
    }

    @GetMapping("/linkyoutube")
    public String startYoutubeAuth(HttpServletRequest request) {
        UUID uuid = UUID.randomUUID();
        String youtubeAuthUrl = youtubeUtil.buildYoutubeAuthUrl(request, uuid.toString());
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
}
