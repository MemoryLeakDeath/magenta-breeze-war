package tv.memoryleakdeath.magentabreeze.conf;

import java.io.IOException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.spring.VelocityEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("tv.memoryleakdeath.magentabreeze.backend")
@PropertySource("classpath:version.properties")
public class MagentaBreezeApplicationConfig {

    @Autowired
    private Environment env;

    @Bean
    public String appSourceVersion() {
        return env.getProperty("version");
    }

    @Bean
    public VelocityEngine velocity() throws VelocityException, IOException {
        VelocityEngineFactoryBean velFactory = new VelocityEngineFactoryBean();
        return velFactory.createVelocityEngine();
    }
}
