package com.hyl.component.api.routing;

import com.hyl.component.api.autoconfigure.MatchConfiguration;
import com.hyl.component.api.service.MatchService;
import com.hyl.component.api.util.AnnotationUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author hyl
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(MatchConfiguration.class)
public class ApiRoutingAspect {

    @Resource
    private MatchService matchService;

    /**
     * 1.定义切面
     */
    @Pointcut("@annotation(com.hyl.component.api.routing.ApiRouting)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(apiRouting)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, ApiRouting apiRouting) throws Throwable {
        String key = AnnotationUtil.getAnnotationKey(joinPoint,ApiRoutingKey.class);
        log.debug("routingAspect key:{}...", key);
        if (Objects.nonNull(key) && matchService.matchingRule(apiRouting.route(), key)) {
            return matchService.routing(apiRouting.route(), key, joinPoint.getArgs());
        }
        return joinPoint.proceed();
    }

}
