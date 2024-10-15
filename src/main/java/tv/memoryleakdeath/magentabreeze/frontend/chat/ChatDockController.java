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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.service.ChatEventsService;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/dock/chat")
public class ChatDockController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChatDockController.class);

    @Autowired
    private ChatEventsService chatEventsService;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        viewContents(request, model);
        return "dock/chat/widget-chat";
    }

    @GetMapping("/contents")
    public String viewContents(HttpServletRequest request, Model model) {
        return "dock/chat/messages";
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
}
