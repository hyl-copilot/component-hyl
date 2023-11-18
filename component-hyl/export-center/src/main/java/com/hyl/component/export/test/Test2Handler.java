package com.hyl.component.export.test;

import com.hyl.component.export.annotation.ExportHandler;
import com.hyl.component.export.job_handler.MysqlJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@ExportHandler("test2")
@Component
@Slf4j
public class Test2Handler extends MysqlJobHandler<TestVO> {

    @Override
    public String export(TestVO param) {
        log.info("TestHandler execute param:{}", param);
        try {
            Thread.sleep(1000*60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("TestHandler execute success - {}", param);
        return "/test/test.xls";
    }
}
