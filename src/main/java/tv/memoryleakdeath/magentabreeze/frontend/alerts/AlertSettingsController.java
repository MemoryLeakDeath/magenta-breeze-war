package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AlertSettingsDao;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/settings/alerts")
public class AlertSettingsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsController.class);

    @Autowired
    private AlertSettingsDao settingsDao;

    @Autowired
    private AlertSettingsValidator<AlertSettingsModel> settingsValidator;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "text.alerts.title");
        try {
            List<AlertSettingsRow> allSettings = settingsDao.getAllSettings();
            model.addAttribute("allSettings", allSettings);
        } catch (Exception e) {
            logger.error("Unable to fetch all settings!");
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/alerts/alerts";
    }

    @GetMapping("/create")
    public String createNew(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "text.alerts.new.title");
        if (!model.containsAttribute("alertSettingsModel")) {
            model.addAttribute("alertSettingsModel", new AlertSettingsModel());
        }
        return "settings/alerts/alerts-create";
    }

    @PostMapping("/savenew")
    public String saveNew(HttpServletRequest request, Model model, @ModelAttribute AlertSettingsModel settings,
            BindingResult bindingResult) {
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Unable to save new alert settings, validation failed!");
                addErrorMessage(request, "text.error.systemerror");
                return createNew(request, model);
            }
        } catch (Exception e) {
            logger.error("Unable to save new alert settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/alerts/";
    }
}
