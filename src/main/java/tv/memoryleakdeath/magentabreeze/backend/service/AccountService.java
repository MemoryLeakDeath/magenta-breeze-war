package tv.memoryleakdeath.magentabreeze.backend.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.github.twitch4j.helix.domain.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ChannelSnippet;

import tv.memoryleakdeath.magentabreeze.backend.dao.AccountsDao;
import tv.memoryleakdeath.magentabreeze.common.OAuthTokenTypes;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.Account;
import tv.memoryleakdeath.magentabreeze.util.SecureStorageUtil;
import tv.memoryleakdeath.magentabreeze.util.TwitchUtil;
import tv.memoryleakdeath.magentabreeze.util.YoutubeUtil;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TwitchUtil twitchUtil;

    @Autowired
    private YoutubeUtil youtubeUtil;

    public boolean createAccount(String accessToken, String refreshToken, String service, Long expiresIn) {
        boolean success = false;
        if (ServiceTypes.TWITCH.name().equals(service)) {
            logger.debug("Starting creation process for linked twitch user...");
            success = createTwitchLinkedAccount(accessToken);
        } else if (ServiceTypes.YOUTUBE.name().equals(service)) {
            logger.debug("Starting creation process for linked youtube user...");
            success = createYoutubeLinkedAccount(accessToken, refreshToken, expiresIn);
        }
        return success;
    }

    public boolean createAccount(String accessToken, String service) {
        return createAccount(accessToken, null, service, null);
    }

    public boolean updateAccount(String accessToken, String refreshToken, String service, Long expiresIn,
            Integer accountId) {
        boolean success = false;
        Account account = accountsDao.getAccountById(accountId);
        if (ServiceTypes.TWITCH.name().equals(service)) {
            logger.debug("Starting update process for linked twitch user...");
            success = updateTwitchLinkedAccount(account, accessToken);
        } else if (ServiceTypes.YOUTUBE.name().equals(service)) {
            logger.debug("Starting update process for linked youtube user...");
            success = updateYoutubeLinkedAccount(account, accessToken, refreshToken, expiresIn);
        }
        return success;
    }

    public boolean updateAccount(String accessToken, String service, Integer accountId) {
        return updateAccount(accessToken, null, service, null, accountId);
    }

    private Account buildAccountObject(ServiceTypes service, User twitchUser) {
        Account account = new Account();
        account.setService(service.name());
        account.setDisplayName(twitchUser.getDisplayName());
        account.setProfileUrl(twitchUser.getProfileImageUrl());
        account.setServiceUserId(twitchUser.getId());
        return account;
    }

    private boolean createTwitchLinkedAccount(String accessToken) {
        boolean success = false;
        User twitchUser = twitchUtil.getTwitchLoggedInUser(accessToken);
        Account account = buildAccountObject(ServiceTypes.TWITCH, twitchUser);
        if (!accountsDao.createAccount(account)) {
            logger.error("Unable to create account for linkage!");
        } else {
            SecureStorageUtil.saveOAuthTokenInSecureStorage(accessToken, OAuthTokenTypes.ACCESSTOKEN,
                    ServiceTypes.TWITCH,
                    account.getId().intValue(), resourceLoader);
            success = true;
        }
        return success;
    }

    private boolean updateTwitchLinkedAccount(Account account, String accessToken) {
        boolean success = false;
        User twitchUser = twitchUtil.getTwitchLoggedInUser(accessToken);
        account.setDisplayName(twitchUser.getDisplayName());
        account.setProfileUrl(twitchUser.getProfileImageUrl());
        account.setServiceUserId(twitchUser.getId());
        if (!accountsDao.updateAccount(account)) {
            logger.error("Unable to update account linkage!");
        } else {
            SecureStorageUtil.saveOAuthTokenInSecureStorage(accessToken, OAuthTokenTypes.ACCESSTOKEN,
                    ServiceTypes.TWITCH, account.getId().intValue(), resourceLoader);
            success = true;
        }
        return success;
    }

    private boolean createYoutubeLinkedAccount(String accessToken, String refreshToken, Long expiresIn) {
        boolean success = false;
        Credential cred = youtubeUtil.buildCredential(accessToken, refreshToken, expiresIn);
        ChannelSnippet youtubeUser = getYoutubeLoggedInUser(cred);
        Account account = buildAccountObject(ServiceTypes.YOUTUBE, youtubeUser);
        if (!accountsDao.createAccount(account)) {
            logger.error("Unable to create account for linkage!");
        } else {
            SecureStorageUtil.saveOAuthTokenInSecureStorage(accessToken, OAuthTokenTypes.ACCESSTOKEN,
                    ServiceTypes.YOUTUBE,
                    account.getId().intValue(), resourceLoader);
            SecureStorageUtil.saveOAuthTokenInSecureStorage(refreshToken, OAuthTokenTypes.REFRESHTOKEN,
                    ServiceTypes.YOUTUBE,
                    account.getId().intValue(), resourceLoader);
            success = true;
        }
        return success;
    }

    private boolean updateYoutubeLinkedAccount(Account account, String accessToken, String refreshToken,
            Long expiresIn) {
        boolean success = false;
        Credential cred = youtubeUtil.buildCredential(accessToken, refreshToken, expiresIn);
        ChannelSnippet youtubeUser = getYoutubeLoggedInUser(cred);
        account.setDisplayName(youtubeUser.getTitle());
        account.setProfileUrl(youtubeUser.getThumbnails().getDefault().getUrl());
        if (!accountsDao.updateAccount(account)) {
            logger.error("Unable to update account linkage!");
        } else {
            SecureStorageUtil.saveOAuthTokenInSecureStorage(accessToken, OAuthTokenTypes.ACCESSTOKEN,
                    ServiceTypes.YOUTUBE, account.getId().intValue(), resourceLoader);
            SecureStorageUtil.saveOAuthTokenInSecureStorage(refreshToken, OAuthTokenTypes.REFRESHTOKEN,
                    ServiceTypes.YOUTUBE, account.getId().intValue(), resourceLoader);
            success = true;
        }
        return success;
    }

    private ChannelSnippet getYoutubeLoggedInUser(Credential cred) {
        YouTube yt = youtubeUtil.getService(cred);
        try {
            YouTube.Channels.List request = yt.channels().list(List.of("snippet"));
            ChannelListResponse response = request.setMine(true).execute();
            List<Channel> channelList = response.getItems();
            if (!channelList.isEmpty()) {
                return channelList.get(0).getSnippet();
            }
        } catch (IOException e) {
            logger.error("Unable to contact youtube to retrieve channel details!", e);
        }
        return null;
    }

    private Account buildAccountObject(ServiceTypes service, ChannelSnippet youtubeUser) {
        if (youtubeUser == null) {
            return null;
        }
        Account account = new Account();
        account.setService(service.name());
        account.setDisplayName(youtubeUser.getTitle());
        account.setProfileUrl(youtubeUser.getThumbnails().getDefault().getUrl());
        return account;
    }

    public Integer getTwitchChatAccountId() {
        return accountsDao.getTwitchChatAccount();
    }

    public Account getTwitchChatAccount() {
        Integer accountId = accountsDao.getTwitchChatAccount();
        return accountsDao.getAccountById(accountId);
    }

    public List<Account> getAllAccounts() {
        return accountsDao.getAllAccounts();
    }

    public boolean updateAccount(Account account) {
        return accountsDao.updateAccount(account);
    }

    public boolean deleteAccount(long accountId) {
        return accountsDao.deleteAccount(accountId);
    }

    public boolean updateChatOnlyFlag(long accountId, boolean flag) {
        return accountsDao.updateChatOnlyFlag(accountId, flag);
    }
}
