package com.hyl.component.binlog.listener;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.event.TableInfo;

public interface ITableListener {

    void onInsert(TableInfo tableInfo, WriteRowsEventData event);

    void onUpdate(TableInfo tableInfo, UpdateRowsEventData event);

    void onDelete(TableInfo tableInfo, DeleteRowsEventData event);

}
