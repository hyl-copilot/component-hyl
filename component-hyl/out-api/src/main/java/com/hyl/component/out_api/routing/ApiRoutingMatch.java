package com.hyl.component.out_api.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由转发
 * 根据请求头配置路由规则
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiRoutingMatch {

    /**
     * 路由点
     * @return
     */
    String route();

    /**
     * 路由key
     * @return
     */
    String key();
}
