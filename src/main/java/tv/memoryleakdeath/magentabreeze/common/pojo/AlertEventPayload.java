package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

public class AlertEventPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long eventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
