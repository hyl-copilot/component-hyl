package com.hyl.component.gray.business.annotation;

import java.lang.annotation.*;

/**
 * 2022-12-01 20:58
 * create by hyl
 * desc:
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributionGray {

    String distributionTag();

    /**
     * 转发分级path
     * {"a":[{"b":"b1"},{"b":"b2"}]}
     * 根据b的值做拆分转发
     */
    String distributionJsonPath();

    /**
     * body 类型，0 jsonObj 1 jsonArray
     */
    int bodyType() default 0;


    String grayTag() default "";

    String jsonPath() default "";

}
