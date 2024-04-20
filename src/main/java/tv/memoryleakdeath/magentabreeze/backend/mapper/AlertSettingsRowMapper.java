package tv.memoryleakdeath.magentabreeze.backend.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.magentabreeze.common.AlertTypes;
import tv.memoryleakdeath.magentabreeze.common.ServiceTypes;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;

public class AlertSettingsRowMapper extends BaseMapper implements RowMapper<AlertSettingsRow> {

    @Override
    public AlertSettingsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        AlertSettingsRow row = new AlertSettingsRow();
        row.setActive(rs.getBoolean("active"));
        row.setId(rs.getLong("id"));
        row.setService(getEnumTypeFromString(rs.getString("service"), ServiceTypes.class));
        row.setSettings(null);
        row.setType(getEnumTypeFromString(rs.getString("type"), AlertTypes.class));
        return row;
    }

}
