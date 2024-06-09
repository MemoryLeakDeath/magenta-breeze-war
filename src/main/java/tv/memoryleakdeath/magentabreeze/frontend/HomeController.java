package tv.memoryleakdeath.magentabreeze.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;

@Controller
@RequestMapping("/")
public class HomeController extends BaseFrontendController {

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute("hello", "Hello World!");
        model.addAttribute("SecureStorageTestValue",
                SecureStorageUtil.getValueFromSecureStorage("keys", "testvalue", resourceLoader));
        request.getSession().setAttribute("testSessionAttrib", "this is a test of a session attribute.");
        return "home/home";
    }
}
