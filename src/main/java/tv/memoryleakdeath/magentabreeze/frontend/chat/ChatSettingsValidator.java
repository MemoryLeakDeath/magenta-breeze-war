package tv.memoryleakdeath.magentabreeze.frontend.chat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ChatSettingsValidator<T extends ChatSettingsModel> {

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (StringUtils.isBlank(target.getChatFont())) {
            errors.rejectValue("chatFont", "text.chat.error.chatfontrequired");
        }

        if (StringUtils.isBlank(target.getChatTextColor())) {
            errors.rejectValue("chatTextColor", "text.chat.error.chattextcolorrequired");
        }

        if (StringUtils.isBlank(target.getName())) {
            errors.rejectValue("name", "text.chat.error.namerequired");
        }
    }
}
