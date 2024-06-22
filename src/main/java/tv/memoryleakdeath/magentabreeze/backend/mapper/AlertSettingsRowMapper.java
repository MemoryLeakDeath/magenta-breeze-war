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

import tv.memoryleakdeath.magentabreeze.common.AlertTypeConstants;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettings;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;

public class AlertSettingsRowMapper extends BaseMapper implements RowMapper<AlertSettingsRow> {
    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsRowMapper.class);

    @Override
    public AlertSettingsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        AlertSettingsRow row = new AlertSettingsRow();
        row.setActive(rs.getBoolean("active"));
        row.setId(rs.getLong("id"));
        row.setSettings(parseAlertSettings(rs.getBytes("settings")));
        row.setType(AlertTypeConstants.getType(rs.getString("type")));
        row.setCreated(rs.getTimestamp("created"));
        row.setUpdated(rs.getTimestamp("updated"));
        Long imageId = rs.getLong("imageid");
        if (rs.wasNull()) {
            imageId = null;
        }
        row.setImageId(imageId);
        Long soundId = rs.getLong("soundid");
        if (rs.wasNull()) {
            soundId = null;
        }
        row.setSoundId(soundId);
        return row;
    }

    public AlertSettingsRow mapRowWithPrefix(ResultSet rs, int rowNum, String prefix) throws SQLException {
        AlertSettingsRow row = new AlertSettingsRow();
        row.setActive(rs.getBoolean(prefix + "_active"));
        row.setId(rs.getLong(prefix + "_id"));
        row.setSettings(parseAlertSettings(rs.getBytes(prefix + "_settings")));
        row.setType(AlertTypeConstants.getType(rs.getString(prefix + "_type")));
        row.setCreated(rs.getTimestamp(prefix + "_created"));
        row.setUpdated(rs.getTimestamp(prefix + "_updated"));
        Long imageId = rs.getLong(prefix + "_imageid");
        if (rs.wasNull()) {
            imageId = null;
        }
        row.setImageId(imageId);
        Long soundId = rs.getLong(prefix + "_soundid");
        if (rs.wasNull()) {
            soundId = null;
        }
        row.setSoundId(soundId);
        return row;
    }

    private AlertSettings parseAlertSettings(byte[] settings) {
        ObjectMapper mapper = JsonMapper.builder().enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION).build();
        try {
            return mapper.readValue(new String(settings, StandardCharsets.UTF_8), AlertSettings.class);
        } catch (JsonProcessingException e) {
            logger.error("Unable to deserialize json to alert settings object!", e);
        }
        return null;
    }

}
