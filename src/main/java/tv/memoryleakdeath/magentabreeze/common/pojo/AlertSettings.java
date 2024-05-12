package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

public class AlertSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alertText;
    private String alertTextColor;

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }

    public String getAlertTextColor() {
        return alertTextColor;
    }

    public void setAlertTextColor(String alertTextColor) {
        this.alertTextColor = alertTextColor;
    }
}
