package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String service;
    private boolean chatOnly;
    private String stateKey;
    private Date created;
    private Date updated;
    private String displayName;
    private boolean stateKeyExpired;
    private String profileUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public boolean isChatOnly() {
        return chatOnly;
    }

    public void setChatOnly(boolean chatOnly) {
        this.chatOnly = chatOnly;
    }

    public String getStateKey() {
        return stateKey;
    }

    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isStateKeyExpired() {
        return stateKeyExpired;
    }

    public void setStateKeyExpired(boolean stateKeyExpired) {
        this.stateKeyExpired = stateKeyExpired;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
