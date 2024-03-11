package com.hyl.component.binlog.dispatch;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.event.ColumnInfo;
import com.hyl.component.binlog.event.TableInfo;
import com.hyl.component.binlog.util.TableUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
public class LogicDeleteDispatch extends SimpleDispatch {

    private final String LogicDeleteColumn;

    public LogicDeleteDispatch(TableUtil tableUtil) {
        super(tableUtil);
        this.LogicDeleteColumn = "enable_flag";
    }

    public LogicDeleteDispatch(TableUtil tableUtil, String logicDeleteColumn) {
        super(tableUtil);
        this.LogicDeleteColumn = logicDeleteColumn;
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
                super.updateEvent(updateRowsEventData);
                return;
            }
            // 根据enable_flag字段的值判断是否需要处理
            if (Objects.equals(beforeEnableFlag, 10) && Objects.equals(afterEnableFlag, 20)) {
                // 逻辑删除
                List<Serializable[]> rows = new ArrayList<>();
                rows.add(row.getKey());
                DeleteRowsEventData deleteRowsEventData = new DeleteRowsEventData();
                deleteRowsEventData.setTableId(updateRowsEventData.getTableId());
                deleteRowsEventData.setRows(rows);
                super.deleteEvent(deleteRowsEventData);
            } else if (Objects.equals(beforeEnableFlag, 20) && Objects.equals(afterEnableFlag, 10)) {
                // 逻辑恢复
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
        // 根据enable_flag字段判断是否需要处理
        TableInfo table = getTable(deleteRowsEventData.getTableId());
        ColumnInfo enableFlag = table.getColumns().values().stream().filter(column -> column.getName().equals("enable_flag")).findFirst().orElse(null);
        if (Objects.isNull(enableFlag)) {
            super.deleteEvent(deleteRowsEventData);
            return;
        }
        List<Serializable[]> rows = deleteRowsEventData.getRows();
        List<Serializable[]> deleteRows = new ArrayList<>();
        rows.forEach(row -> {
            Object enableFlagValue = row[enableFlag.getIndex() - 1];
            if (Objects.equals(enableFlagValue, 10)) {
                //需要删除
                log.info("deleteEvent 需要处理");
                deleteRows.add(row);
            }
        });
        if (deleteRows.size() > 0) {
            DeleteRowsEventData deleteEventData = new DeleteRowsEventData();
            deleteEventData.setTableId(deleteRowsEventData.getTableId());
            deleteEventData.setRows(deleteRows);
            super.deleteEvent(deleteEventData);
            return;
        }
        log.info("deleteEvent 不需要处理");
    }

}
