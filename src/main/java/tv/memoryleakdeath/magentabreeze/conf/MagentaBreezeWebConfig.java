package tv.memoryleakdeath.magentabreeze.conf;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/webfonts/**").addResourceLocations("/WEB-INF/webfonts/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/WEB-INF/images/favicon.ico").resourceChain(true).addResolver(new EncodedResourceResolver())
                .addResolver(new PathResourceResolver());
    }


    @Bean
    public FreeMarkerConfigurer viewConfig() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setDefaultEncoding("UTF-8");
        config.setTemplateLoaderPath("/WEB-INF/views/");
        return config;
    }

    @Bean
    public FreeMarkerViewResolver viewResolver() {
        FreeMarkerViewResolver view = new FreeMarkerViewResolver();
        view.setApplicationContext(ac);
        view.setCache(false);
        view.setPrefix("");
        view.setRequestContextAttribute("rc");
        view.setSuffix(".html");
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
}