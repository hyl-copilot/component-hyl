package com.hyl.component.binlog.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInfo {

    private int index;

    private String name;

    private String type;

    private String value;

    private boolean isKey;


}
