package tv.memoryleakdeath.magentabreeze.conf;

import org.h2.server.web.JakartaDbStarter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

public class MagentaBreezeInit implements WebApplicationInitializer {
    public static final long MAX_UPLOAD_SIZE = 100L * 1024L * 1024L; // 100MB
    public static final long MAX_UPLOAD_REQUEST_SIZE = 3L * MAX_UPLOAD_SIZE; // 300MB
    public static final int FILE_SIZE_THRESHOLD = 50 * 1024; // 50KB
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("tv.memoryleakdeath.magentabreeze.conf");

        servletContext.setInitParameter("db.url", MagentaBreezeDBConfig.DB_URL);
        servletContext.setInitParameter("db.user", "mb");
        servletContext.setInitParameter("db.password", "");
        servletContext.setInitParameter("db.tcpServer", "-tcpAllowOthers -ifNotExists");
        servletContext.addListener(new JakartaDbStarter());

        servletContext.addListener(new ContextLoaderListener(rootContext));
        servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter("UTF-8", true))
                .addMappingForUrlPatterns(null, true, "/*");

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
                new DispatcherServlet(rootContext));
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
        dispatcher.setMultipartConfig(
                new MultipartConfigElement(TEMP_DIR, MAX_UPLOAD_REQUEST_SIZE, MAX_UPLOAD_SIZE, FILE_SIZE_THRESHOLD));
    }

}
