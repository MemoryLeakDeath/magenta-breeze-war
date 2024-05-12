package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.io.Serializable;

public class AlertSettingsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alertText;
    private String type;
    private String service;
    private String alertTextColor = "#ffffff";
    private Long id;

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

    public String getAlertTextColor() {
        return alertTextColor;
    }

    public void setAlertTextColor(String alertTextColor) {
        this.alertTextColor = alertTextColor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
