package com.hyl.component.binlog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "binlog.master")
public class MasterConfig {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private Long serverId;

    private BinlogConfig binlog;


}
