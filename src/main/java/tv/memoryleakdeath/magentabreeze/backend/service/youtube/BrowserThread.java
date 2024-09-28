package tv.memoryleakdeath.magentabreeze.backend.service.youtube;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserContext.WaitForConditionOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;

import okio.Path;

public class BrowserThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(BrowserThread.class);
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36";
    private AtomicBoolean running = new AtomicBoolean(true);
    private String url;
    private String interceptUrl;
    private ResourceLoader loader;

    public BrowserThread(String pageUrl, String interceptUrl, ResourceLoader loader) {
        this.url = pageUrl;
        this.interceptUrl = interceptUrl;
        this.loader = loader;
    }

    @Override
    public void run() {
        logger.debug("BrowserThread started....");
        try (Playwright playwright = Playwright.create()) {
            try (Browser chromeBrowser = playwright.chromium().launch()) {
                BrowserContext context = chromeBrowser.newContext(new NewContextOptions().setUserAgent(USER_AGENT));
                Page chatPage = context.newPage();
                chatPage.route(interceptUrl, new ChatRouteInterceptor());
                chatPage.navigate(url);
                chatPage.screenshot(
                        new Page.ScreenshotOptions().setPath(Paths.get(System.getProperty("java.io.tmpdir")
                                + Path.DIRECTORY_SEPARATOR + "mb-chat-screenshot.png")));
                WaitForConditionOptions waitOptions = new WaitForConditionOptions().setTimeout(20_000);

                while (running.get()) {
                    logger.info("Browser Thread running...");
                    try {
                        context.waitForCondition(() -> false, waitOptions);
                    } catch (TimeoutError e) {
                        // do nothing
                    }
                }
            }
        } catch (Exception e) {
            running.set(false);
            logger.error("Unable to navigate to chat page!", e);
        }
        logger.debug("BrowserThread stopped.");
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public void setRunning(AtomicBoolean running) {
        this.running = running;
    }
}
