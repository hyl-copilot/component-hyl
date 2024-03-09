package com.hyl.component.binlog.autoconfigure;


import com.hyl.component.binlog.client.BinlogClient;
import com.hyl.component.binlog.config.MasterConfig;
import com.hyl.component.binlog.register.TableRegister;
import com.hyl.component.binlog.util.TableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
    @Autowired
    public TableUtil tableUtil(MasterConfig masterConfig){
        return new TableUtil(masterConfig);
    }



    @Bean
    @Autowired
    public TableRegister tableRegister(ApplicationContext context, TableUtil tableUtil){
        TableRegister register = new TableRegister(tableUtil);
        register.init(context);
        return register;
    }

    @Bean
    @Autowired
    public BinlogClient binlogClient(MasterConfig masterConfig, TableRegister tableRegister){
        return new BinlogClient(masterConfig,tableRegister);
    }

}
