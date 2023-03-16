package com.hyl.component.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author hyl
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeanMethod {

    private Object bean;

    private Method method;
}
