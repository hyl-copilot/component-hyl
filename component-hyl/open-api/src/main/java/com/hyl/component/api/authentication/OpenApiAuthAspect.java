package com.hyl.component.api.authentication;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


/**
 * @author hyl
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix="open-api-config",name = "enable", havingValue = "true")
public class OpenApiAuthAspect {

    @Resource
    private OpenAuthService openAuthService;

    /**
     * 1.定义切面
     */
    @Pointcut("@annotation(com.hyl.component.api.authentication.OpenApiAuth)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(openApiAuth)")
    public Object aroundCut(ProceedingJoinPoint joinPoint, OpenApiAuth openApiAuth) throws Throwable {
        log.debug("openApiAuthAspect...");
        String keyField = openApiAuth.key_field();
        String secretField = openApiAuth.secret_field();
        log.debug("keyField:{},secretField:{}", keyField, secretField);
        HttpServletRequest request = getRequest();
        //从请求头中获取keyField secretField
        String systemCode = Optional.ofNullable(request.getHeader(keyField)).orElse("public");
        String appKey = request.getHeader(keyField);
        String appSecret = request.getHeader(secretField);
        if (!openAuthService.auth(systemCode, appKey, appSecret)) {
            //认证不通过
            HttpServletResponse response = getResponse();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            JSONObject body = new JSONObject();
            body.set("code", 1);
            body.set("msg", "token已过期,请重新登录");
            body.set("data", "");
            return body;
        }
        return joinPoint.proceed();
    }


    /**
     * 获取当前请求
     *
     * @return 获取当前请求
     */
    protected HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取当前请求
     *
     * @return 获取当前请求
     */
    protected HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getResponse();
    }
}
