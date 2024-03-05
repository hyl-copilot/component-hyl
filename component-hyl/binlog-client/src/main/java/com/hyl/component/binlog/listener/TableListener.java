package com.hyl.component.binlog.listener;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.event.DDLEvent;

public interface TableListener {

    String getTableName();

    void onInsert(WriteRowsEventData event);

    void onUpdate(UpdateRowsEventData event);

    void onDelete(DeleteRowsEventData event);

    void onDDL(DDLEvent event);
}
