package com.hyl.component.api.autoconfigure;


import com.hyl.component.api.authentication.ThirdAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

/**
 * @author hyl
 */
@Configuration
@Slf4j
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ThirdAuthConfig.class)
@ComponentScan("com.hyl.component.api")
public class OpenApiAutoConfiguration {

    @PostConstruct
    public void init(){
        log.info("open_api init...");
    }
}
