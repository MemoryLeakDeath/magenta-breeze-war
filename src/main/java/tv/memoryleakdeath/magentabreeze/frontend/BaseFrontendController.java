package tv.memoryleakdeath.magentabreeze.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseFrontendController {

    @Autowired
    @Qualifier("appSourceVersion")
    String applicationVersion;

    protected String getAppVersion() {
        return applicationVersion;
    }
}
