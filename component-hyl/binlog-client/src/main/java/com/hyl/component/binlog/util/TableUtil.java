package com.hyl.component.binlog.util;

import com.hyl.component.binlog.config.MasterConfig;
import com.hyl.component.binlog.event.ColumnInfo;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TableUtil {

    private final MasterConfig masterConfig;

    public TableUtil(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }


    public Map<Integer, ColumnInfo> getTableColumns(String database, String table) throws SQLException, ClassNotFoundException {
        //链接数据库获取表结构
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager
                .getConnection("jdbc:mysql://" + masterConfig.getHost() + ":"
                                + masterConfig.getPort() + "/"
                                + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8",
                        masterConfig.getUsername(),
                        masterConfig.getPassword());
        //获取表结构
        String sql = "select * from information_schema.columns where table_schema=? and table_name=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, database);
        preparedStatement.setString(2, table);
        ResultSet resultSet = preparedStatement.executeQuery();
        Map<Integer, ColumnInfo> columnInfoMap = null;
        while (resultSet.next()) {
            if (columnInfoMap == null) {
                columnInfoMap = new HashMap<>();
            }
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setIndex(resultSet.getInt("ordinal_position"));
            columnInfo.setName(resultSet.getString("column_name"));
            columnInfo.setType(resultSet.getString("data_type"));
            columnInfo.setKey("PRI".equals(resultSet.getString("column_key")));
            columnInfoMap.put(columnInfo.getIndex() - 1, columnInfo);
        }
        return columnInfoMap;
    }


}
