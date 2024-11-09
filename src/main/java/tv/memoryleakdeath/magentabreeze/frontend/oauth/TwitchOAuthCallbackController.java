package tv.memoryleakdeath.magentabreeze.frontend.oauth;

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
        model.addAttribute("createUrl", "/settings/accounts/create");
        if (error != null) {
            logger.error("Error during OAuth authorization: {} - {} - {}", error, errorDescription, state);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "oauth/oauth-twitch-callback";
    }

    @GetMapping("/reauthenticate")
    public String reauthenticate(HttpServletRequest request, Model model,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription,
            @RequestParam(name = "state", required = false) String state) {
        model.addAttribute("createUrl", "/settings/accounts/update");
        if (error != null) {
            logger.error("Error during OAuth reauthorization: {} - {} - {}", error, errorDescription, state);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "oauth/oauth-twitch-callback";
    }
}
