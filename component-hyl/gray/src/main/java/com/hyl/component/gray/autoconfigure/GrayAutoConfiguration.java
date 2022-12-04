package com.hyl.component.gray.autoconfigure;

import com.hyl.component.gray.business.cache.DataCache;
import com.hyl.component.gray.business.cache.LocalCache;
import com.hyl.component.gray.simple.rule.DefaultSimpleGrayRule;
import com.hyl.component.gray.simple.rule.SimpleGrayRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 2022-12-05 00:19
 * create by hyl
 * desc:
 */
@Configuration
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix="gray",name = "enable", havingValue = "true")
@ComponentScan("com.hyl.component.gray")
public class GrayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SimpleGrayRule.class)
    public SimpleGrayRule DefaultSimpleGrayRule(){
        return new DefaultSimpleGrayRule();
    }

    @Bean
    @ConditionalOnMissingBean(DataCache.class)
    public DataCache LocalCache(){
        return new LocalCache();
    }
}
