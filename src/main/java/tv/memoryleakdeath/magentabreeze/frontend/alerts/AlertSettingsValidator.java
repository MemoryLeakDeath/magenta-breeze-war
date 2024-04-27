package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AlertSettingsValidator<T extends AlertSettingsModel> {

    public void validate(HttpServletRequest request, T target, Errors errors) {
        errors.rejectValue("alertText", "text.alerts.error.required");
    }
}
