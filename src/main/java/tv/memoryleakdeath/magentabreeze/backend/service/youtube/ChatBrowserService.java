package tv.memoryleakdeath.magentabreeze.backend.service.youtube;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class ChatBrowserService {
    private static final Logger logger = LoggerFactory.getLogger(ChatBrowserService.class);
    private static final String CHAT_BASE_URL = "https://studio.youtube.com/live_chat?is_popout=1&v=";
    private static final String CHAT_FETCH_URL = "**/get_live_chat?prettyPrint=false";
    private BrowserThread chatBrowserThread;

    @Autowired
    private ResourceLoader loader;

    public void launchChatBrowser() {
        chatBrowserThread = new BrowserThread(CHAT_BASE_URL + "1ZsNB8cSbo0", CHAT_FETCH_URL, loader);
        chatBrowserThread.start();
    }

    public void stopChatBrowser() {
        chatBrowserThread.setRunning(new AtomicBoolean(false));
    }
}
