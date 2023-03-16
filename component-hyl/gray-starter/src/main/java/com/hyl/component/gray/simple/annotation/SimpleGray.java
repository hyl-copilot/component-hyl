package com.hyl.component.gray.simple.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 2022-12-01 20:48
 * create by hyl
 * desc:
 * @author hyl
 */


@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleGray {
    /**
     * 转发比例
     */
    String proportion() default "1%";
}
