package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.io.Serializable;

public class AlertSettingsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alertText;
    private String type;
    private String service;

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
