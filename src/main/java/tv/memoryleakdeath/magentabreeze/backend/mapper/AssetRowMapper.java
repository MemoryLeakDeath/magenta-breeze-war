package tv.memoryleakdeath.magentabreeze.backend.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.magentabreeze.common.pojo.Asset;

public class AssetRowMapper implements RowMapper<Asset> {
    private static final Logger logger = LoggerFactory.getLogger(AssetRowMapper.class);

    @Override
    public Asset mapRow(ResultSet rs, int rowNum) throws SQLException {
        Asset asset = new Asset();
        asset.setId(rs.getLong("id"));
        asset.setOriginalFilename(rs.getString("originalfilename"));
        asset.setContentType(rs.getString("contenttype"));
        asset.setDescription(rs.getString("description"));
        asset.setCreated(rs.getDate("created"));
        asset.setUpdated(rs.getDate("updated"));
        return asset;
    }

    public Asset mapRowWithPrefix(ResultSet rs, int rowNum, String prefix) throws SQLException {
        Asset asset = new Asset();
        asset.setId(rs.getLong(prefix + "_id"));
        asset.setOriginalFilename(rs.getString(prefix + "_originalfilename"));
        asset.setContentType(rs.getString(prefix + "_contenttype"));
        asset.setDescription(rs.getString(prefix + "_description"));
        asset.setCreated(rs.getDate(prefix + "_created"));
        asset.setUpdated(rs.getDate(prefix + "_updated"));
        return asset;
    }
}
