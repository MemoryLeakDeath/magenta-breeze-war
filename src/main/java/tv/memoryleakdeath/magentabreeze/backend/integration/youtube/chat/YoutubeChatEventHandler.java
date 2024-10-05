package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class YoutubeChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeChatEventHandler.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    @EventListener
    @Async
    public void parseChatEvent(ParseYoutubeChatEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        YoutubeChatMessageEvent messageEvent = new YoutubeChatMessageEvent();
        try {
            JsonNode rootNode = mapper.readTree(event.getChatJsonString());
            if (rootNode.has("continuationContents")) {
                processContinuationContents(rootNode.get("continuationContents"), messageEvent);
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse youtube chat event with contents: {}", event.getChatJsonString());
        }

        // send chat message event if any chat message was parsed
        if (messageEvent.getChatMessage() != null && messageEvent.getChatMessage().length() > 0) {
            sendChatEvent(messageEvent);
        }
    }

    private void processContinuationContents(final JsonNode node, YoutubeChatMessageEvent messageEvent) {
        if (node.has("liveChatContinuation")) {
            JsonNode chatContinuation = node.get("liveChatContinuation");
            if (chatContinuation.has("actions")) {
                processChatActions(chatContinuation.get("actions"), messageEvent);
            }
        }
    }

    private void processChatActions(final JsonNode actionsNode, YoutubeChatMessageEvent messageEvent) {
        actionsNode.elements().forEachRemaining(actionItemNode -> {
            if (actionItemNode.has("addChatItemAction")) {
                // search for message node and process it
                List<JsonNode> messageNodesList = actionItemNode.findValues("message");
                if (!messageNodesList.isEmpty()) {
                    processChatMessage(messageNodesList.get(0), messageEvent);
                } else {
                    logger.debug("no message nodes found");
                }

                // search for author name node and process it
                List<JsonNode> authorNameNodesList = actionItemNode.findValues("authorName");
                if (!authorNameNodesList.isEmpty()) {
                    processAuthorName(authorNameNodesList.get(0), messageEvent);
                } else {
                    logger.debug("no author name node found");
                }

                // search for author photo node and process it
                List<JsonNode> authorPhotoNodesList = actionItemNode.findValues("authorPhoto");
                if (!authorPhotoNodesList.isEmpty()) {
                    processAuthorPhoto(authorPhotoNodesList.get(0), messageEvent);
                } else {
                    logger.debug("no author photo node found");
                }

                // search for author badges and process it
                List<JsonNode> authorBadgesNodesList = actionItemNode.findValues("authorBadges");
                if (!authorBadgesNodesList.isEmpty()) {
                    processAuthorBadge(authorBadgesNodesList, messageEvent);
                }

                // search for timestamp and process it
                List<JsonNode> timestampNodes = actionItemNode.findValues("timestampUsec");
                if (!timestampNodes.isEmpty()) {
                    processTimestamp(timestampNodes.get(0), messageEvent);
                } else {
                    logger.debug("no timestamp node found");
                }
            }
        });
    }

    private void processChatMessage(final JsonNode chatMessageNode, YoutubeChatMessageEvent messageEvent) {
        List<JsonNode> messageNodes = chatMessageNode.findValues("text");
        StringBuilder message = new StringBuilder("");
        messageNodes.forEach(n -> {
            message.append(n.textValue());
        });
        messageEvent.setChatMessage(message.toString());
    }

    private void processAuthorName(final JsonNode authorNameNode, YoutubeChatMessageEvent messageEvent) {
        List<JsonNode> nameNodes = authorNameNode.findValues("simpleText");
        if (!nameNodes.isEmpty()) {
            messageEvent.setAuthorName(nameNodes.get(0).textValue());
        } else {
            logger.debug("no simpletext node found under author name");
        }
    }

    private void processAuthorPhoto(final JsonNode authorPhotoNode, YoutubeChatMessageEvent messageEvent) {
        List<JsonNode> urlNodes = authorPhotoNode.findValues("url");
        if (!urlNodes.isEmpty()) {
            messageEvent.setAuthorThumbnail(urlNodes.get(0).textValue());
        } else {
            logger.debug("no url node found under author photo");
        }
    }

    private void processAuthorBadge(final List<JsonNode> authorBadgeNodes, YoutubeChatMessageEvent messageEvent) {
        List<String> badgeTypes = new ArrayList<>();
        for (JsonNode authorBadgeNode : authorBadgeNodes) {
            List<JsonNode> badgeTypeNodes = authorBadgeNode.findValues("iconType");
            if (!badgeTypeNodes.isEmpty()) {
                badgeTypes.add(badgeTypeNodes.get(0).textValue());
            } else {
                logger.debug("no icontype nodes found under author badges");
            }
        }
        messageEvent.setAuthorBadges(badgeTypes);
    }

    private void processTimestamp(final JsonNode timestampNode, YoutubeChatMessageEvent messageEvent) {
        String timestampString = timestampNode.textValue();
        if (timestampString.length() > 0) {
            try {
                messageEvent.setTimestamp(Long.parseLong(timestampString));
            } catch (Exception e) {
                logger.error("Unable to parse timestamp from youtube chat message", e);
            }
        }
    }

    private void sendChatEvent(YoutubeChatMessageEvent messageEvent) {
        messageEvent.setEventId(UUID.randomUUID().toString());
        publisher.publishEvent(messageEvent);
    }
}
