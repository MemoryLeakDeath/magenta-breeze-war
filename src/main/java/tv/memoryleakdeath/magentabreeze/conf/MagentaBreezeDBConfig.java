package tv.memoryleakdeath.magentabreeze.conf;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class MagentaBreezeDBConfig {
    public static final String DB_URL = "jdbc:h2:tcp://localhost/./db/magentabreeze;FILE_LOCK=SOCKET";

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        ds.addDataSourceProperty("URL", DB_URL);
        ds.addDataSourceProperty("user", "mb");
        ds.addDataSourceProperty("password", "");
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

//    @Bean
//    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
//        DatabaseStartupValidator validator = new DatabaseStartupValidator();
//        validator.setDataSource(dataSource);
//        validator.setInterval(2);
//        validator.setTimeout(60);
//        return validator;
//    }
//
//    @Bean
//    @DependsOn("databaseStartupValidator")
//    public LiquibaseRunner databaseMigrationRunner() {
//        LiquibaseRunner runner = new LiquibaseRunner();
//        runner.runMigrations();
//        return runner;
//    }
}
