package com.hyl.component.binlog.event;

import cn.hutool.db.meta.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    private String schema;

    private String tableName;

    private Map<Integer,ColumnInfo> columns;


    /**
     * 获取schema.table
     */
    public String getFullName() {
        return schema + "." + tableName;
    }
}
