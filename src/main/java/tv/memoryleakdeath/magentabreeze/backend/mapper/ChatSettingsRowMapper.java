package tv.memoryleakdeath.magentabreeze.backend.mapper;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettings;
import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettingsRow;

public class ChatSettingsRowMapper extends BaseMapper implements RowMapper<ChatSettingsRow> {
    private static final Logger logger = LoggerFactory.getLogger(ChatSettingsRowMapper.class);

    @Override
    public ChatSettingsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatSettingsRow row = new ChatSettingsRow();
        row.setActive(rs.getBoolean("active"));
        row.setId(rs.getLong("id"));
        row.setName(rs.getString("name"));
        row.setSettings(parseChatSettings(rs.getBytes("settings")));
        row.setCreated(rs.getTimestamp("created"));
        row.setUpdated(rs.getTimestamp("updated"));
        return row;
    }

    public ChatSettingsRow mapRowWithPrefix(ResultSet rs, int rowNum, String prefix) throws SQLException {
        ChatSettingsRow row = new ChatSettingsRow();
        row.setActive(rs.getBoolean(prefix + "_active"));
        row.setId(rs.getLong(prefix + "_id"));
        row.setName(rs.getString(prefix + "_name"));
        row.setSettings(parseChatSettings(rs.getBytes(prefix + "_settings")));
        row.setCreated(rs.getTimestamp(prefix + "_created"));
        row.setUpdated(rs.getTimestamp(prefix + "_updated"));
        return row;
    }

    private ChatSettings parseChatSettings(byte[] settings) {
        ObjectMapper mapper = JsonMapper.builder().enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION).build();
        try {
            return mapper.readValue(new String(settings, StandardCharsets.UTF_8), ChatSettings.class);
        } catch (JsonProcessingException e) {
            logger.error("Unable to deserialize json to chat settings object!", e);
        }
        return null;
    }

}
