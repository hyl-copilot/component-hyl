package com.hyl.component.api.adapter;

import cn.hutool.core.map.MapUtil;
import com.hyl.component.api.exception.NotFoundFuncException;
import com.hyl.component.api.vo.ApiAdapterFunc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


/**
 * @author hyl
 */
@SuppressWarnings("unchecked")
@Component
@Slf4j
public class ApiAdapterManager {


    private static final Map<String, ApiAdapterFunc> ADAPTER_MAP = new HashMap<>();

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerAdapter() {
        log.info("api adapter register ..");
        Map<String, ApiAdapterServiceI> beansOfType = applicationContext.getBeansOfType(ApiAdapterServiceI.class);
        if (MapUtil.isEmpty(beansOfType)) {
            return;
        }
        beansOfType.forEach((service, adapter) -> {
            String adapterName = getAdapterName(adapter);
            Map<String, Function> func = adapter.register();
            if (MapUtil.isEmpty(func)) {
                return;
            }
            ADAPTER_MAP.put(adapterName, this.buildApiFunc(adapterName, func));
        });
    }

    private String getAdapterName(ApiAdapterServiceI adapterServiceI) {
        ApiAdapter adapter = adapterServiceI.getClass().getAnnotation(ApiAdapter.class);
        if (Objects.isNull(adapter)) {
            throw new RuntimeException("请使用@ApiAdapter注解【" + adapterServiceI.getClass().getName() + "】");
        }
        return adapter.adapter();
    }

    private ApiAdapterFunc buildApiFunc(String adapter, Map<String, Function> func) {
        return ApiAdapterFunc.builder()
                .name(adapter)
                .apiFunc(func)
                .build();
    }

    /**
     * 注册适配器
     *
     * @param adapterName 适配器名称
     * @param funcName    方法名称
     * @param func        方法体
     */
    public void registerAdapter(String adapterName, String funcName, Function func) {
        ApiAdapterFunc apiAdapterFunc = ADAPTER_MAP.get(adapterName);
        if (Objects.isNull(apiAdapterFunc)) {
            apiAdapterFunc = ApiAdapterFunc.builder()
                    .name(adapterName)
                    .build();
        }
        Map<String, Function> apiFunc = apiAdapterFunc.getApiFunc();
        if (Objects.isNull(apiFunc)) {
            apiFunc = new HashMap<>(Integer.SIZE);
        }
        if (apiFunc.containsKey(funcName)) {
            throw new RuntimeException("adapter【" + adapterName + "】func【" + funcName + "】existed");
        }
        apiFunc.put(funcName, func);
        apiAdapterFunc.setApiFunc(apiFunc);
        ADAPTER_MAP.put(adapterName, apiAdapterFunc);
    }

    /**
     * 执行适配器方法
     *
     * @param adapter 适配器名称
     * @param func 方法名
     * @param t 入参
     * @param <T> 入参类型
     * @param <R> 回参类型
     * @return 执行结果
     */
    public <T, R> R apply(String adapter, String func, T t) {
        return (R) getFunc(adapter, func).apply(t);
    }

    /**
     * 获取方法
     *
     * @param adapter 适配器名称
     * @param func 方法名
     * @return 方法函数
     */
    public Function getFunc(String adapter, String func) {
        ApiAdapterFunc apiAdapterFunc = ADAPTER_MAP.get(adapter);
        if (Objects.isNull(apiAdapterFunc)) {
            throw new NotFoundFuncException("adapter:【" + adapter + "】not found");
        }
        if (MapUtil.isEmpty(apiAdapterFunc.getApiFunc()) || !apiAdapterFunc.getApiFunc().containsKey(func)) {
            throw new NotFoundFuncException("func:【" + func + "】not found in adapter:【" + adapter + "】");
        }
        return apiAdapterFunc.getApiFunc().get(func);
    }
}
