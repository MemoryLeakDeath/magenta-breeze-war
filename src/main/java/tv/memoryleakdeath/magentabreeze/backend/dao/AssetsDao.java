package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.magentabreeze.backend.mapper.AssetRowMapper;
import tv.memoryleakdeath.magentabreeze.common.pojo.Asset;

@Repository
public class AssetsDao {
    private static final Logger logger = LoggerFactory.getLogger(AssetsDao.class);
    public static final String[] COLUMNS = { "id", "originalfilename", "contenttype", "description",
            "created", "updated" };
    private static final int PAGE_SIZE = 20;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String GET_ASSET_SQL = """
            select %s from assets where id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public Asset getAsset(long id) {
        List<Asset> results = jdbcTemplate.query(GET_ASSET_SQL, new AssetRowMapper(), id);
        return results.stream().findFirst().orElse(null);
    }

    @Transactional
    public long createAsset(Asset newAsset) {
        String sql = "insert into assets (originalfilename, contenttype, description, created, updated) values (?,?,?,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP())";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newAsset.getOriginalFilename());
            ps.setString(2, newAsset.getContentType());
            ps.setString(3, newAsset.getDescription());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private static final String SHOW_ALL_ASSETS_SQL = """
            select %s from (
                select *, ROW_NUMBER() OVER(order by updated desc) AS rnum from assets
            ) where rnum between :start and :end
            order by updated desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<Asset> showAllAssets(int page) {
        int totalAssets = getAssetCount();
        int start = (page - 1) * PAGE_SIZE + 1;
        int end = Math.min(start + PAGE_SIZE - 1, totalAssets);
        return jdbcTemplate.query(SHOW_ALL_ASSETS_SQL, new AssetRowMapper(), start, end);
    }

    public int getAssetCount() {
        String sql = "select count(*) from assets";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    private static final String SHOW_ASSETS_BY_CONTENT_TYPE_SQL = """
            select %s from (
                select *, ROW_NUMBER() OVER(order by updated desc) AS rnum from assets
                where contenttype IN (:contentTypes)
            ) where rnum between :start and :end
            order by updated desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<Asset> getAssetsByContentType(List<String> contentTypes, int page) {
        if (contentTypes == null) {
            return Collections.emptyList();
        }
        int totalAssets = getAssetCountByType(contentTypes);
        int start = (page - 1) * PAGE_SIZE + 1;
        int end = Math.min(start + PAGE_SIZE - 1, totalAssets);
        Map<String, Object> params = new HashMap<>();
        params.put("contentTypes", contentTypes);
        params.put("start", start);
        params.put("end", end);
        return namedParameterJdbcTemplate.query(SHOW_ASSETS_BY_CONTENT_TYPE_SQL, params, new AssetRowMapper());
    }

    public int getAssetCountByType(List<String> contentTypes) {
        String sql = "select count(*) from assets where contenttype in (:contentTypes)";
        Map<String, Object> params = new HashMap<>();
        params.put("contentTypes", contentTypes);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public int getAssetPageCountByType(List<String> contentTypes) {
        return getAssetCountByType(contentTypes) / PAGE_SIZE;
    }
}
