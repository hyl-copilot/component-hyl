package com.hyl.component.api.autoconfigure;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ComponentScan("com.hyl.component.api")
@ConditionalOnProperty(prefix = "api_match", name = "enable", havingValue = "true")
public class MatchConfiguration {

    @PostConstruct
    public void init(){
        log.info("out_api init...");
    }
}
