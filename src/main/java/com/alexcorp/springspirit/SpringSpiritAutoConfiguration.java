package com.alexcorp.springspirit;

import com.alexcorp.springspirit.props.SpringSpiritProperties;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableConfigurationProperties(SpringSpiritProperties.class)
@EnableJpaRepositories
@EntityScan("com.alexcorp.springspirit.*")
@EnableAspectJAutoProxy
@ComponentScan
public class SpringSpiritAutoConfiguration {

    static {
        Thread.currentThread().setName("Application");
    }

}
