package com.hyl.component.gray.business.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hyl.component.gray.business.cache.AbstractDataCache;
import com.hyl.component.gray.config.GrayConfigProperties;
import com.hyl.component.gray.config.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 2022-12-02 01:59
 * create by hyl
 * desc:
 * @author hyl
 */

@Slf4j
public abstract class BaseGrayAspect {

    @Autowired
    protected GrayConfigProperties grayConfigProperties;

    @Resource
    protected NacosClientService nacosClientService;

    @Autowired
    protected AbstractDataCache dataCache;


    /**
     * 灰度路由转发
     *
     * @param joinPoint 切点
     * @param gray 灰度标签
     * @return 返回结果
     * @throws Throwable
     */
    protected <T> Object distributionGrayRequest(ProceedingJoinPoint joinPoint, T gray) throws Throwable {
        try {
            HttpServletRequest request = getRequest();
            String body = getBody(request);
            if (!matchingRule(gray, body)) {
                return joinPoint.proceed();
            }
            Object result;
            if (StrUtil.isNotEmpty(grayConfigProperties.getGrayPath())) {
                result = simpleGrayRequest(grayConfigProperties.getGrayPath(), request, body);
            } else {
                result = grayByNacosServer(request, body);
            }
            log.debug("请求:{} 转发成功：{}", request.getRequestURI(), result);
            //无异常：灰度成功，缓存灰度业务数据
            afterSuccess(gray, body);
            if (!StrUtil.equals(GrayConfigProperties.MODEL_DISTRIBUTE, grayConfigProperties.getModel())) {
                return result;
            }
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("灰度请求转发失败..继续执行本地业务", e);
            return joinPoint.proceed();
        }
    }

    protected <T> boolean matchingRule(T gray, String body) {
        return true;
    }

    /**
     * 成功后执行
     * @param gray 灰度标签
     * @param body 执行参数
     * @param <T> 返回参数类型
     */
    public abstract <T> void afterSuccess(T gray, String body);


    protected Set<String> getTagValues(String grayTag, String jsonPath, String body) {
        Set<String> tagSet = new HashSet<>();
        if (body.startsWith(CharPool.BRACKET_START+"")) {
            JSONArray jsonArray = JSONUtil.parseArray(body);
            jsonArray.forEach(obj -> {
                String tagValue = Optional.ofNullable(JSONUtil.getByPath(JSONUtil.parseObj(obj.toString()), jsonPath)).map(Object::toString).orElse("");
                tagSet.add(tagValue);
            });
        } else {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            String tagValue = Optional.ofNullable(JSONUtil.getByPath(jsonObject, jsonPath)).map(Object::toString).orElse("");
            tagSet.add(tagValue);
        }
        return tagSet;
    }

    protected void addGrayTag(String grayTag, String jsonPath, String body) {
        log.debug("request body :{}", body);
        Set<String> tagSet = getTagValues(grayTag, jsonPath, body);
        if (CollUtil.isEmpty(tagSet)) {
            return;
        }
        tagSet.forEach(tagValue -> {
            dataCache.cacheGrayLink(grayTag, tagValue);
        });
        log.debug("grayTag:{}", JSONUtil.toJsonStr(dataCache.getAllGrayLink()));

    }

    protected Object grayByNacosServer(HttpServletRequest request, String body) {
        ServiceInstance serviceInstance = nacosClientService.getOneGrayInstance();
        if (Objects.nonNull(serviceInstance)) {
            String url = serviceInstance.getUri().toString() + request.getRequestURI();
            log.debug("nacos server dispatcher:{} body:{}", url, body);
            return HttpUtil.post(url, body);
        }
        throw new RuntimeException("not find gray server");
    }


    protected String getBody(HttpServletRequest request) throws IOException {
        try (InputStream is = request.getInputStream()) {
            return IoUtil.read(is, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("read http request failed.", ex);
            throw ex;
        }
    }

    protected Object simpleGrayRequest(String grayPath, HttpServletRequest request, String body) {
        String url = grayPath + request.getRequestURI();
        log.debug("config path dispatcher:{} body:{}", url, body);
        return HttpUtil.post(url, body);
    }


    /**
     * 获取当前请求
     * @return 获取当前请求
     */
    protected HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

}
