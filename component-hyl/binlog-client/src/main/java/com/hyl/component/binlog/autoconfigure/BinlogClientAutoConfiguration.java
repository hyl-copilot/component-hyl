package com.hyl.component.binlog.autoconfigure;


import com.hyl.component.binlog.client.BinlogClient;
import com.hyl.component.binlog.config.MasterConfig;
import com.hyl.component.binlog.dispatch.EventDispatch;
import com.hyl.component.binlog.dispatch.SimpleDispatch;
import com.hyl.component.binlog.register.TableRegister;
import com.hyl.component.binlog.util.TableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnMissingBean
    public EventDispatch eventDispatch(TableUtil tableUtil){
        return new SimpleDispatch(tableUtil);
    }


    @Bean
    @Autowired
    public TableRegister tableRegister(ApplicationContext context, EventDispatch eventDispatch){
        TableRegister register = new TableRegister(eventDispatch);
        register.init(context);
        return register;
    }

    @Bean
    @Autowired
    public BinlogClient binlogClient(MasterConfig masterConfig, TableRegister tableRegister){
        return new BinlogClient(masterConfig,tableRegister);
    }

}
