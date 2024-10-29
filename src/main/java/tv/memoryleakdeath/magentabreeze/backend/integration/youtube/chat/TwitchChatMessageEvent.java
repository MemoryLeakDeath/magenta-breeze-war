package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchChatMessageEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatMessage;
    private String authorName;
    private List<String> authorBadges;
    private String authorThumbnail;
    private String eventId;
    private Map<String, String> emoteMap = new HashMap<>();
    private Long timestamp;
    private String messageDateTime;

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

    public List<String> getAuthorBadges() {
        return authorBadges;
    }

    public void setAuthorBadges(List<String> authorBadges) {
        this.authorBadges = authorBadges;
    }

    public String getAuthorThumbnail() {
        return authorThumbnail;
    }

    public void setAuthorThumbnail(String authorThumbnail) {
        this.authorThumbnail = authorThumbnail;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map<String, String> getEmoteMap() {
        return emoteMap;
    }

    public void setEmoteMap(Map<String, String> emoteMap) {
        this.emoteMap = emoteMap;
    }

    @Override
    public String toString() {
        return "TwitchChatMessageEvent [chatMessage=" + chatMessage + ", authorName=" + authorName + ", authorBadges="
                + authorBadges + ", authorThumbnail=" + authorThumbnail + ", eventId=" + eventId + ", emoteMap="
                + emoteMap + "]";
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageDateTime() {
        return messageDateTime;
    }

    public void setMessageDateTime(String messageDateTime) {
        this.messageDateTime = messageDateTime;
    }
}
