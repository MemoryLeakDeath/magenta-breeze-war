package tv.memoryleakdeath.magentabreeze.frontend.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.ChatSettingsDao;
import tv.memoryleakdeath.magentabreeze.backend.service.ChatEventsService;
import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettingsRow;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/chat")
public class ChatViewController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    @Autowired
    private ChatSettingsDao settingsDao;

    @Autowired
    private ChatEventsService chatEventsService;

    @GetMapping("/view/{id}")
    public String view(HttpServletRequest request, Model model, @PathVariable(name = "id") Long id) {
        viewChatContents(request, model, id);
        return "chat/view-chat";
    }

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        chatEventsService.addListener(emitter);
        return emitter;
    }

    @ControllerAdvice
    static class CustomAdvice {
        @ExceptionHandler(AsyncRequestNotUsableException.class)
        void handleAsyncRequestNotUsableException(AsyncRequestNotUsableException e) {
            // FIXME: remove it after SB fix it (6.2.0 release), probably this issue:
            // https://github.com/spring-projects/spring-framework/issues/33225
        }
    }

    @GetMapping("/contents/{id}")
    public String viewChatContents(HttpServletRequest request, Model model, @PathVariable(name = "id") Long id) {
        try {
            ChatSettingsRow chatSettings = settingsDao.getSettingsById(id);
            model.addAttribute("chatSettings", chatSettings);
        } catch (Exception e) {
            logger.error("Unable to find alert settings for alert id: " + id, e);
        }
        return "chat/chat-message";
    }
}
