package tv.memoryleakdeath.magentabreeze.conf.backend;

import org.h2.server.web.JakartaDbStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class StartDBAndRunMigrations extends JakartaDbStarter {
    private static final Logger logger = LoggerFactory.getLogger(StartDBAndRunMigrations.class);
    private static boolean hasRun = false;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        logger.info("Running database migrations...");
        runMigrations();
    }

    private synchronized void setHasRun(boolean value) {
        hasRun = value;
    }

    private void runMigrations() {
        if (hasRun) {
            return;
        }
        setHasRun(true);
        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(getConnection()));
            Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
                CommandScope update = new CommandScope("update");
                update.addArgumentValue("changelogFile", "db_migrations.json");
                update.addArgumentValue("database", database);
                update.execute();
            });
            logger.info("Migrations complete!");
        } catch (Exception e) {
            logger.error("Unable to perform database migrations!", e);
        }
    }
}