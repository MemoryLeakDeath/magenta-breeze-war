package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import tv.memoryleakdeath.magentabreeze.common.AlertTypeConstants;

public class AlertSettingsRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private AlertTypeConstants type;
    private boolean active;
    private AlertSettings settings;
    private Date created;
    private Date updated;
    private Long imageId;
    private Long soundId;
    private List<Account> accounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AlertSettings getSettings() {
        return settings;
    }

    public void setSettings(AlertSettings settings) {
        this.settings = settings;
    }

    public AlertTypeConstants getType() {
        return type;
    }

    public void setType(AlertTypeConstants type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getSoundId() {
        return soundId;
    }

    public void setSoundId(Long soundId) {
        this.soundId = soundId;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
