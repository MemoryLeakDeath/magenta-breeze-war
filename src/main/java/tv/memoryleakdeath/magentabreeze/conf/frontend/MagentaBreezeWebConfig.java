package tv.memoryleakdeath.magentabreeze.conf.frontend;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import tv.memoryleakdeath.magentabreeze.interceptors.MagentaBreezeControllerInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan("tv.memoryleakdeath.magentabreeze.frontend")
public class MagentaBreezeWebConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext ac;

    @Autowired
    @Qualifier("installBaseDir")
    private String installBaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/webfonts/**").addResourceLocations("/WEB-INF/webfonts/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/WEB-INF/images/favicon.ico").resourceChain(true).addResolver(new EncodedResourceResolver())
                .addResolver(new PathResourceResolver());
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:///" + installBaseDir + "/uploads/")
                .resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
    }


    @Bean
    public FreeMarkerConfigurer viewConfig() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setTemplateLoaderPath("/WEB-INF/views/");
        config.setFreemarkerSettings(freemarkerSettings());
        config.setDefaultEncoding("UTF-8");
        return config;
    }

    private Properties freemarkerSettings() {
        Properties props = new Properties();
        props.setProperty("autoImport",
                "/spring.ftl as spring, macros/macros.html as mb, macros/layout.html as layout");
        props.setProperty("apiBuiltinEnabled", "true");
        props.setProperty("defaultEncoding", "UTF-8");
        props.setProperty("lazyAutoImports", "true");
        props.setProperty("outputEncoding", "UTF-8");
        props.setProperty("outputFormat", "HTML");
        props.setProperty("tagSyntax", "square_bracket");
        props.setProperty("templateExceptionHandler", "rethrow");
        return props;
    }

    @Bean
    public FreeMarkerViewResolver viewResolver() {
        FreeMarkerViewResolver view = new FreeMarkerViewResolver();
        view.setApplicationContext(ac);
        view.setCache(false);
        view.setPrefix("");
        view.setRequestContextAttribute("rc");
        view.setSuffix(".html");
        view.setExposeSessionAttributes(true);
        view.setAttributesMap(Map.of("applicationVersion", ac.getBean("appSourceVersion")));
        return view;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MagentaBreezeControllerInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("/WEB-INF/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.US);
        messageSource.setCacheSeconds(2);
        return messageSource;
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("America/New_York"));
        return resolver;
    }

    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language");
        return interceptor;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }

}
