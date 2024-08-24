package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

public class ChatEventPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private String chatMessage;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
