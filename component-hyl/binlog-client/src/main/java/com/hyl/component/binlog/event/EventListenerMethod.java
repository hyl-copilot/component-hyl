package com.hyl.component.binlog.event;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class EventListenerMethod {

    private Object target;

    private Method method;

    public void invoke(TableInfo tableInfo, Object eventData) {
        try {
            method.invoke(target, tableInfo, eventData);
        } catch (Exception e) {
            throw new RuntimeException("invoke method error", e);
        }
    }
}
