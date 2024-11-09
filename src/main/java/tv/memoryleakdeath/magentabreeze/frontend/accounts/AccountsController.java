package tv.memoryleakdeath.magentabreeze.frontend.accounts;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.service.AccountService;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;
import tv.memoryleakdeath.magentabreeze.util.TwitchUtil;
import tv.memoryleakdeath.magentabreeze.util.YoutubeUtil;

@Controller
@RequestMapping("/settings/accounts")
public class AccountsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

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
            model.addAttribute("accounts", accountService.getAllAccounts());
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

    @PostMapping("/relinktwitch")
    public String startTwitchReAuth(HttpServletRequest request,
            @RequestParam(name = "id", required = true) Long accountId) {
        UUID uuid = UUID.randomUUID();
        String twitchAuthUrl = twitchUtil.buildTwitchReAuthUrl(request, uuid.toString(), accountId);
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
    @PostMapping("/update")
    public String updateAccount(HttpServletRequest request, Model model,
            @RequestParam(name = "access_token", required = true) String accessToken,
            @RequestParam(name = "scope", required = true) String scope,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "token_type", required = true) String tokenType,
            @RequestParam(name = "service", required = true) String service) {
        if (StringUtils.isBlank(accessToken)) {
            logger.error("NO access token received from {}!", service);
            addErrorMessage(request, "text.error.systemerror");
        }
        if (StringUtils.isBlank(state) || !state.contains("_")) {
            logger.error("Unable to parse account id from oauth state variable!");
            addErrorMessage(request, "text.error.systemerror");
        }
        try {
            String[] stateParts = state.split("_");
            Integer accountId = Integer.valueOf(stateParts[1]);
            boolean updateResult = accountService.updateAccount(accessToken, service, accountId);
            if (!updateResult) {
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.oauth.success");
            }
        } catch (Exception e) {
            logger.error("Unable to update oauth account for service: %s".formatted(service), e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/accounts/";
    }

    @PostMapping("/delete")
    public String delete(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) Long accountId) {
        try {
            boolean success = accountService.deleteAccount(accountId);
            if (!success) {
                logger.error("Unable to delete account with id: {}", accountId);
                addErrorMessage(request, "text.error.systemerror");
            } else {
                logger.debug("successfully deleted account with id: {}", accountId);
                addSuccessMessage(request, "text.success.accountdeleted");
            }
        } catch (Exception e) {
            logger.error("Unable to delete account with id: " + accountId, e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/accounts/";
    }

    @PostMapping("/togglechatonly")
    public ResponseEntity<Void> toggleChatOnlyFlag(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) Long id,
            @RequestParam(name = "active", required = true) Boolean activeFlag) {
        try {
            boolean success = accountService.updateChatOnlyFlag(id, activeFlag);
            if (!success) {
                logger.error("Unable to update ChatOnly flag for account id: {}", id);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Unable to toggle ChatOnly flag for account id: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
