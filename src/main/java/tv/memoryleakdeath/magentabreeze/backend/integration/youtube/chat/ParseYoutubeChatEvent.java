package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.io.Serializable;

public class ParseYoutubeChatEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatJsonString;

    public String getChatJsonString() {
        return chatJsonString;
    }

    public void setChatJsonString(String chatJsonString) {
        this.chatJsonString = chatJsonString;
    }

}
