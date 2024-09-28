package tv.memoryleakdeath.magentabreeze.backend.service.youtube;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Route.FulfillOptions;

public class ChatRouteInterceptor implements Consumer<Route> {
    private static final Logger logger = LoggerFactory.getLogger(ChatRouteInterceptor.class);

    @Override
    public void accept(Route route) {
        logger.info("**URL INTERCEPTED!** {}", route.request().url());
        APIResponse response = route.fetch();
        String body = response.text();
        logger.info("Request body: {}", body);
        route.fulfill(new FulfillOptions().setResponse(response));
    }

}
