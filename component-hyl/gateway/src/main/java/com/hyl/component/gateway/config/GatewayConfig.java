package com.hyl.component.gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;


@Data
@Configuration
@Scope
@ConfigurationProperties("gateway-config")
public class GatewayConfig {
    /**
     * token 过期时间 s
     * 默认 3600s
     */
    @Value("${expireTime:3600}")
    private Long expireTime;
    /**
     * 白名单
     */
    private List<String> whiteList;

    /**
     * 第三方系统
     * 通过第三方系统访问的接口，不需要进行token校验，使用密钥校验
     */
    private List<ThirdAuth> thirdAuthList;


}
