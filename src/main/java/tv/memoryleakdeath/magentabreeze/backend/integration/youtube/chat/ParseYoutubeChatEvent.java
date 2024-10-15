package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.io.Serializable;
import java.util.Locale;

public class ParseYoutubeChatEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatJsonString;
    private Locale locale;

    public String getChatJsonString() {
        return chatJsonString;
    }

    public void setChatJsonString(String chatJsonString) {
        this.chatJsonString = chatJsonString;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
