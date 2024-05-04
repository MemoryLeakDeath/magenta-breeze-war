package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

import tv.memoryleakdeath.magentabreeze.common.AlertTypeConstants;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;

public class AlertSettingsRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private ServiceTypes service;
    private AlertTypeConstants type;
    private boolean active;
    private AlertSettings settings;

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

    public ServiceTypes getService() {
        return service;
    }

    public void setService(ServiceTypes service) {
        this.service = service;
    }

    public AlertTypeConstants getType() {
        return type;
    }

    public void setType(AlertTypeConstants type) {
        this.type = type;
    }
}
