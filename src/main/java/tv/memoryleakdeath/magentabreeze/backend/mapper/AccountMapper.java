package tv.memoryleakdeath.magentabreeze.backend.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.magentabreeze.common.pojo.Account;

public class AccountMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setService(rs.getString("service"));
        account.setChatOnly(rs.getBoolean("chatonly"));
        account.setCreated(rs.getDate("created"));
        account.setUpdated(rs.getDate("updated"));
        account.setDisplayName(rs.getString("displayname"));
        account.setProfileUrl(rs.getString("profileurl"));
        account.setServiceUserId(rs.getString("serviceuserid"));
        return account;
    }

}
