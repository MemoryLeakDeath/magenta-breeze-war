package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.io.Serializable;
import java.util.List;

public class YoutubeChatMessageEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String chatMessage;
    private String authorName;
    private List<String> authorBadges;
    private String authorThumbnail;
    private String eventId;
    private Long timestamp;

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorThumbnail() {
        return authorThumbnail;
    }

    public void setAuthorThumbnail(String authorThumbnail) {
        this.authorThumbnail = authorThumbnail;
    }

    public List<String> getAuthorBadges() {
        return authorBadges;
    }

    public void setAuthorBadges(List<String> authorBadges) {
        this.authorBadges = authorBadges;
    }

    @Override
    public String toString() {
        return "YoutubeChatMessageEvent [chatMessage=" + chatMessage + ", authorName=" + authorName + ", authorBadges="
                + authorBadges + ", authorThumbnail=" + authorThumbnail + "]";
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}