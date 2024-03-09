package com.hyl.component.binlog.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.hyl.component.binlog.event.ColumnInfo;
import com.hyl.component.binlog.event.DDLEvent;
import com.hyl.component.binlog.event.TableInfo;
import com.mysql.cj.xdevapi.JsonArray;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleTableListener implements ITableListener {

    /**
     * 获取列数据
     *
     * @param tableInfo
     * @param event
     */
    public JSONArray getColumns(TableInfo tableInfo, List<Serializable[]> eventRows) {
        Map<Integer, ColumnInfo> columns = tableInfo.getColumns();
        JSONArray jsonArray = new JSONArray();
        for (Object[] row : eventRows) {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < row.length; i++) {
                ColumnInfo columnInfo = columns.get(i);
                obj.putOpt(columnInfo.getName(), row[i]);
            }
            jsonArray.add(obj);
        }
        return jsonArray;
    }


    @Override
    public void onInsert(TableInfo tableInfo, WriteRowsEventData event) {
        List<Serializable[]> rows = event.getRows();
        JSONArray columns = getColumns(tableInfo, rows);
        System.out.println("table:" + tableInfo.getTableName() + " insert event:" + columns);
    }

    @Override
    public void onUpdate(TableInfo tableInfo, UpdateRowsEventData event) {
        List<Map.Entry<Serializable[], Serializable[]>> rows = event.getRows();
        JSONArray beforeColumns = getColumns(tableInfo, rows.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        JSONArray afterColumns = getColumns(tableInfo, rows.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        System.out.println("table:" + tableInfo.getTableName() + " update before event:" + beforeColumns);
        System.out.println("table:" + tableInfo.getTableName() + " update after event:" + afterColumns);
    }

    @Override
    public void onDelete(TableInfo tableInfo, DeleteRowsEventData event) {
        List<Serializable[]> rows = event.getRows();
        JSONArray columns = getColumns(tableInfo, rows);
        System.out.println("table:" + tableInfo.getTableName() + " delete event:" + columns);
    }
}
