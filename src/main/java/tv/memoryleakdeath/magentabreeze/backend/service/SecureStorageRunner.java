package tv.memoryleakdeath.magentabreeze.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
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

    @EventListener(ContextRefreshedEvent.class)
    @Order(1)
    public void initAndRotateEncryption() {
        logger.info("Initializing secure storage files...");
        SecureStorageUtil.initResourceFiles(loader);
        logger.info("Rotating secure storage encryption keys.");
        SecureStorageUtil.rotateSecureStorage(loader);
    }
}
