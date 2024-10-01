package tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class YoutubeChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeChatEventHandler.class);

    @EventListener
    @Async
    public void parseChatEvent(ParseYoutubeChatEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(event.getChatJsonString());
            if (rootNode.has("continuationContents")) {
                processContinuationContents(rootNode.get("continuationContents"));
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse youtube chat event with contents: {}", event.getChatJsonString());
        }
    }

    private void processContinuationContents(final JsonNode node) {
        if (node.has("liveChatContinuation")) {
            JsonNode chatContinuation = node.get("liveChatContinuation");
            if (chatContinuation.has("actions")) {
                processChatActions(chatContinuation.get("actions"));
            }
        }
    }

    private void processChatActions(final JsonNode actionsNode) {
        actionsNode.elements().forEachRemaining(actionItemNode -> {
            if (actionItemNode.has("addChatItemAction")) {
                List<JsonNode> messageNodesList = actionItemNode.findValues("message");
                if (!messageNodesList.isEmpty()) {
                    processChatMessage(messageNodesList.get(0));
                } else {
                    logger.info("no message nodes found");
                }
            }
        });
    }

    private void processChatMessage(final JsonNode chatMessageNode) {
        List<JsonNode> messageNodes = chatMessageNode.findValues("text");
        messageNodes.forEach(n -> {
            logger.info("**[CHAT MESSAGE RECIEVED]**: {}", n.textValue());
        });
    }
}
