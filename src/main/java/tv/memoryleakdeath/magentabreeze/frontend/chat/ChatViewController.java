package tv.memoryleakdeath.magentabreeze.frontend.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/chat")
public class ChatViewController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        return "chat/view-chat";
    }
}
