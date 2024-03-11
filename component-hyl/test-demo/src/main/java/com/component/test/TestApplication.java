package com.component.test;

import com.hyl.component.binlog.dispatch.LogicDeleteDispatch;
import com.hyl.component.binlog.util.TableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


    @Bean
    @Autowired
    public LogicDeleteDispatch logicDeleteDispatch(TableUtil tableUtil) {
        return new LogicDeleteDispatch(tableUtil, "enable_flag");
    }
}
