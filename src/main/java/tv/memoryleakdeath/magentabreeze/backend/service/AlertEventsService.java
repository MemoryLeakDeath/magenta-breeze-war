package tv.memoryleakdeath.magentabreeze.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import tv.memoryleakdeath.magentabreeze.common.pojo.AlertEventPayload;

@Service
public class AlertEventsService {
    private static final Logger logger = LoggerFactory.getLogger(AlertEventsService.class);

    private final List<SseEmitter> listenerEventList = new CopyOnWriteArrayList<>();

    public void addListener(SseEmitter emitter) {
        listenerEventList.add(emitter);

        emitter.onCompletion(() -> {
            logger.debug("Emitter completed!");
            listenerEventList.remove(emitter);
        });

        emitter.onTimeout(() -> {
            logger.debug("Emitter timed out!");
            emitter.complete();
        });
    }

    @EventListener
    @Async
    public void sendAlert(AlertEventPayload payload) {
        List<SseEmitter> failedEmitters = new ArrayList<>();
        listenerEventList.forEach(emitter -> {
            logger.debug("Sending alert to emitter for event id: {}", payload.getEventId());
            SseEmitter.SseEventBuilder event = SseEmitter.event().name("trigger-alert")
                    .data(String.valueOf(payload.getEventId()));
            try {
                emitter.send(event);
            } catch (IOException e) {
                logger.error("Unable to send event id: %s to emitter!".formatted(payload.getEventId()), e);
                emitter.completeWithError(e);
                failedEmitters.add(emitter);
            }
        });
        listenerEventList.removeAll(failedEmitters);
    }
}
