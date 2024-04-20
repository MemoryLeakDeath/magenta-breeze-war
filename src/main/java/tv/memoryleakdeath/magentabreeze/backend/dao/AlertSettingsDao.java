package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.magentabreeze.backend.mapper.AlertSettingsRowMapper;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;

@Repository
public class AlertSettingsDao {
    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsDao.class);
    private static final String[] COLUMNS = { "id", "service", "type", "active", "settings" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_ACTIVE_SETTINGS_SQL = """
            select %s from alertsettings where active = true
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<AlertSettingsRow> getAllActiveSettings() {
        return jdbcTemplate.query(GET_ALL_ACTIVE_SETTINGS_SQL, new AlertSettingsRowMapper());
    }
}
