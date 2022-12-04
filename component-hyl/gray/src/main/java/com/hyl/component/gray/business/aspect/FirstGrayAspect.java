package com.hyl.component.gray.business.aspect;

import cn.hutool.core.map.MapUtil;
import com.hyl.component.gray.business.annotation.FirstGray;
import com.hyl.component.gray.business.rule.GrayRuleFactory;
import com.hyl.component.gray.config.GrayConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 2022-12-01 21:15
 * create by hyl
 * desc:
 */

@Slf4j
@Aspect
@Component
@ConditionalOnClass(GrayConfigProperties.class)
public class FirstGrayAspect extends BaseGrayAspect {

    @Autowired
    private GrayRuleFactory grayRuleFactory;

    // 1.定义切面
    @Pointcut("@annotation(com.hyl.component.gray.business.annotation.FirstGray)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(firstGray)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, FirstGray firstGray) throws Throwable {
        //灰度开关
        if (!GrayConfigProperties.SWITCH_OPEN.equals(grayConfigProperties.getGraySwitch())) {
            return joinPoint.proceed();
        }
        log.debug("灰度模式已启用...");
        return distributionGrayRequest(joinPoint, firstGray);
    }

    @Override
    protected <T> boolean matchingRule(T gray, String body) {
        FirstGray firstGray = (FirstGray) gray;
        Map<String, GrayConfigProperties.FirsTagConfig> firstTag = grayConfigProperties.getFirstTag();
        if (MapUtil.isEmpty(firstTag) || !firstTag.containsKey(firstGray.grayTag())) {
            return false;
        }
        GrayConfigProperties.FirsTagConfig grayTagConfig = firstTag.get(firstGray.grayTag());
        //根据body解析tag
        Set<String> tagValues = getTagValues(firstGray.grayTag(), firstGray.jsonPath(), body);
        for (String tagValue : tagValues) {
            if (grayRuleFactory.matchingRule(grayTagConfig.getRule(), firstGray.grayTag(), tagValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> void afterSuccess(T gray, String body) {
        FirstGray firstGray = (FirstGray) gray;
        addGrayTag(firstGray.grayTag(), firstGray.jsonPath(), body);
    }
}
