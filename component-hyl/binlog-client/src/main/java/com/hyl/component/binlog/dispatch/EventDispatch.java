package com.hyl.component.binlog.dispatch;

import com.github.shyiko.mysql.binlog.event.Event;
import com.hyl.component.binlog.event.EventListenerMethod;
import com.hyl.component.binlog.listener.ITableListener;

import java.sql.SQLException;
import java.util.Map;

public interface EventDispatch {

    void init(Map<String, ITableListener> registerListener, Map<String, Map<String, EventListenerMethod>> eventListener);

    void dispatch(Event event) throws Exception;

}
