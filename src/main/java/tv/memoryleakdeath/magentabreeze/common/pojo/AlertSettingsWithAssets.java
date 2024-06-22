package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class AlertSettingsWithAssets extends AlertSettingsRow {
    private static final long serialVersionUID = 1L;

    private List<Account> accounts = new ArrayList<>();

    public AlertSettingsWithAssets(AlertSettingsRow row) {
        super.setId(row.getId());
        super.setType(row.getType());
        super.setActive(row.isActive());
        super.setSettings(row.getSettings());
        super.setCreated(row.getCreated());
        super.setUpdated(row.getUpdated());
        super.setAccounts(row.getAccounts());
    }

    private Asset alertImage;
    private Asset alertSound;

    public Asset getAlertImage() {
        return alertImage;
    }

    public void setAlertImage(Asset alertImage) {
        this.alertImage = alertImage;
    }

    public Asset getAlertSound() {
        return alertSound;
    }

    public void setAlertSound(Asset alertSound) {
        this.alertSound = alertSound;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
