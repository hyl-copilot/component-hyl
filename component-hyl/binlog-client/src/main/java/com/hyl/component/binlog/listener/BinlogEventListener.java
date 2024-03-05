package com.hyl.component.binlog.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.hyl.component.binlog.register.TableRegister;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BinlogEventListener implements BinaryLogClient.EventListener {

    private final TableRegister tableRegister;

    public BinlogEventListener(TableRegister tableRegister) {
        this.tableRegister = tableRegister;
    }

    @Override
    public void onEvent(Event event) {
        log.debug("event:{}", event);
        //分发事件
        tableRegister.dispatch(event);
    }
}
