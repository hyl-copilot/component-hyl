package com.hyl.component.export.config;


import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {
    private static final String REDISSON_PREFIX = "redis://";
    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    @Value("${spring.redis.port:6379}")
    private String redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private Integer database;

    @Bean
    public RedissonClient singleRedissonClient() throws IOException {
        //Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson.yml"));
        //return Redisson.create(config);
        // 单节点模式
        // 1、创建配置
        Config config = new Config();
        // Redis url should start with redis:// or rediss://
        config.useSingleServer()
                .setAddress(REDISSON_PREFIX + redisHost + ":" + redisPort)
                .setPassword(StrUtil.isBlank(redisPassword) ? null : redisPassword)
                .setDatabase(database);
        // 2、根据 Config 创建出 RedissonClient 实例
        return Redisson.create(config);
    }

//    @Bean
//    public RedissonClient clusterRedissonClient() {
//        Config config = new Config();
//        config.useClusterServers()
//                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
//                //可以用"rediss://"来启用SSL连接
//                .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
//                .addNodeAddress("redis://127.0.0.1:7002");
//        return Redisson.create(config);
//    }

}
