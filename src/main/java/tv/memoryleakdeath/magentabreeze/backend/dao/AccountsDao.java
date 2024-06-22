package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.magentabreeze.backend.mapper.AccountMapper;
import tv.memoryleakdeath.magentabreeze.common.pojo.Account;

@Repository
public class AccountsDao {
    private static final Logger logger = LoggerFactory.getLogger(AccountsDao.class);
    private static final String[] COLUMNS = { "id", "service", "chatonly", "statekey", "created", "updated",
            "displayname", "statekeyexpired", "profileurl" };

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

    private static final String GET_ALL_ACCOUNTS_SQL = """
            select %s from accounts order by updated desc, created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<Account> getAllAccounts() {
        return jdbcTemplate.query(GET_ALL_ACCOUNTS_SQL, new AccountMapper());
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
        String sql = "insert into accounts (service, chatonly, statekey, created, updated, displayname, statekeyexpired, profileurl) values (?,?,?,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP(),?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql, account.getService(), account.isChatOnly(), account.getStateKey(),
                account.getDisplayName(), account.isStateKeyExpired(), account.getProfileUrl());
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean deleteAccount(Long id) {
        String sql = "delete from accounts where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean updateAccount(Account account) {
        String sql = "update accounts set service = ?, chatonly = ?, statekey = ?, updated = CURRENT_TIMESTAMP(), displayname = ?, statekeyexpired = ?, profileurl = ? where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, account.getService(), account.isChatOnly(), account.getStateKey(),
                account.getDisplayName(), account.isStateKeyExpired(), account.getProfileUrl(), account.getId());
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
