package tv.memoryleakdeath.magentabreeze.conf.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;

@Component
public class SecureStorageRunner {
    private static final Logger logger = LoggerFactory.getLogger(SecureStorageRunner.class);

    @Autowired
    private ResourceLoader loader;

    @EventListener(ContextStartedEvent.class)
    @Order(1)
    public void rotateEncryption() {
        logger.info("Rotating secure storage encryption keys.");
        SecureStorageUtil.rotateSecureStorage(loader);
    }
}
