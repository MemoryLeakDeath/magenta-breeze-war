package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        try {
            List<AlertSettingsRow> allActiveSettings = settingsDao.getAllActiveSettings();
            model.addAttribute("allActiveSettings", allActiveSettings);
        } catch (Exception e) {
            logger.error("Unable to fetch all active settings!");
        }
        return "settings/alerts/alerts";
    }
}
