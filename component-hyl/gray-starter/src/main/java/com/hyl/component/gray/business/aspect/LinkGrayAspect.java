package com.hyl.component.gray.business.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hyl.component.gray.business.annotation.LinkGray;
import com.hyl.component.gray.business.cache.AbstractDataCache;
import com.hyl.component.gray.config.GrayConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 2022-12-01 21:15
 * create by hyl
 * desc:
 * @author hyl
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(FirstGrayAspect.class)
public class LinkGrayAspect extends BaseGrayAspect {

    @Autowired
    private AbstractDataCache dataCache;


    /**
     * 1.定义切面
     */
    @Pointcut("@annotation(com.hyl.component.gray.business.annotation.LinkGray)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(linkGray)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, LinkGray linkGray) throws Throwable {
        //灰度开关
        if (!GrayConfigProperties.SWITCH_OPEN.equals(grayConfigProperties.getGraySwitch())
                || !dataCache.hasGrayTag()) {
            return joinPoint.proceed();
        }
        //灰度开关 根据firstGray决定
        String linkTag = linkGray.linkTag();
        String linkJsonPath = linkGray.linkJsonPath();
        String body = getBody(getRequest());
        String tagValue = Optional.ofNullable(JSONUtil.getByPath(JSONUtil.parseObj(body), linkJsonPath)).map(Object::toString).orElse("");
        if (StrUtil.isEmpty(tagValue)) {
            return joinPoint.proceed();
        }
        boolean isGrayLink = dataCache.isGrayLink(linkTag, tagValue);
        if (!isGrayLink) {
            return joinPoint.proceed();
        }
        return distributionGrayRequest(joinPoint, linkGray);

    }

    @Override
    public <T> void afterSuccess(T gray, String body) {
        LinkGray linkGray = (LinkGray) gray;
        if (StrUtil.isEmpty(linkGray.grayTag()) || StrUtil.isEmpty(linkGray.jsonPath())) {
            //无须标记为灰度数据
            return;
        }
        addGrayTag(linkGray.grayTag(), linkGray.jsonPath(), body);
    }

}
