package com.hyl.component.api.authentication;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Data
@Configuration
@Scope
@ConfigurationProperties("open-api")
public class ThirdAuthConfig {

    /**
     * 第三方系统
     * 通过第三方系统访问的接口，不需要进行token校验，使用密钥校验
     */
    private List<ThirdAuth> thirdAuthList;
}
