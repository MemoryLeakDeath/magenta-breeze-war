package tv.memoryleakdeath.magentabreeze.backend.service.youtube;

import java.util.Locale;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Route.FulfillOptions;

import tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat.ParseYoutubeChatEvent;

@Service
public class ChatRouteInterceptor implements Consumer<Route> {
    private static final Logger logger = LoggerFactory.getLogger(ChatRouteInterceptor.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    private Locale locale;

    @Override
    public void accept(Route route) {
        APIResponse response = route.fetch();
        ParseYoutubeChatEvent event = new ParseYoutubeChatEvent();
        event.setLocale(locale);
        event.setChatJsonString(response.text());
        publisher.publishEvent(event);
        route.fulfill(new FulfillOptions().setResponse(response));
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
