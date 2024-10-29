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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat.TwitchChatMessageEvent;
import tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat.YoutubeChatMessageEvent;

/**
 * The ChatEventsService class is responsible for managing Server-Sent Events
 * (SSE) emitters and sending chat message events to them.
 * 
 * This class maintains a list of SseEmitter objects, which represent clients
 * that are listening for events. When an alert event is received, it is sent to
 * all registered emitters.
 * 
 * If an emitter completes or times out, it is removed from the list of active
 * emitters. If an error occurs while sending an event to an emitter, the
 * emitter is completed with the error and removed from the list.
 */
@Service
public class ChatEventsService {
    private static final Logger logger = LoggerFactory.getLogger(ChatEventsService.class);

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
            listenerEventList.remove(emitter);
        });
    }

    @EventListener
    @Async
    public void sendYoutubeChatEvent(YoutubeChatMessageEvent event) {
        sendChatEvent(getJsonString(event), event.getEventId());
    }

    @EventListener
    @Async
    public void sendTwitchChatEvent(TwitchChatMessageEvent event) {
        sendChatEvent(getJsonString(event), event.getEventId());
    }

    private <T> String getJsonString(T event) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert payload to json!", e);
        }
        return null;
    }

    private void sendChatEvent(String data, String eventId) {
        List<SseEmitter> failedEmitters = new ArrayList<>();
        listenerEventList.forEach(emitter -> {
            logger.debug("Sending chat event to emitter for event id: {}", eventId);
            SseEmitter.SseEventBuilder event = SseEmitter.event().name("trigger-chat").data(data);
            try {
                emitter.send(event);
            } catch (IOException e) {
                logger.error("Unable to send event id: %s to emitter!".formatted(eventId), e);
                emitter.completeWithError(e);
                failedEmitters.add(emitter);
            }
        });
        listenerEventList.removeAll(failedEmitters);
    }

}
