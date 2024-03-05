package com.hyl.component.binlog.register;

import cn.hutool.core.util.StrUtil;
import com.github.shyiko.mysql.binlog.event.*;
import com.hyl.component.binlog.event.DDLEvent;
import com.hyl.component.binlog.listener.TableListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class TableRegister {

    private final Map<String, TableListener> registerListener = new HashMap<>();

    private final Map<Long, String> tableMap = new HashMap<>();

    //注册表监听器
    public void register(TableListener listener) {
        String tableName = listener.getTableName();
        if (StrUtil.isBlank(tableName)) {
            log.warn("{}监听表名不能为空", listener.getClass().getName());
            throw new RuntimeException(listener.getClass().getName() + "监听表明不能为空");
        }
        if (registerListener.containsKey(tableName)) {
            log.warn("表名{}已经存在,已经存在的监听器{}", tableName, registerListener.get(tableName).getClass().getName());
            throw new RuntimeException(tableName + "监听器已经存在");
        }
        registerListener.put(tableName, listener);
    }

    public boolean isEmpty() {
        return registerListener.isEmpty();
    }

    public void dispatch(Event event) {
        switch (event.getHeader().getEventType()) {
            case TABLE_MAP:
                TableMapEventData data = event.getData();
                tableMap.put(data.getTableId(), data.getTable());
                break;
            case WRITE_ROWS:
                WriteRowsEventData rowsEventData = event.getData();
                TableListener listener = getTableListener(rowsEventData.getTableId());
                if (listener == null){
                    break;
                }
                listener.onInsert(rowsEventData);
                break;
            case UPDATE_ROWS:
                UpdateRowsEventData updateRowsEventData = event.getData();
                listener = getTableListener(updateRowsEventData.getTableId());
                if (listener == null){
                    break;
                }
                listener.onUpdate(updateRowsEventData);
                break;
            case DELETE_ROWS:
                DeleteRowsEventData deleteRowsEventData = event.getData();
                listener = getTableListener(deleteRowsEventData.getTableId());
                if (listener == null){
                    break;
                }
                listener.onDelete(deleteRowsEventData);
                break;
            case QUERY: // Add a case for the DDL event type
                QueryEventData queryEventData = event.getData();
                if (isDDL(queryEventData)){
                    String tableName = getTableName(queryEventData);
                    log.debug("DDL event, table name: {}", tableName);
                    listener = registerListener.get(tableName);
                    if (listener == null){
                        break;
                    }
                    listener.onDDL(new DDLEvent(queryEventData));
                }
                break;
            default:
                //log.warn("未知事件类型:{}", event.getHeader().getEventType());
        }
    }


    private TableListener getTableListener(Long tableId) {
        String tableName = tableMap.get(tableId);
        if (StrUtil.isBlank(tableName)) {
            log.warn("未找到表名,tableId:{}", tableId);
            throw new RuntimeException("未找到表名,tableId:" + tableId);
        }
        TableListener listener = registerListener.get(tableName);
        if (listener == null) {
            log.debug("未找到表名,tableName:{}", tableName);
            return null;
        }
        return listener;

    }

    private boolean isDDL(QueryEventData event) {
        String sql = event.getSql();
        return sql != null && sql.toLowerCase().startsWith("alter") || sql.toLowerCase().startsWith("create") || sql.toLowerCase().startsWith("drop");
    }

    private String getTableName(QueryEventData queryEventData) {
        String sql = queryEventData.getSql();
        String[] split = sql.split(" ");
        return split[split.length - 1];
    }
}
