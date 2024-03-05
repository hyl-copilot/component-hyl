package com.hyl.component.binlog.listener;


import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.event.DDLEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserTableListener implements TableListener {


    @Override
    public String getTableName() {
        return "t_user";
    }

    @Override
    public void onInsert(WriteRowsEventData event) {
        log.info("table:{} insert event:{}", getTableName(), event);
    }

    @Override
    public void onUpdate(UpdateRowsEventData event) {
        log.info("table:{} update event:{}", getTableName(), event);
    }

    @Override
    public void onDelete(DeleteRowsEventData event) {
        log.info("table:{} delete event:{}", getTableName(), event);
    }

    @Override
    public void onDDL(DDLEvent event) {
        log.info("table:{} ddl event:{}", getTableName(), event);
    }
}
