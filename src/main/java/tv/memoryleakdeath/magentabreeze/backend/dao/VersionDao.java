package tv.memoryleakdeath.magentabreeze.backend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VersionDao {
    private static final Logger logger = LoggerFactory.getLogger(VersionDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setVersion(String newVersion) {
        String deleteSql = "delete from version";
        String insertSql = "insert into version (version) values (?)";
        jdbcTemplate.update(deleteSql);
        jdbcTemplate.update(insertSql, newVersion);
    }

    public String getCurrentVersion() {
        String sql = "select version from version fetch first row only";
        List<String> results = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("version");
            }
        });
        return results.stream().findFirst().orElse(null);
    }
}
