package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class ChatSettingsRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private boolean active;
    private String name;
    private ChatSettings settings;
    private Date created;
    private Date updated;

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

    public ChatSettings getSettings() {
        return settings;
    }

    public void setSettings(ChatSettings settings) {
        this.settings = settings;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
