package tv.memoryleakdeath.magentabreeze.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class HomeController extends BaseFrontendController {
    private static final String YT_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String YT_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    private static final String YT_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        return "home/home";
    }

}
