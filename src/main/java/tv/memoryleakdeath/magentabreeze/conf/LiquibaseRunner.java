package tv.memoryleakdeath.magentabreeze.conf;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import tv.memoryleakdeath.magentabreeze.backend.dao.VersionDao;

@Component
public class LiquibaseRunner {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseRunner.class);

    @Autowired
    private DataSource ds;

    @Autowired
    @Qualifier("appSourceVersion")
    private String sourceVersion;

    @Autowired
    private VersionDao versionDao;

    @EventListener(ContextRefreshedEvent.class)
    @Order(0)
    public void runMigrations() {
        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(ds.getConnection()));
            Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
                CommandScope update = new CommandScope("update");
                update.addArgumentValue("changelogFile", "db_migrations.json");
                update.addArgumentValue("database", database);
                update.execute();
                versionDao.setVersion(sourceVersion);
            });
        } catch (Exception e) {
            logger.error("Unable to perform database migrations!", e);
        }
    }
}
