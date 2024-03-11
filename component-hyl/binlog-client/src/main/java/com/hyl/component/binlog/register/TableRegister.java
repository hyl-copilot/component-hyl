package com.hyl.component.binlog.register;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.github.shyiko.mysql.binlog.event.*;
import com.hyl.component.binlog.annotation.TableEventListener;
import com.hyl.component.binlog.annotation.TableListener;
import com.hyl.component.binlog.dispatch.EventDispatch;
import com.hyl.component.binlog.event.*;
import com.hyl.component.binlog.listener.ITableListener;
import com.hyl.component.binlog.util.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class TableRegister {

    private final EventDispatch eventDispatch;

    private final Map<String, ITableListener> registerListener = new HashMap<>();

    private final Map<String, Map<String, EventListenerMethod>> eventListener = new HashMap<>();

    public TableRegister(EventDispatch eventDispatch) {
        this.eventDispatch = eventDispatch;
    }


    /*
     * 从spring容器中获取所有的TableListener注解的监听器
     */
    public void init(ApplicationContext context) {
        //从spring容器中获取所有的ITableListener
        registerByITableListener(context);
        //注册监听bean
        registerByMethodListener(context);
        //初始化转接器
        eventDispatch.init(registerListener, eventListener);
    }

    private void registerByMethodListener(ApplicationContext context) {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(TableListener.class);
        if (MapUtil.isEmpty(beansWithAnnotation)) {
            return;
        }
        for (Object listener : beansWithAnnotation.values()) {
            TableListener annotation = listener.getClass().getAnnotation(TableListener.class);
            String schema = annotation.schema();
            String tableName = annotation.table_name();
            Method[] methods = listener.getClass().getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(TableEventListener.class)) {
                    continue;
                }
                registerEvent(listener, schema + "." + tableName, method);
            }
        }
    }


    private void registerByITableListener(ApplicationContext context) {
        Map<String, ITableListener> beansOfType = context.getBeansOfType(ITableListener.class);
        if (MapUtil.isEmpty(beansOfType)) {
            return;
        }
        for (ITableListener listener : beansOfType.values()) {
            register(listener);
        }
    }

    //注册表监听器
    public void register(ITableListener listener) {
        String tableName = getTableName(listener);
        if (registerListener.containsKey(tableName)) {
            log.warn("表名{}已经存在,已经存在的监听器{}", tableName, registerListener.get(tableName).getClass().getName());
            throw new RuntimeException(tableName + "监听器已经存在");
        }
        log.info("注册表监听器,表名:{},监听器:{} success", tableName, listener.getClass().getName());
        registerListener.put(tableName, listener);
    }

    //注册方法监听器
    private void registerEvent(Object listener, String tableName, Method method) {
        TableEventListener annotation = method.getAnnotation(TableEventListener.class);
        if (StrUtil.isBlank(tableName)) {
            log.warn("{}未找到TableEventListener注解", method.getName());
            throw new RuntimeException(method.getName() + "未找到TableEventListener注解");
        }
        String eventType = annotation.event_type();
        if (StrUtil.isBlank(eventType)) {
            log.warn("{}未找到TableEventListener注解", method.getName());
            throw new RuntimeException(method.getName() + "未找到TableEventListener注解");
        }
        Map<String, EventListenerMethod> methodMap = eventListener.computeIfAbsent(tableName, k -> new HashMap<>());
        EventListenerMethod eventListenerMethod = new EventListenerMethod();
        eventListenerMethod.setTarget(listener);
        eventListenerMethod.setMethod(method);
        methodMap.put(eventType, eventListenerMethod);
        log.info("注册事件监听器,表名:{},事件类型:{},监听器:{} success", tableName, eventType, method.getName());
    }

    public boolean isEmpty() {
        return registerListener.isEmpty();
    }

    public void dispatch(Event event) throws Exception {
        eventDispatch.dispatch(event);
    }


    private String getTableName(ITableListener listener) {
        TableListener tableListener = listener.getClass().getAnnotation(TableListener.class);
        if (tableListener == null) {
            log.warn("{}未找到TableListener注解", listener.getClass().getName());
            throw new RuntimeException(listener.getClass().getName() + "未找到TableListener注解");
        }
        String schema = tableListener.schema();
        String tableName = tableListener.table_name();
        if (StrUtil.isNotBlank(tableName)) {
            return schema + "." + tableName;
        }
        //类名转换为表名  驼峰转下划线
        return schema + "." + StrUtil.toUnderlineCase(listener.getClass().getSimpleName());
    }


}
