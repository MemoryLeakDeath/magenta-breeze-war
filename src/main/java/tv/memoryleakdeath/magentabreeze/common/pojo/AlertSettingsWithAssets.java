package tv.memoryleakdeath.magentabreeze.common.pojo;

public class AlertSettingsWithAssets extends AlertSettingsRow {
    private static final long serialVersionUID = 1L;

    public AlertSettingsWithAssets() {
    }

    public AlertSettingsWithAssets(AlertSettingsRow row) {
        super.setId(row.getId());
        super.setType(row.getType());
        super.setService(row.getService());
        super.setActive(row.isActive());
        super.setSettings(row.getSettings());
        super.setCreated(row.getCreated());
        super.setUpdated(row.getUpdated());
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
}
