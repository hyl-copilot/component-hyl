package com.hyl.component.gray.request;

import com.hyl.component.gray.config.GrayConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author hyl
 */
@Configuration
@ConditionalOnClass(GrayConfigProperties.class)
public class WebAuthFilterConfig {

    @SuppressWarnings("unchecked")
    @Bean
    public FilterRegistrationBean webAuthFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(bodyFilter());
        registration.setName("bodyFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public Filter bodyFilter() {
        return new BodyFilter();
    }
}