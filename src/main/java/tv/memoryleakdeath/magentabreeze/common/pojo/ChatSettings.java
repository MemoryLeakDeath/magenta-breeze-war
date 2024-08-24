package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

public class ChatSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatTextColor;
    private Integer chatTextSize = 48;
    private String chatFont;
    private boolean showServiceIcon = true;
    private boolean forceSmallEmotes = true;

    public String getChatTextColor() {
        return chatTextColor;
    }

    public void setChatTextColor(String chatTextColor) {
        this.chatTextColor = chatTextColor;
    }

    public Integer getChatTextSize() {
        return chatTextSize;
    }

    public void setChatTextSize(Integer chatTextSize) {
        this.chatTextSize = chatTextSize;
    }

    public String getChatFont() {
        return chatFont;
    }

    public void setChatFont(String chatFont) {
        this.chatFont = chatFont;
    }

    public boolean isShowServiceIcon() {
        return showServiceIcon;
    }

    public void setShowServiceIcon(boolean showServiceIcon) {
        this.showServiceIcon = showServiceIcon;
    }

    public boolean isForceSmallEmotes() {
        return forceSmallEmotes;
    }

    public void setForceSmallEmotes(boolean forceSmallEmotes) {
        this.forceSmallEmotes = forceSmallEmotes;
    }
}
