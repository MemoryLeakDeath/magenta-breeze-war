package tv.memoryleakdeath.magentabreeze.frontend.alerts;

import java.io.Serializable;
import java.util.List;

import tv.memoryleakdeath.magentabreeze.common.pojo.Account;

public class AlertSettingsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alertText;
    private String type;
    private String alertTextColor = "#ffffff";
    private Long id;
    private Integer alertTextSize = 48;
    private Long alertImageId;
    private Long alertSoundId;
    private List<Account> accounts;
    private List<Long> associatedAccountIds;

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

    public Integer getAlertTextSize() {
        return alertTextSize;
    }

    public void setAlertTextSize(Integer alertTextSize) {
        this.alertTextSize = alertTextSize;
    }

    public Long getAlertImageId() {
        return alertImageId;
    }

    public void setAlertImageId(Long alertImageId) {
        this.alertImageId = alertImageId;
    }

    public Long getAlertSoundId() {
        return alertSoundId;
    }

    public void setAlertSoundId(Long alertSoundId) {
        this.alertSoundId = alertSoundId;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Long> getAssociatedAccountIds() {
        return associatedAccountIds;
    }

    public void setAssociatedAccountIds(List<Long> associatedAccountIds) {
        this.associatedAccountIds = associatedAccountIds;
    }

}
