package com.hyl.component.gray.autoconfigure;

import com.hyl.component.gray.business.cache.AbstractDataCache;
import com.hyl.component.gray.business.cache.LocalCache;
import com.hyl.component.gray.simple.rule.DefaultSimpleGrayRule;
import com.hyl.component.gray.simple.rule.AbstractSimpleGrayRule;
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
 * @author hyl
 */
@Configuration
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix="gray",name = "enable", havingValue = "true")
@ComponentScan("com.hyl.component.gray")
public class GrayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AbstractSimpleGrayRule.class)
    public AbstractSimpleGrayRule defaultSimpleGrayRule(){
        return new DefaultSimpleGrayRule();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractDataCache.class)
    public AbstractDataCache localCache(){
        return new LocalCache();
    }
}
