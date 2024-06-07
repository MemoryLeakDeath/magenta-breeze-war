package tv.memoryleakdeath.magentabreeze.common;

import java.util.List;

public class ContentTypeConstants {
    public static final List<String> IMAGE_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/gif",
            "image/svg+xml");
    public static final List<String> AUDIO_CONTENT_TYPES = List.of("audio/mpeg", "audio/ogg", "audio/wav",
            "audio/webm", "audio/flac", "audio/aac", "audio/mp4");

    private ContentTypeConstants() {
    }
}
