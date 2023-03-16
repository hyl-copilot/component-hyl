package com.hyl.component.gray.simple.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hyl.component.gray.business.aspect.BaseGrayAspect;
import com.hyl.component.gray.config.GrayConfigProperties;
import com.hyl.component.gray.simple.annotation.SimpleGray;
import com.hyl.component.gray.simple.rule.AbstractSimpleGrayRule;
import com.hyl.component.gray.util.MatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 2022-12-04 03:22
 * create by hyl
 * desc:
 * @author hyl
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(GrayConfigProperties.class)
public class SimpleGrayAspect extends BaseGrayAspect {

    @Autowired
    private AbstractSimpleGrayRule simpleGrayRule;

    /**
     * 1.定义切面
     */
    @Pointcut("@annotation(com.hyl.component.gray.simple.annotation.SimpleGray)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(simpleGray)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, SimpleGray simpleGray) throws Throwable {
        log.debug("SimpleGrayAspect ...");
        if (!GrayConfigProperties.SWITCH_OPEN.equals(grayConfigProperties.getGraySwitch())
                || CollUtil.isEmpty(grayConfigProperties.getSimpleTag())) {
            return joinPoint.proceed();
        }
        return distributionGrayRequest(joinPoint, simpleGray);
    }

    @Override
    protected <T> boolean matchingRule(T gray, String body) {
        SimpleGray simpleGray = (SimpleGray) gray;
        HttpServletRequest request = getRequest();
        String uri = request.getRequestURI();
        boolean match = false;
        GrayConfigProperties.SimpleTagConfig config = null;
        for (GrayConfigProperties.SimpleTagConfig tagConfig : grayConfigProperties.getSimpleTag()) {
            match = MatchUtil.isMatch(uri, tagConfig.getPath());
            if (match) {
                config = tagConfig;
                break;
            }
        }
        if (!match) {
            return match;
        }
        if (StrUtil.isEmpty(config.getProportion())) {
            //从注解中获取
            config.setProportion(simpleGray.proportion());
        }
        log.debug("请求uri：{} 符合灰度转发规则：{} , 转发比例为{}", uri, config.getPath(), config.getProportion());
        return simpleGrayRule.matchingRule(config,request,body);
    }


    @Override
    public <T> void afterSuccess(T gray, String body) {

    }

}
