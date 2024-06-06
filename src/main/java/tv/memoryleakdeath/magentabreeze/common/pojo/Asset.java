package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

import tv.memoryleakdeath.magentabreeze.util.UploadUtil;

public class Asset implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String originalFilename;
    private String contentType;
    private String description;
    private Date created;
    private Date updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contenttype) {
        this.contentType = contenttype;
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

    @Transient
    public boolean isImage() {
        return UploadUtil.isImageContentType(contentType);
    }

    @Transient
    public boolean isAudio() {
        return UploadUtil.isAudioContentType(contentType);
    }

    @Transient
    public String getUploadedFilename() {
        return UploadUtil.getUploadedFilename(id, originalFilename);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
