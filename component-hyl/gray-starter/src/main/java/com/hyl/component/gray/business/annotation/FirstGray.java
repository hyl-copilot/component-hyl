package com.hyl.component.gray.business.annotation;

import java.lang.annotation.*;

/**
 * 2022-12-01 20:48
 * create by hyl
 * desc:
 * @author hyl
 */


@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstGray {

    String grayTag();

    String jsonPath();

}
