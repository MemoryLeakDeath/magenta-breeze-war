package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.io.Serializable;

public class AlertSettingsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alertText;

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }

}
