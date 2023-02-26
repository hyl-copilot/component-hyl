package com.hyl.component.out_api.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface ApiAdapter {

    /**
     * adapter
     * @return
     */
    String adapter();

}
