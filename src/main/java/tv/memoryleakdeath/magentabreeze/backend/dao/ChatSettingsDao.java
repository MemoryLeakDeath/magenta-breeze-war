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

import tv.memoryleakdeath.magentabreeze.backend.mapper.ChatSettingsRowMapper;
import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettingsRow;

@Repository
public class ChatSettingsDao {
    private static final Logger logger = LoggerFactory.getLogger(ChatSettingsDao.class);
    private static final String[] COLUMNS = { "id", "active", "name", "settings", "created", "updated" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_SETTINGS_SQL = """
            select %s from chatsettings order by updated desc, created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<ChatSettingsRow> getAllSettings() {
        return jdbcTemplate.query(GET_ALL_SETTINGS_SQL, new ChatSettingsRowMapper());
    }

    @Transactional
    public boolean createSettings(ChatSettingsRow newSettings) {
        String sql = "insert into chatsettings (active, name, settings, created, updated) values (?,?,? FORMAT JSON,?,?)";
        String jsonSettings = "";
        try {
            jsonSettings = new ObjectMapper().writeValueAsString(newSettings.getSettings());
        } catch (JsonProcessingException e) {
            logger.error("Unable to create new chat settings!", e);
            return false;
        }
        Date createdDate = new Date();
        int rowsAffected = jdbcTemplate.update(sql, newSettings.isActive(), newSettings.getName(), jsonSettings,
                createdDate, createdDate);
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean deleteSettings(Long id) {
        String sql = "delete from chatsettings where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return (rowsAffected > 0);
    }

    private static final String GET_SETTINGS_BY_ID = """
            select %s from chatsettings where id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public ChatSettingsRow getSettingsById(Long id) {
        List<ChatSettingsRow> results = jdbcTemplate.query(GET_SETTINGS_BY_ID, new ChatSettingsRowMapper(), id);
        return results.stream().findFirst().orElse(null);
    }

    @Transactional
    public boolean updateSettings(ChatSettingsRow row) {
        String sql = "update chatsettings set name = ?, settings = ? FORMAT JSON, updated = CURRENT_TIMESTAMP() where id = ?";
        String jsonSettings = "";
        if (row.getSettings() != null) {
            try {
                jsonSettings = new ObjectMapper().writeValueAsString(row.getSettings());
            } catch (JsonProcessingException e) {
                logger.error("Unable to write updated json chat settings for id: " + row.getId(), e);
                return false;
            }
        }
        int rowsAffected = jdbcTemplate.update(sql, row.getName(), jsonSettings, row.getId());
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean updateActiveFlag(Long id, boolean newSetting) {
        String sql = "update chatsettings set active = ?, updated = CURRENT_TIMESTAMP() where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newSetting, id);
        return (rowsAffected > 0);
    }
}
