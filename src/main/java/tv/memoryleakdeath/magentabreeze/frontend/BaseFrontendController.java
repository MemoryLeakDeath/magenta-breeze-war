package tv.memoryleakdeath.magentabreeze.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.common.pojo.PageMessages;

public class BaseFrontendController {
    public static final String PAGE_TITLE = "pageTitle";
    public static final String PAGE_MESSAGES = "pageMessages";
    public static final String CSS_SCRIPTS = "pageCSSScripts";
    public static final String JS_SCRIPTS = "pageJSScripts";
    public static final String HTMX_CONFIG = "htmxConfig";

    @Autowired
    @Qualifier("appSourceVersion")
    String applicationVersion;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    protected String getAppVersion() {
        return applicationVersion;
    }

    protected Locale getLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request);
    }

    protected String getMessage(HttpServletRequest request, String message, Object[] messageArgs) {
        return messageSource.getMessage(message, messageArgs, getLocale(request));
    }

    protected String getMessage(HttpServletRequest request, String message) {
        return getMessage(request, message, null);
    }

    protected void setPageTitle(HttpServletRequest request, Model model, String message, Object[] messageArgs) {
        model.addAttribute(PAGE_TITLE, getMessage(request, message, messageArgs));
    }

    protected void setPageTitle(HttpServletRequest request, Model model, String message) {
        setPageTitle(request, model, message, null);
    }

    @SuppressWarnings("unchecked")
    private <E> List<E> getOrCreateList(Model model, String attribute) {
        List<E> someList = Optional.ofNullable((List<E>) model.getAttribute(attribute)).orElseGet(ArrayList::new);
        model.addAttribute(attribute, someList);
        return someList;
    }

    protected void addPageCSS(Model model, String relativePath) {
        List<String> pageCSS = getOrCreateList(model, CSS_SCRIPTS);
        pageCSS.add(relativePath);
    }

    protected void addPageJS(Model model, String relativePath) {
        List<String> pageJS = getOrCreateList(model, JS_SCRIPTS);
        pageJS.add(relativePath);
    }

    @SuppressWarnings("unchecked")
    private PageMessages getMessageModel(HttpServletRequest request) {
        PageMessages messages = Optional.ofNullable((PageMessages) request.getSession().getAttribute(PAGE_MESSAGES))
                .orElseGet(PageMessages::new);
        request.getSession().setAttribute(PAGE_MESSAGES, messages);
        return messages;
    }

    protected void addErrorMessage(HttpServletRequest request, String messageKey) {
        PageMessages messages = getMessageModel(request);
        messages.addErrorMessage(messageKey);
    }

    protected void addSuccessMessage(HttpServletRequest request, String messageKey) {
        PageMessages messages = getMessageModel(request);
        messages.addSuccessMessage(messageKey);
    }

    protected void addInfoMessage(HttpServletRequest request, String messageKey) {
        PageMessages messages = getMessageModel(request);
        messages.addInfoMessage(messageKey);
    }

    protected void addWarningMessage(HttpServletRequest request, String messageKey) {
        PageMessages messages = getMessageModel(request);
        messages.addWarningMessage(messageKey);
    }

}
