package com.component.test.listener;


import cn.hutool.json.JSONArray;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.annotation.TableEventListener;
import com.hyl.component.binlog.annotation.TableListener;
import com.hyl.component.binlog.event.DDLEvent;
import com.hyl.component.binlog.event.MethEventType;
import com.hyl.component.binlog.event.TableInfo;
import com.hyl.component.binlog.listener.ITableListener;
import com.hyl.component.binlog.listener.SimpleTableListener;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@TableListener(schema = "abc", table_name = "user")
public class UserTableListener extends SimpleTableListener {

    @TableEventListener(event_type = MethEventType.INSERT)
    public void insert(TableInfo tableInfo, WriteRowsEventData event) {
        log.info("user table insert2 columns:{}", getColumns(tableInfo, event.getRows()));
    }

    @Override
    public void onUpdate(TableInfo tableInfo, UpdateRowsEventData event) {
        List<Map.Entry<Serializable[], Serializable[]>> rows = event.getRows();
        JSONArray beforeColumns = getColumns(tableInfo, rows.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        JSONArray afterColumns = getColumns(tableInfo, rows.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        System.out.println("UserTableListener table:" + tableInfo.getTableName() + " update before event:" + beforeColumns);
        System.out.println("UserTableListener table:" + tableInfo.getTableName() + " update after event:" + afterColumns);
    }

}
