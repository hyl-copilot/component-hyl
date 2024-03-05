package com.hyl.component.binlog.event;

import com.github.shyiko.mysql.binlog.event.QueryEventData;

public class DDLEvent {

    private String schema;

    private String sql;


    public DDLEvent(QueryEventData queryEventData) {

        this.schema = queryEventData.getDatabase();

        this.sql = queryEventData.getSql();

    }

}
