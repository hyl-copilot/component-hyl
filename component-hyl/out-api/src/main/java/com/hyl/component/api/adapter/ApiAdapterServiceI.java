package com.hyl.component.api.adapter;

import java.util.Map;
import java.util.function.Function;

/**
 * @author hyl
 */
public interface ApiAdapterServiceI<T,R> {
    /**
     * 注册Function
     * @return 返回已经注册的方法
     */
    Map<String,Function<T,R>> register();

}
