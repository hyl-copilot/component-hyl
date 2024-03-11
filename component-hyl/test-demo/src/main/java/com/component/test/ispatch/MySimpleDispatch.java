package com.component.test.ispatch;

import cn.hutool.core.collection.CollUtil;
import com.github.shyiko.mysql.binlog.event.*;
import com.hyl.component.binlog.dispatch.EventDispatch;
import com.hyl.component.binlog.dispatch.SimpleDispatch;
import com.hyl.component.binlog.event.ColumnInfo;
import com.hyl.component.binlog.event.EventListenerMethod;
import com.hyl.component.binlog.event.MethEventType;
import com.hyl.component.binlog.event.TableInfo;
import com.hyl.component.binlog.listener.ITableListener;
import com.hyl.component.binlog.util.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;


@Slf4j
//@Service
public class MySimpleDispatch extends SimpleDispatch {

    @Autowired
    private TableUtil tableUtil;

    public MySimpleDispatch(TableUtil tableUtil) {
        super(tableUtil);
    }

    @Override
    protected void updateEvent(UpdateRowsEventData updateRowsEventData) {
        // 重写updateEvent方法
        // 根据enable_flag字段判断是否需要处理
        TableInfo table = getTable(updateRowsEventData.getTableId());
        ColumnInfo enableFlag = table.getColumns().values().stream().filter(column -> column.getName().equals("enable_flag")).findFirst().orElse(null);
        if (Objects.isNull(enableFlag)) {
            super.updateEvent(updateRowsEventData);
            return;
        }
        // 获取enable_flag字段的值
        updateRowsEventData.getRows().forEach(row -> {
            Object beforeEnableFlag = row.getKey()[enableFlag.getIndex() - 1];
            Object afterEnableFlag = row.getValue()[enableFlag.getIndex() - 1];
            if (Objects.equals(beforeEnableFlag, afterEnableFlag)) {
                //更新
                log.info("更新");
                super.updateEvent(updateRowsEventData);
                return;
            }
            // 根据enable_flag字段的值判断是否需要处理
            if (Objects.equals(beforeEnableFlag, 10) && Objects.equals(afterEnableFlag, 20)) {
                // 逻辑删除
                log.info("逻辑删除");
                List<Serializable[]> rows = new ArrayList<>();
                rows.add(row.getKey());
                DeleteRowsEventData deleteRowsEventData = new DeleteRowsEventData();
                deleteRowsEventData.setTableId(updateRowsEventData.getTableId());
                deleteRowsEventData.setRows(rows);
                super.deleteEvent(deleteRowsEventData);
            } else if (Objects.equals(beforeEnableFlag, 20) && Objects.equals(afterEnableFlag, 10)) {
                // 逻辑恢复
                log.info("逻辑恢复");
                List<Serializable[]> rows = new ArrayList<>();
                rows.add(row.getValue());
                WriteRowsEventData writeRowsEventData = new WriteRowsEventData();
                writeRowsEventData.setTableId(updateRowsEventData.getTableId());
                writeRowsEventData.setRows(rows);
                insertEvent(writeRowsEventData);
            }
        });
    }


    @Override
    protected void deleteEvent(DeleteRowsEventData deleteRowsEventData) {
        log.debug("deleteEvent 不需要处理");
    }

}
