package com.hyl.component.binlog.autoconfigure;


import com.hyl.component.binlog.client.BinlogClient;
import com.hyl.component.binlog.config.MasterConfig;
import com.hyl.component.binlog.register.TableRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(MasterConfig.class)
@ConditionalOnProperty(prefix="binlog",name = "client", havingValue = "enable")
@ComponentScan("com.hyl.component.binlog")
public class BinlogClientAutoConfiguration {

    @Bean
    public TableRegister tableRegister(){
        return new TableRegister();
    }

    @Bean
    @Autowired
    public BinlogClient binlogClient(MasterConfig masterConfig, TableRegister tableRegister){
        return new BinlogClient(masterConfig,tableRegister);
    }

}
