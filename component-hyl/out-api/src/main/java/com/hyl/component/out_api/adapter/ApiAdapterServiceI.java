package com.hyl.component.out_api.adapter;

import java.util.Map;
import java.util.function.Function;

public interface ApiAdapterServiceI<T,R> {

    Map<String,Function<T,R>> register();

}
