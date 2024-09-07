package tv.memoryleakdeath.magentabreeze.conf.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import tv.memoryleakdeath.magentabreeze.backend.dao.VersionDao;

@Component
public class VersionUpdateRunner {

    @Autowired
    @Qualifier("appSourceVersion")
    private String sourceVersion;

    @Autowired
    private VersionDao versionDao;

    @EventListener(ContextRefreshedEvent.class)
    public void updateVersion() {
        versionDao.setVersion(sourceVersion);
    }
}
