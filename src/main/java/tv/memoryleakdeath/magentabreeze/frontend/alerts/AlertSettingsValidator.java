package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AlertSettingsValidator<T extends AlertSettingsModel> {

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (StringUtils.isBlank(target.getAlertText())) {
            errors.rejectValue("alertText", "text.alerts.error.alerttextrequired");
        }

        if (StringUtils.isBlank(target.getAlertTextColor())) {
            errors.rejectValue("alertTextColor", "text.alerts.error.alerttextcolorrequired");
        }
    }
}
