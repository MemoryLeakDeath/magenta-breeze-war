package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tv.memoryleakdeath.magentabreeze.backend.mapper.AlertSettingsRowMapper;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;

@Repository
public class AlertSettingsDao {
    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsDao.class);
    private static final String[] COLUMNS = { "id", "service", "type", "active", "settings", "created", "updated" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_SETTINGS_SQL = """
            select %s from alertsettings order by updated desc, created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<AlertSettingsRow> getAllSettings() {
        return jdbcTemplate.query(GET_ALL_SETTINGS_SQL, new AlertSettingsRowMapper());
    }

    @Transactional
    public boolean createSettings(AlertSettingsRow newSettings) {
        String sql = "insert into alertsettings (service, type, active, settings, created, updated) values (?,?,?,? FORMAT JSON,?,?)";
        String jsonSettings = "";
        try {
            jsonSettings = new ObjectMapper().writeValueAsString(newSettings.getSettings());
        } catch (JsonProcessingException e) {
            logger.error("Unable to create new alert settings!", e);
            return false;
        }
        Date createdDate = new Date();
        int rowsAffected = jdbcTemplate.update(sql, newSettings.getService().name(),
                newSettings.getType().toString(), newSettings.isActive(), jsonSettings, createdDate, createdDate);
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean deleteSettings(Long id) {
        String sql = "delete from alertsettings where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return (rowsAffected > 0);
    }

    private static final String GET_SETTINGS_BY_ID = """
            select %s from alertsettings where id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public AlertSettingsRow getSettingsById(Long id) {
        List<AlertSettingsRow> results = jdbcTemplate.query(GET_SETTINGS_BY_ID, new AlertSettingsRowMapper(), id);
        return results.stream().findFirst().orElse(null);
    }

    @Transactional
    public boolean updateSettings(AlertSettingsRow row) {
        String sql = "update alertsettings set service = ?, type = ?, settings = ? FORMAT JSON, updated = CURRENT_TIMESTAMP() where id = ?";
        String jsonSettings = "";
        if (row.getSettings() != null) {
            try {
                jsonSettings = new ObjectMapper().writeValueAsString(row.getSettings());
            } catch (JsonProcessingException e) {
                logger.error("Unable to write updated json alert settings for id: " + row.getId(), e);
                return false;
            }
        }
        int rowsAffected = jdbcTemplate.update(sql, row.getService().name(), row.getType().toString(), jsonSettings,
                row.getId());
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean updateActiveFlag(Long id, boolean newSetting) {
        String sql = "update alertsettings set active = ?, updated = CURRENT_TIMESTAMP() where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newSetting, id);
        return (rowsAffected > 0);
    }
}
