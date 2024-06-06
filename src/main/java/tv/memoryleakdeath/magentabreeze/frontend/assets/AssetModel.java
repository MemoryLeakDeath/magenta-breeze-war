package tv.memoryleakdeath.magentabreeze.frontend.assets;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class AssetModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private MultipartFile asset;
    private String type;
    private String description;

    public MultipartFile getAsset() {
        return asset;
    }

    public void setAsset(MultipartFile asset) {
        this.asset = asset;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
