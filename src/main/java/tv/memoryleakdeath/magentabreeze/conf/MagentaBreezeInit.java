package tv.memoryleakdeath.magentabreeze.conf;

import org.h2.server.web.JakartaDbStarter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

public class MagentaBreezeInit implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("tv.memoryleakdeath.magentabreeze.conf");

        servletContext.addListener(new ContextLoaderListener(rootContext));
        servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter("UTF-8", true))
                .addMappingForUrlPatterns(null, true, "/*");

        servletContext.setInitParameter("db.url", MagentaBreezeDBConfig.DB_URL);
        servletContext.setInitParameter("db.user", "mb");
        servletContext.setInitParameter("db.password", "");
        servletContext.setInitParameter("db.tcpServer", "-tcpAllowOthers");
        servletContext.addListener(new JakartaDbStarter());

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
                new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

}
