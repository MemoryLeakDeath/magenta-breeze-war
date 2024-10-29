package tv.memoryleakdeath.magentabreeze.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.service.twitch.TwitchChatService;
import tv.memoryleakdeath.magentabreeze.backend.service.youtube.ChatBrowserService;

@Controller
@RequestMapping("/")
public class HomeController extends BaseFrontendController {
    private static final String YT_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String YT_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    private static final String YT_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

    @Autowired
    private ChatBrowserService chatBrowserService;

    @Autowired
    private TwitchChatService twitchChatService;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        return "home/home";
    }

    @GetMapping("/startbrowser")
    public String startChatBrowser(HttpServletRequest request, Model model) {
        chatBrowserService.launchChatBrowser(getLocale(request));
        addSuccessMessage(request, "text.success.browserlaunched");
        return "home/home";
    }

    @GetMapping("/stopbrowser")
    public String stopChatBrowser(HttpServletRequest request, Model model) {
        chatBrowserService.stopChatBrowser();
        addSuccessMessage(request, "text.success.browserstopped");
        return "home/home";
    }

    @GetMapping("/attachtwitch")
    public String attachTwitchChat(HttpServletRequest request, Model model) {
        twitchChatService.attachChat(getLocale(request));
        addSuccessMessage(request, "text.success.twitchattached");
        return "home/home";
    }
}
