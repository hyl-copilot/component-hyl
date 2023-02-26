package com.hyl.component.out_api.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.hyl.component.out_api.routing.ApiRoutingKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class AnnotationUtil {
    public static String getAnnotationKey(ProceedingJoinPoint joinPoint, Class keyAnnotationClass) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        String key = null;
        L:
        for (int index = 0; index < parameterAnnotations.length; index++) {
            for (Annotation annotation : parameterAnnotations[index]) {
                if (Objects.equals(keyAnnotationClass, annotation.annotationType())) {
                    key = Optional.ofNullable(args[index]).map(Object::toString).orElse(null);
                    break L;
                }
            }
        }
        if (StrUtil.isBlank(key)) {
            key = getParamsKey(joinPoint.getArgs(),keyAnnotationClass);
        }
        return key;
    }

    private static String getParamsKey(Object[] args, Class keyAnnotationClass) {
        for (Object arg : args) {
            Field[] fields = ReflectUtil.getFields(arg.getClass());
            if (ArrayUtil.isEmpty(fields)) {
                continue;
            }
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(keyAnnotationClass);
                if (Objects.nonNull(annotation)){
                    return Optional.ofNullable(ReflectUtil.getFieldValue(arg,field))
                            .map(Object::toString).orElse(null);
                }
            }
        }
        return null;
    }

}
