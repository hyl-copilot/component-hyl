package com.hyl.component.binlog.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface TableListener {

    String schema();

    String table_name();

}
