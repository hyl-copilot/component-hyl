package com.hyl.component.api.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由转发
 * 根据请求头配置路由规则
 * @author hyl
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiRoutingMatch {

    /**
     * @return 路由点
     */
    String route();

    /**
     * @return 路由key
     */
    String key();
}
