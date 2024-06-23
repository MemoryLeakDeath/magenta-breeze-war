package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String service;
    private boolean chatOnly;
    private Date created;
    private Date updated;
    private String displayName;
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

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
