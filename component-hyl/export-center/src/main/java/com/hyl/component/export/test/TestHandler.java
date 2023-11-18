package com.hyl.component.export.test;

import com.hyl.component.export.annotation.ExportHandler;
import com.hyl.component.export.job_handler.MysqlJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@ExportHandler("test")
@Component
@Slf4j
public class TestHandler extends MysqlJobHandler<String> {

    @Override
    public String export(String param) {
        log.info("TestHandler execute param:{}", param);
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("TestHandler execute success - {}", param);
        return "/test/test.xls";
    }
}
