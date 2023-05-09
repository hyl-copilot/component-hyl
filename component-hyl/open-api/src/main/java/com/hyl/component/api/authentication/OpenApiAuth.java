package com.hyl.component.api.authentication;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther hyl
 * @create 2023-05-09
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface OpenApiAuth {

    String key_field() default "appKey";

    String secret_field() default "appSecret";
}
