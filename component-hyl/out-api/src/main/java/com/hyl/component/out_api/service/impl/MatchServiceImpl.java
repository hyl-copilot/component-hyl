package com.hyl.component.out_api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.hyl.component.out_api.adapter.ApiAdapterMatch;
import com.hyl.component.out_api.adapter.ApiAdapterServiceI;
import com.hyl.component.out_api.autoconfigure.MatchConfiguration;
import com.hyl.component.out_api.routing.ApiRoutingMatch;
import com.hyl.component.out_api.routing.ApiRoutingServiceI;
import com.hyl.component.out_api.service.MatchService;
import com.hyl.component.out_api.vo.BeanMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Resource
    private ApplicationContext applicationContext;


    private BeanMethod getRouteMethod(String route,String key) {
        Map<String, ApiRoutingServiceI> routeBeanMap = applicationContext.getBeansOfType(ApiRoutingServiceI.class);
        if (MapUtil.isEmpty(routeBeanMap)) {
            return null;
        }
        //查询所有类中所有含有@ApiRoutingMatch注解的方法
        for (ApiRoutingServiceI serviceI : routeBeanMap.values()) {
            Method[] methods = ReflectUtil.getMethods(serviceI.getClass(), method -> {
                ApiRoutingMatch annotation = method.getAnnotation(ApiRoutingMatch.class);
                if (Objects.isNull(annotation)) {
                    return false;
                }
                return StrUtil.equals(route, annotation.route()) && StrUtil.equals(key, annotation.key());
            });
            if (ArrayUtil.isNotEmpty(methods)) {
                return BeanMethod.builder()
                        .bean(serviceI)
                        .method(methods[0])
                        .build();
            }
        }
        return null;
    }


    private BeanMethod getAdapterMethod(String adapter,String key) {
        Map<String, ApiAdapterServiceI> adapterBeanMap = applicationContext.getBeansOfType(ApiAdapterServiceI.class);
        if (MapUtil.isEmpty(adapterBeanMap)) {
            return null;
        }
        //查询所有类中所有含有@ApiAdapterMatch注解的方法
        for (ApiAdapterServiceI serviceI : adapterBeanMap.values()) {
            Method[] methods = ReflectUtil.getMethods(serviceI.getClass(), method -> {
                ApiAdapterMatch annotation = method.getAnnotation(ApiAdapterMatch.class);
                if (Objects.isNull(annotation)) {
                    return false;
                }
                return StrUtil.equals(adapter, annotation.adapter()) && StrUtil.equals(key, annotation.key());
            });
            if (ArrayUtil.isNotEmpty(methods)) {
                return BeanMethod.builder()
                        .bean(serviceI)
                        .method(methods[0])
                        .build();
            }
        }
        return null;
    }

    @Override
    public boolean matchingRule(String route,String key) {
        BeanMethod adapterMethod = getRouteMethod(route,key);
        return Objects.nonNull(adapterMethod);
    }

    @Override
    public Object routing(String route,String key, Object[] args) {
        BeanMethod routeMethod = getRouteMethod(route,key);
        return ReflectUtil.invoke(routeMethod.getBean(), routeMethod.getMethod(), args);
    }

    @Override
    public boolean matchingAdapter(String adapter,String key) {
        BeanMethod adapterMethod = getAdapterMethod(adapter,key);
        return Objects.nonNull(adapterMethod);
    }

    @Override
    public Object execute(String adapter,String key, Object[] args) {
        BeanMethod adapterMethod = getAdapterMethod(adapter,key);
        return ReflectUtil.invoke(adapterMethod.getBean(), adapterMethod.getMethod(), args);
    }
}
