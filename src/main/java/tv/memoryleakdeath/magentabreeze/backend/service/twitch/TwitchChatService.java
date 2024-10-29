package tv.memoryleakdeath.magentabreeze.backend.service.twitch;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.twitch4j.eventsub.domain.chat.Emote.Format;
import com.github.twitch4j.eventsub.domain.chat.Fragment;
import com.github.twitch4j.eventsub.domain.chat.Fragment.Type;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.helix.domain.ChatBadgeSetList;
import com.github.twitch4j.helix.domain.EmoteList;
import com.github.twitch4j.helix.domain.UserList;

import tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat.TwitchChatMessageEvent;
import tv.memoryleakdeath.magentabreeze.backend.service.AccountService;
import tv.memoryleakdeath.magentabreeze.common.OAuthTokenTypes;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.Account;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;
import tv.memoryleakdeath.magentabreeze.util.TwitchUtil;

@Service
public class TwitchChatService {
    private static final Logger logger = LoggerFactory.getLogger(TwitchChatService.class);
    private static final String DATE_FORMAT_STRING = "MMM dd hh:mm:ss a";
    private static SimpleDateFormat dateFormat = null;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TwitchUtil twitchUtil;

    private Map<String, String> emoteCacheMap = new HashMap<>();
    private String emoteCDNUrlTemplate;
    private Map<String, String> badgeCacheMap = new HashMap<>();
    private Map<String, String> chatterThumbnailCacheMap = new HashMap<>();

    public void attachChat(Locale locale) {
        logger.info("Attaching to twitch chat...");
        initDateFormatter(locale);
        String userId = getTwitchChatUserId();
        String accessToken = getAccessToken();
        Assert.notNull(accessToken, "Twitch Access Token is null!");
        populateGlobalEmotesIntoCache(accessToken);
        populateGlobalBadgesIntoCache(accessToken);
        IEventSubConduit conduit = twitchUtil.getConduit();
        conduit.register(SubscriptionTypes.CHANNEL_CHAT_MESSAGE,
                c -> c.broadcasterUserId(userId).userId(userId).build());
        conduit.getEventManager().onEvent(ChannelChatMessageEvent.class, handleChatMessages());
        logger.info("Twitch chat conduit established.");
    }

    private String getTwitchChatUserId() {
        Account account = accountService.getTwitchChatAccount();
        return account.getServiceUserId();
    }

    private String getAccessToken() {
        int accountId = accountService.getTwitchChatAccountId();
        return SecureStorageUtil.getOAuthTokenFromSecureStorage(ServiceTypes.TWITCH, OAuthTokenTypes.ACCESSTOKEN,
                accountId, resourceLoader);
    }

    private void populateGlobalEmotesIntoCache(String accessToken) {
        EmoteList list = twitchUtil.getClient().getHelix().getGlobalEmotes(accessToken).execute();
        emoteCDNUrlTemplate = list.getTemplate();
        list.getEmotes().forEach(e -> {
            String url = list.getPopulatedTemplateUrl(e.getId(), e.getFormat().getFirst(), e.getThemeMode().getFirst(),
                    e.getScale().getFirst());
            String name = e.getName();
            emoteCacheMap.put(name, url);
        });
        logger.info("Populated {} global emotes into cache", emoteCacheMap.size());
    }

    private String buildEmoteUrl(String id, String format, String theme, String size) {
        return StringUtils.replaceEach(emoteCDNUrlTemplate,
                new String[] { "{{id}}", "{{format}}", "{{theme_mode}}", "{{scale}}" },
                new String[] { id, format.toLowerCase(), theme.toLowerCase(), size });
    }

    private void populateGlobalBadgesIntoCache(String accessToken) {
        ChatBadgeSetList list = twitchUtil.getClient().getHelix().getGlobalChatBadges(accessToken).execute();
        list.getBadgeSets().forEach(set -> {
            set.getVersions().forEach(b -> {
                badgeCacheMap.put(b.getId(), b.getMediumImageUrl());
            });
        });
        logger.info("Populated {} global badges into cache", badgeCacheMap.size());
    }

    private Consumer<ChannelChatMessageEvent> handleChatMessages() {
        return e -> {
            TwitchChatMessageEvent chatMessageEvent = new TwitchChatMessageEvent();
            List<Fragment> messageFragments = e.getMessage().getFragments();
            messageFragments.forEach(f -> {
                switch (f.getType()) {
                case Type.EMOTE:
                    if (!emoteCacheMap.containsKey(f.getText())) {
                        String id = f.getEmote().getId();
                        String format = f.getEmote().getFormat().stream().findFirst().orElse(Format.STATIC).name();
                        String url = buildEmoteUrl(id, format, "dark", "2.0");
                        emoteCacheMap.put(f.getText(), url);
                    }
                    break;
                default:
                    break;
                }
            });
            chatMessageEvent.setEmoteMap(emoteCacheMap);
            chatMessageEvent.setChatMessage(e.getMessage().getText());
            chatMessageEvent.setAuthorBadges(
                    e.getBadges().stream().map(b -> badgeCacheMap.getOrDefault(b.getId(), b.getId())).toList());
            chatMessageEvent.setAuthorName(e.getChatterUserName());
            chatMessageEvent.setAuthorThumbnail(getChatterThumbnailUrl(e.getChatterUserId()));
            setChatTimestamp(chatMessageEvent);
            sendChatEvent(chatMessageEvent);
        };
    }

    private String getChatterThumbnailUrl(String chatterUserId) {
        if (chatterThumbnailCacheMap.containsKey(chatterUserId)) {
            return chatterThumbnailCacheMap.get(chatterUserId);
        }
        UserList users = twitchUtil.getClient().getHelix().getUsers(getAccessToken(), List.of(chatterUserId), null)
                .execute();
        String imageUrl = users.getUsers().getFirst().getProfileImageUrl();
        chatterThumbnailCacheMap.put(chatterUserId, imageUrl);
        return imageUrl;
    }

    private void initDateFormatter(Locale locale) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING, locale);
        }
    }

    private void setChatTimestamp(TwitchChatMessageEvent event) {
        long timestamp = Instant.now().toEpochMilli();
        event.setTimestamp(timestamp);
        event.setMessageDateTime(dateFormat.format(Date.from(Instant.ofEpochMilli(timestamp))));
    }

    private void sendChatEvent(TwitchChatMessageEvent event) {
        event.setEventId(UUID.randomUUID().toString());
        logger.debug("Sending twitch chat event: {}", event);
        publisher.publishEvent(event);
    }
}
