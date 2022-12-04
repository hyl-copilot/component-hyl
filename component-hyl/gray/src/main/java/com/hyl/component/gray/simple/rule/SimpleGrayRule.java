package com.hyl.component.gray.simple.rule;

import com.hyl.component.gray.config.GrayConfigProperties;
import javax.servlet.http.HttpServletRequest;

/**
 * 2022-12-04 13:45
 * create by hyl
 * desc:
 */
public abstract class SimpleGrayRule {

    /**
     * 其他规则可继承 SimpleGrayRule 并重写该方法
     *
     * @param config
     * @param request
     * @param body
     * @return
     */
    public abstract boolean matchingRule(GrayConfigProperties.SimpleTagConfig config, HttpServletRequest request, String body);

}
