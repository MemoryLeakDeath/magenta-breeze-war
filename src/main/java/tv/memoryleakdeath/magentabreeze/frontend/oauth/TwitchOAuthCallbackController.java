package tv.memoryleakdeath.magentabreeze.frontend.oauth;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/oauth")
public class TwitchOAuthCallbackController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(TwitchOAuthCallbackController.class);

    @GetMapping("/authenticate")
    public String authenticate(HttpServletRequest request, Model model,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription,
            @RequestParam(name = "state", required = false) String state) {
        if (error != null) {
            logger.error("Error during OAuth authorization: {} - {} - {}", error, errorDescription, state);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "oauth/oauth-twitch-callback";
    }

    @GetMapping("/token")
    public String parseToken(HttpServletRequest request, Model model,
            @RequestParam(name = "access_token", required = true) String accessToken,
            @RequestParam(name = "scope", required = true) String scope,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "token_type", required = true) String tokenType) {
        if (StringUtils.isNotBlank(accessToken)) {
            logger.debug("Token received!");
            addSuccessMessage(request, "text.oauth.twitch.success");
        }
        return "redirect:/";
    }
}
