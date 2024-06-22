package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.service.AlertEventsService;
import tv.memoryleakdeath.magentabreeze.backend.service.AlertSettingsService;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsWithAssets;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/alerts")
public class AlertViewController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AlertViewController.class);

    @Autowired
    private AlertEventsService eventsService;

    @Autowired
    private AlertSettingsService alertSettingsService;

    @GetMapping("/view/{id}")
    public String view(HttpServletRequest request, Model model, @PathVariable(name = "id") Long id) {
        viewAlertContents(request, model, id);
        return "alerts/view-alert";
    }

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        eventsService.addListener(emitter);
        return emitter;
    }

    @GetMapping("/contents/{id}")
    public String viewAlertContents(HttpServletRequest request, Model model, @PathVariable(name = "id") Long id) {
        try {
            AlertSettingsWithAssets alertSettings = alertSettingsService.getSettingsWithAssetsAndAccounts(id);
            model.addAttribute("alertSettings", alertSettings);
        } catch (Exception e) {
            logger.error("Unable to find alert settings for alert id: " + id, e);
        }
        return "alerts/alert-message";
    }
}
