package tv.memoryleakdeath.magentabreeze.backend.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsWithAssets;

public class AlertSettingsWithAssetsRowMapper extends BaseMapper implements RowMapper<AlertSettingsWithAssets> {

    @Override
    public AlertSettingsWithAssets mapRow(ResultSet rs, int rowNum) throws SQLException {
        AlertSettingsWithAssets row = new AlertSettingsWithAssets(
                new AlertSettingsRowMapper().mapRowWithPrefix(rs, rowNum, "settings"));
        row.setAlertImage(new AssetRowMapper().mapRowWithPrefix(rs, rowNum, "image"));
        row.setAlertSound(new AssetRowMapper().mapRowWithPrefix(rs, rowNum, "sound"));
        return row;
    }

}
