package com.hyl.component.gray.simple.rule;

import com.hyl.component.gray.config.GrayConfigProperties;
import com.hyl.component.gray.util.MatchUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 2022-12-05 00:42
 * create by hyl
 * desc:
 */
public class DefaultSimpleGrayRule extends SimpleGrayRule{

    private static double count = 0;
    /**
     * 其他规则可继承 SimpleGrayRule 并重写该方法
     * @param config
     * @param request
     * @param body
     * @return
     */
    @Override
    public boolean matchingRule(GrayConfigProperties.SimpleTagConfig config, HttpServletRequest request, String body) {
        return matchingRule(config);
    }

    public boolean matchingRule(GrayConfigProperties.SimpleTagConfig config) {
        if (Objects.isNull(config) || MatchUtil.getProportion(config.getProportion()) == 0) {
            return false;
        }
        double step = MatchUtil.getProportion(config.getProportion());
        if (step == 1) {
            return true;
        }
        count += step;
        if (count >= 1) {
            count = 0;
            return true;
        }
        return false;
    }
}
