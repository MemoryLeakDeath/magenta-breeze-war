package tv.memoryleakdeath.magentabreeze.conf;

import java.util.Set;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionTrackingMode;
import tv.memoryleakdeath.magentabreeze.conf.backend.MagentaBreezeDBConfig;
import tv.memoryleakdeath.magentabreeze.conf.backend.StartDBAndRunMigrations;

public class MagentaBreezeInit implements WebApplicationInitializer {
    public static final long MAX_UPLOAD_SIZE = 100L * 1024L * 1024L; // 100MB
    public static final long MAX_UPLOAD_REQUEST_SIZE = 3L * MAX_UPLOAD_SIZE; // 300MB
    public static final int FILE_SIZE_THRESHOLD = 50 * 1024; // 50KB
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("tv.memoryleakdeath.magentabreeze.conf.backend");
        rootContext.setDisplayName("Root Context Magenta Breeze");
        servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));

        servletContext.setInitParameter("db.url", MagentaBreezeDBConfig.DB_URL);
        servletContext.setInitParameter("db.user", "mb");
        servletContext.setInitParameter("db.password", "");
        servletContext.setInitParameter("db.tcpServer", "-tcpAllowOthers -ifNotExists");
        servletContext.addListener(new StartDBAndRunMigrations());

        servletContext.addListener(new ContextLoaderListener(rootContext));

        FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",
                new CharacterEncodingFilter("UTF-8", true));
        characterEncodingFilter.setAsyncSupported(true);
        characterEncodingFilter.addMappingForUrlPatterns(null, true, "/*");

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.scan("tv.memoryleakdeath.magentabreeze.conf.frontend");
        webContext.setDisplayName("Web Context Magenta Breeze");
        webContext.setParent(rootContext);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
                new DispatcherServlet(webContext));
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
        dispatcher.setMultipartConfig(
                new MultipartConfigElement(TEMP_DIR, MAX_UPLOAD_REQUEST_SIZE, MAX_UPLOAD_SIZE, FILE_SIZE_THRESHOLD));
    }

}
