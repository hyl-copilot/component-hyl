package com.hyl.component.api.adapter;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hyl
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ApiAdapter {
    @AliasFor(annotation = Component.class)
    String value() default "";
    /**
     * adapter
     * @return 适配器名称
     */
    String adapter();

}
