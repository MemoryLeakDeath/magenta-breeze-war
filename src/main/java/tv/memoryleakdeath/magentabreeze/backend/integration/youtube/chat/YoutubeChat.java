package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.io.Serializable;

public class YoutubeChat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageText;
    private String authorThumbnailUrl;
    private String authorThumbnailWidth;
    private String authorThumbnailHeight;
    private String authorName;
    private String authorBadgeType;
    private String authorBadgeLabel;
    private String id;
    private Long timestampUtc;
    private String actionType;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getAuthorThumbnailUrl() {
        return authorThumbnailUrl;
    }

    public void setAuthorThumbnailUrl(String authorThumbnailUrl) {
        this.authorThumbnailUrl = authorThumbnailUrl;
    }

    public String getAuthorThumbnailWidth() {
        return authorThumbnailWidth;
    }

    public void setAuthorThumbnailWidth(String authorThumbnailWidth) {
        this.authorThumbnailWidth = authorThumbnailWidth;
    }

    public String getAuthorThumbnailHeight() {
        return authorThumbnailHeight;
    }

    public void setAuthorThumbnailHeight(String authorThumbnailHeight) {
        this.authorThumbnailHeight = authorThumbnailHeight;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorBadgeType() {
        return authorBadgeType;
    }

    public void setAuthorBadgeType(String authorBadgeType) {
        this.authorBadgeType = authorBadgeType;
    }

    public String getAuthorBadgeLabel() {
        return authorBadgeLabel;
    }

    public void setAuthorBadgeLabel(String authorBadgeLabel) {
        this.authorBadgeLabel = authorBadgeLabel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Long timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
