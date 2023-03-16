package com.hyl.component.gray.business.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hyl.component.gray.business.annotation.DistributionGray;
import com.hyl.component.gray.config.GrayConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 2022-12-01 21:15
 * create by hyl
 * desc:
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(FirstGrayAspect.class)
public class DistributionGrayAspect extends BaseGrayAspect {

    // 1.定义切面
    @Pointcut("@annotation(com.hyl.component.gray.business.annotation.DistributionGray)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(distributionGray)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, DistributionGray distributionGray) throws Throwable {
        ///灰度开关 根据gray body决定
        return distributionGrayRequest(joinPoint, distributionGray);
    }

    /**
     * 灰度路由转发
     *
     * @param joinPoint
     * @param gray
     * @return
     * @throws Throwable
     */
    protected <T> Object distributionGrayRequest(ProceedingJoinPoint joinPoint, T gray) throws Throwable {
        HttpServletRequest request = getRequest();
        String body = super.getBody(request);
        if (Objects.isNull(body)) {
            return joinPoint.proceed();
        }
        Object response = null;
        if (!StrUtil.equals(GrayConfigProperties.MODEL_DISTRIBUTE, grayConfigProperties.getModel())) {
            String filterGrayBody = filterGrayBody(gray, body, request);
            if (Objects.nonNull(filterGrayBody)) {
                //执行本地
                response = joinPoint.proceed(new Object[]{JSONUtil.toBean(filterGrayBody, joinPoint.getArgs()[0].getClass())});
                log.debug("本地执行{}", JSONUtil.toJsonStr(response));
            }
        } else {
            response = joinPoint.proceed();
        }
        //灰度转发
        //更新body 灰度异常走本地处理
        String garyBody = getGaryBody(gray, body, request);
        if (StrUtil.isEmpty(garyBody)) {
            return response;
        }
        try {
            Object result;
            if (StrUtil.isNotEmpty(grayConfigProperties.getGrayPath())) {
                result = simpleGrayRequest(grayConfigProperties.getGrayPath(), request, garyBody);
            } else {
                result = grayByNacosServer(request, garyBody);
            }
            //无异常：灰度成功，缓存灰度业务数据
            afterSuccess(gray, garyBody);
            return result;
        } catch (Exception e) {
            log.error("灰度请求转发失败..继续执行本地业务", e);
            return joinPoint.proceed(new Object[]{JSONUtil.toBean(garyBody, joinPoint.getArgs()[0].getClass())});
        }
    }

    private <T> String getGaryBody(T gray, String body, HttpServletRequest request) {
        if (StrUtil.isEmpty(body)) {
            return null;
        }
        DistributionGray distributionGray = (DistributionGray) gray;
        //拆分数据path
        String distributionPath = distributionGray.distributionJsonPath();
        if (distributionPath.split("\\.").length < 2) {
            log.warn("distributionPath is need *.* but this path is :{} ,you can use LinkGray", distributionPath);
            return null;
        }
        Object bodyObj = distributionGray.bodyType() == 0 ? JSONUtil.parseObj(body) : JSONUtil.parseArray(body);
        Object grayBody = buildGrayBody(bodyObj, distributionGray.distributionTag(), distributionPath);
        if (Objects.isNull(grayBody)) {
            return null;
        }
        return JSONUtil.toJsonStr(grayBody);
    }

    private <T> String filterGrayBody(T gray, String body, HttpServletRequest request) {
        if (StrUtil.isEmpty(body)) {
            return body;
        }
        DistributionGray distributionGray = (DistributionGray) gray;
        //拆分数据path
        String distributionPath = distributionGray.distributionJsonPath();
        if (distributionPath.split("\\.").length < 2) {
            return body;
        }
        Object bodyObj = distributionGray.bodyType() == 0 ? JSONUtil.parseObj(body) : JSONUtil.parseArray(body);
        Object requestBody = filterGrayBody(bodyObj, distributionGray.distributionTag(), distributionPath);
        if (Objects.isNull(requestBody)) {
            return null;
        }
        return JSONUtil.toJsonStr(requestBody);
    }

    private Object filterGrayBody(Object bodyObj, String grayTag, String distributionPath) {
        return selectBody(bodyObj, grayTag, distributionPath, false);
    }


    private Object buildGrayBody(Object bodyObj, String grayTag, String distributionPath) {
        return selectBody(bodyObj, grayTag, distributionPath, true);
    }


    private Object selectBody(Object bodyObj, String grayTag, String distributionPath, boolean isGray) {
        String[] path = distributionPath.split("\\.");
        String field = path[0];
        String next = path[1];
        List<JSONObject> list = converToList(bodyObj);
        List<JSONObject> result = new ArrayList<>(list.size());
        for (JSONObject item : list) {
            JSONObject selectBody = BeanUtil.copyProperties(item, JSONObject.class);
            selectBody.remove(field);
            String subPath = distributionPath.replaceFirst(field + "\\.", "");
            if (path.length == 2) {
                JSONArray jsonArray = item.getJSONArray(field);
                List<JSONObject> selectData = jsonArray.toList(JSONObject.class).stream().filter(obj -> {
                    if (isGray) {
                        return dataCache.isGrayLink(grayTag, obj.getStr(next));
                    } else {
                        return !dataCache.isGrayLink(grayTag, obj.getStr(next));
                    }
                }).collect(Collectors.toList());
                if (CollUtil.isEmpty(selectData)) {
                    return null;
                }
                selectBody.set(field, selectData);
            } else {
                selectBody.set(field, buildGrayBody(item.get(next), grayTag, subPath));
            }
            result.add(selectBody);
        }
        if (CollUtil.isEmpty(result)) {
            return null;
        }
        if (bodyObj instanceof JSONArray) {
            return result;
        } else {
            return result.get(0);
        }
    }

    private List<JSONObject> converToList(Object bodyObj) {
        if (bodyObj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) bodyObj;
            return jsonArray.toList(JSONObject.class);
        }
        List<JSONObject> list = new ArrayList<>();
        list.add((JSONObject) bodyObj);
        return list;
    }

    @Override
    public <T> void afterSuccess(T gray, String body) {
        DistributionGray distributionGray = (DistributionGray) gray;
        if (StrUtil.isEmpty(distributionGray.grayTag()) || StrUtil.isEmpty(distributionGray.jsonPath())) {
            //无须标记为灰度数据
            return;
        }
        addGrayTag(distributionGray.grayTag(), distributionGray.jsonPath(), body);
    }

}
