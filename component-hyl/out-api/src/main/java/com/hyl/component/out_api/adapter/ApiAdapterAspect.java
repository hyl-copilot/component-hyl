package com.hyl.component.out_api.adapter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.hyl.component.out_api.autoconfigure.MatchConfiguration;
import com.hyl.component.out_api.routing.ApiRoutingKey;
import com.hyl.component.out_api.service.MatchService;
import com.hyl.component.out_api.util.AnnotationUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
@ConditionalOnClass(MatchConfiguration.class)
public class ApiAdapterAspect {


    @Resource
    private MatchService matchService;

    // 1.定义切面
    @Pointcut("@annotation(com.hyl.component.out_api.adapter.ApiAdapterMatch)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(adapterMatch)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, ApiAdapterMatch adapterMatch) throws Throwable {
        String key = AnnotationUtil.getAnnotationKey(joinPoint,ApiAdapterKey.class);
        log.debug("adapterAspect key{}...",key);
        if (Objects.nonNull(key) && matchService.matchingAdapter(adapterMatch.adapter(),key)){
            return matchService.execute(adapterMatch.adapter(),key,joinPoint.getArgs());
        }
        return joinPoint.proceed();
    }


}
