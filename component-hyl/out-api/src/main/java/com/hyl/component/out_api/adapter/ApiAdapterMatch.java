package com.hyl.component.out_api.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface ApiAdapterMatch {

    /**
     * adapter
     * @return
     */
    String adapter();

    /**
     * adapter key
     * @return
     */
    String key();
}
