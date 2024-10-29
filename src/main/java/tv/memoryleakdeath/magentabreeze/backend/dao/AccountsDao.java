package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.magentabreeze.backend.mapper.AccountMapper;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.Account;

@Repository
public class AccountsDao {
    private static final Logger logger = LoggerFactory.getLogger(AccountsDao.class);
    private static final String[] COLUMNS = { "id", "service", "chatonly", "created", "updated", "displayname",
            "profileurl", "serviceuserid" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ACCOUNTS_SQL = """
            select %s from accounts where chatonly = ? order by updated desc, created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<Account> getBroadcastAccounts() {
        return jdbcTemplate.query(GET_ACCOUNTS_SQL, new AccountMapper(), false);
    }

    public List<Account> getChatAccounts() {
        return jdbcTemplate.query(GET_ACCOUNTS_SQL, new AccountMapper(), true);
    }

    public Integer getTwitchChatAccount() {
        return runIdQuery(ServiceTypes.TWITCH, true);
    }

    public Integer getTwitchBroadcastAccount() {
        return runIdQuery(ServiceTypes.TWITCH, false);
    }

    public Integer getYoutubeChatAccount() {
        return runIdQuery(ServiceTypes.YOUTUBE, true);
    }

    public Integer getYoutubeBroadcastAccount() {
        return runIdQuery(ServiceTypes.YOUTUBE, false);
    }

    private static final String GET_ACCOUNT_ID_FOR_SERVICE = """
            select id from accounts where service = ? and chatonly = ?
            """;

    private Integer runIdQuery(ServiceTypes type, boolean chatAccount) {
        List<Integer> accounts = jdbcTemplate.queryForList(GET_ACCOUNT_ID_FOR_SERVICE, Integer.class,
                type.name(), chatAccount);
        return accounts.stream().findFirst().orElse(null);
    }

    private static final String GET_ALL_ACCOUNTS_SQL = """
            select %s from accounts order by updated desc, created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<Account> getAllAccounts() {
        return jdbcTemplate.query(GET_ALL_ACCOUNTS_SQL, new AccountMapper());
    }

    private static final String GET_ACCOUNT_BY_ID_SQL = """
            select %s from accounts where id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public Account getAccountById(Integer id) {
        List<Account> accounts = jdbcTemplate.query(GET_ACCOUNT_BY_ID_SQL, new AccountMapper(), id);
        return accounts.stream().findFirst().orElse(null);
    }

    private static final String GET_ACCOUNTS_FOR_ALERTS_SQL = """
            select %s from accounts inner join accountalertsettings on accounts.id = accountalertsettings.accountid
            where accountalertsettings.alertsettingsid = ?
            order by accounts.updated desc, accounts.created desc
            """.formatted(StringUtils.join(prefixedColumns(COLUMNS, "accounts"), ","));

    public List<Account> getAccountsForAlert(Long alertId) {
        return jdbcTemplate.query(GET_ACCOUNTS_FOR_ALERTS_SQL, new AccountMapper(), alertId);
    }

    @Transactional
    public boolean createAccount(Account account) {
        if (account == null) {
            return false;
        }
        String sql = "insert into accounts (service, chatonly, created, updated, displayname, profileurl, serviceuserid) values (?,?,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP(),?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getService());
            ps.setBoolean(2, account.isChatOnly());
            ps.setString(3, account.getDisplayName());
            ps.setString(4, account.getProfileUrl());
            if (account.getServiceUserId() != null) {
                ps.setString(5, account.getServiceUserId());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            return ps;
        }, keyHolder);
        if (rowsAffected > 0) {
            account.setId(keyHolder.getKey().longValue());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteAccount(Long id) {
        String sql = "delete from accounts where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean updateAccount(Account account) {
        String sql = "update accounts set service = ?, chatonly = ?, updated = CURRENT_TIMESTAMP(), displayname = ?, profileurl = ?, serviceuserid = ? where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, account.getService(), account.isChatOnly(),
                account.getDisplayName(), account.getProfileUrl(), account.getId(), account.getServiceUserId());
        return (rowsAffected > 0);
    }

    private static String[] prefixedColumns(String[] columns, String prefix) {
        String[] prefixed = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            prefixed[i] = prefix + "." + columns[i];
        }
        return prefixed;
    }
}
