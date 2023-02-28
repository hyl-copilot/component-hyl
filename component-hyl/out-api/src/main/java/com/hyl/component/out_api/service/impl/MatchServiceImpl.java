package com.hyl.component.out_api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.hyl.component.out_api.routing.ApiRoutingMatch;
import com.hyl.component.out_api.routing.ApiRoutingServiceI;
import com.hyl.component.out_api.service.MatchService;
import com.hyl.component.out_api.vo.BeanMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    private final static Map<String,BeanMethod> routeCache = new HashMap<>();

    @Resource
    private ApplicationContext applicationContext;


    private BeanMethod getRouteMethod(String route,String key) {
        String cacheKey = route+">"+ key;
        if (routeCache.containsKey(cacheKey)){
            return routeCache.get(cacheKey);
        }
        Map<String, ApiRoutingServiceI> routeBeanMap = applicationContext.getBeansOfType(ApiRoutingServiceI.class);
        if (MapUtil.isEmpty(routeBeanMap)) {
            routeCache.put(cacheKey,null);
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
                BeanMethod beanMethod = BeanMethod.builder()
                        .bean(serviceI)
                        .method(methods[0])
                        .build();
                routeCache.put(cacheKey,beanMethod);
                return beanMethod;
            }
        }
        routeCache.put(cacheKey,null);
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

}
