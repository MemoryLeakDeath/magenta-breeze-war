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
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AlertSettingsDao;
import tv.memoryleakdeath.magentabreeze.common.AlertTypeConstants;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettings;
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
        model.addAttribute("alertTypes", new AlertTypeConstants());
        model.addAttribute("serviceTypes", ServiceTypes.values());
        model.addAttribute("service", ServiceTypes.TWITCH);
        return "settings/alerts/alerts-create";
    }

    @PostMapping("/savenew")
    public String saveNew(HttpServletRequest request, Model model,
            @ModelAttribute AlertSettingsModel settings,
            BindingResult bindingResult) {
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Unable to save new alert settings, validation failed!");
                addErrorMessage(request, "text.error.systemerror");
                return createNew(request, model);
            }
            boolean success = settingsDao.createSettings(buildSettingsObject(settings));
            if (!success) {
                logger.error("Unable to save new alert settings!");
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.alerts.success.created");
            }
        } catch (Exception e) {
            logger.error("Unable to save new alert settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/alerts/";
    }

    private AlertSettingsRow buildSettingsObject(AlertSettingsModel model) {
        AlertSettingsRow row = new AlertSettingsRow();
        row.setActive(true);
        row.setService(ServiceTypes.valueOf(model.getService()));
        row.setType(AlertTypeConstants.getType(model.getType()));
        AlertSettings settings = new AlertSettings();
        settings.setAlertText(model.getAlertText());
        settings.setAlertTextColor(model.getAlertTextColor());
        row.setSettings(settings);
        return row;
    }

    private AlertSettingsRow buildSettingsObjectForUpdate(AlertSettingsModel model) {
        AlertSettingsRow row = buildSettingsObject(model);
        row.setId(model.getId());
        return row;
    }

    @PostMapping("/types")
    public String getTypes(HttpServletRequest request, Model model,
            @RequestParam(value = "service", required = true) String service) {
        model.addAttribute("alertSettingsModel", new AlertSettingsModel());
        model.addAttribute("service", ServiceTypes.valueOf(service));
        model.addAttribute("alertTypes", new AlertTypeConstants());
        return "settings/alerts/alerts-types-ajax";
    }

    @PostMapping("/edit")
    public String edit(HttpServletRequest request, Model model, @RequestParam(name = "id", required = true) Long id) {
        setPageTitle(request, model, "text.alerts.edit.title");
        try {
            if (!model.containsAttribute("alertSettingsModel")) {
                AlertSettingsRow row = settingsDao.getSettingsById(id);
                model.addAttribute("alertSettingsModel", buildModelObject(row));
                model.addAttribute("service", row.getService());
            }
        } catch (Exception e) {
            addErrorMessage(request, "text.error.systemerror");
            logger.error("Unable to edit alert with id: " + id, e);
        }
        model.addAttribute("alertTypes", new AlertTypeConstants());
        model.addAttribute("serviceTypes", ServiceTypes.values());
        return "settings/alerts/alerts-edit";
    }

    private AlertSettingsModel buildModelObject(AlertSettingsRow row) {
        AlertSettingsModel model = new AlertSettingsModel();
        if (row.getSettings() != null) {
            model.setAlertText(row.getSettings().getAlertText());
            model.setAlertTextColor(row.getSettings().getAlertTextColor());
        }
        model.setService(row.getService().name());
        model.setType(row.getType().toString());
        model.setId(row.getId());
        return model;
    }

    @PostMapping("/update")
    public String update(HttpServletRequest request, Model model, @ModelAttribute AlertSettingsModel settings,
            BindingResult bindingResult) {
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Unable to edit alert settings, validation failed!");
                addErrorMessage(request, "text.error.systemerror");
                return edit(request, model, settings.getId());
            }
            boolean success = settingsDao.updateSettings(buildSettingsObjectForUpdate(settings));
            if (!success) {
                logger.error("Unable to edit alert settings!");
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.alerts.success.edited");
            }
        } catch (Exception e) {
            logger.error("Unable to edit alert settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/alerts/";
    }

}
