package com.hyl.component.out_api.autoconfigure;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
@EnableAspectJAutoProxy
@ComponentScan("com.hyl.component.out_api")
@ConditionalOnProperty(prefix = "api_match", name = "enable", havingValue = "true")
public class MatchConfiguration {

    @PostConstruct
    public void init(){
        log.info("out_api init...");
    }
}
