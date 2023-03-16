package com.hyl.component.gray.simple.rule;

import com.hyl.component.gray.config.GrayConfigProperties;
import javax.servlet.http.HttpServletRequest;

/**
 * 2022-12-04 13:45
 * create by hyl
 * desc:
 * @author hyl
 */
public abstract class AbstractSimpleGrayRule {

    /**
     * 其他规则可继承 SimpleGrayRule 并重写该方法
     *
     * @param config config配置
     * @param request 请求
     * @param body 请求体
     * @return 是否匹配规则
     */
    public abstract boolean matchingRule(GrayConfigProperties.SimpleTagConfig config, HttpServletRequest request, String body);

}
