package com.hyl.component.binlog.dispatch;

import com.github.shyiko.mysql.binlog.event.*;
import com.hyl.component.binlog.event.EventListenerMethod;
import com.hyl.component.binlog.event.MethEventType;
import com.hyl.component.binlog.event.TableInfo;
import com.hyl.component.binlog.listener.ITableListener;
import com.hyl.component.binlog.util.TableUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class SimpleDispatch implements EventDispatch {

    private final TableUtil tableUtil;

    private Map<String, ITableListener> registerListener = new HashMap<>();

    private Map<String, Map<String, EventListenerMethod>> eventListener = new HashMap<>();

    private final Map<Long, TableInfo> tableMap = new HashMap<>();

    public SimpleDispatch(TableUtil tableUtil) {
        this.tableUtil = tableUtil;
    }

    @Override
    public void init(Map<String, ITableListener> registerListener, Map<String, Map<String, EventListenerMethod>> eventListener) {
        this.registerListener = registerListener;
        this.eventListener = eventListener;
    }

    @Override
    public void dispatch(Event event) throws Exception {
        EventType eventType = event.getHeader().getEventType();
        if (eventType == EventType.TABLE_MAP) {
            asyncTableMap(event);
            return;
        }
        switch (eventType) {
            case WRITE_ROWS:
            case EXT_WRITE_ROWS:
                insertEvent(event.getData());
                break;
            case UPDATE_ROWS:
            case EXT_UPDATE_ROWS:
                updateEvent(event.getData());
                break;
            case DELETE_ROWS:
            case EXT_DELETE_ROWS:
                deleteEvent(event.getData());
                break;
            default:
                //log.warn("未知事件类型:{}", event.getHeader().getEventType());
        }
    }

    protected void deleteEvent(DeleteRowsEventData deleteRowsEventData) {
        TableInfo tableInfo = tableMap.get(deleteRowsEventData.getTableId());
        EventListenerMethod method = getMethodListener(deleteRowsEventData.getTableId(), MethEventType.DELETE);
        if (method != null) {
            method.invoke(tableInfo, deleteRowsEventData);
            return;
        }
        ITableListener listener = getTableListener(deleteRowsEventData.getTableId());
        if (listener == null) {
            return;
        }
        listener.onDelete(tableInfo, deleteRowsEventData);
    }

    protected void updateEvent(UpdateRowsEventData updateRowsEventData) {
        TableInfo tableInfo = tableMap.get(updateRowsEventData.getTableId());
        EventListenerMethod method = getMethodListener(updateRowsEventData.getTableId(), MethEventType.UPDATE);
        if (method != null) {
            method.invoke(tableInfo, updateRowsEventData);
            return;
        }
        ITableListener listener = getTableListener(updateRowsEventData.getTableId());
        if (listener == null) {
            return;
        }
        listener.onUpdate(tableInfo, updateRowsEventData);

    }

    protected void insertEvent(WriteRowsEventData rowsEventData) {
        TableInfo tableInfo = tableMap.get(rowsEventData.getTableId());
        EventListenerMethod method = getMethodListener(rowsEventData.getTableId(), MethEventType.INSERT);
        if (method != null) {
            method.invoke(tableInfo, rowsEventData);
            return;
        }
        ITableListener listener = getTableListener(rowsEventData.getTableId());
        if (listener == null) {
            return;
        }
        listener.onInsert(tableInfo, rowsEventData);
    }

    protected void asyncTableMap(Event event) {
        TableMapEventData data = event.getData();
        try {
            tableMap.put(data.getTableId(), buildTableInfo(data));
        } catch (Exception e) {
            log.error("获取表信息失败", e);
        }
    }

    protected TableInfo buildTableInfo(TableMapEventData data) throws SQLException, ClassNotFoundException {
        if (tableMap.containsKey(data.getTableId())) {
            return tableMap.get(data.getTableId());
        }
        return TableInfo.builder()
                .schema(data.getDatabase())
                .tableName(data.getTable())
                .columns(tableUtil.getTableColumns(data.getDatabase(), data.getTable())).build();
    }


    protected EventListenerMethod getMethodListener(long tableId, String eventName) {
        TableInfo table = tableMap.get(tableId);
        if (Objects.isNull(table)) {
            log.warn("未找到表名,tableId:{}", tableId);
            throw new RuntimeException("未找到表名,tableId:" + tableId);
        }

        Map<String, EventListenerMethod> methodMap = eventListener.get(table.getFullName());
        if (methodMap == null) {
            return null;
        }
        return methodMap.get(eventName);
    }


    protected ITableListener getTableListener(Long tableId) {
        TableInfo table = tableMap.get(tableId);
        if (Objects.isNull(table)) {
            log.warn("未找到表名,tableId:{}", tableId);
            throw new RuntimeException("未找到表名,tableId:" + tableId);
        }
        ITableListener listener = registerListener.get(table.getFullName());
        if (listener == null) {
            log.debug("未找到表名,tableName:{}", table.getFullName());
            return null;
        }
        return listener;

    }


    protected TableInfo getTable(long tableId) {
        return tableMap.get(tableId);
    }
}
