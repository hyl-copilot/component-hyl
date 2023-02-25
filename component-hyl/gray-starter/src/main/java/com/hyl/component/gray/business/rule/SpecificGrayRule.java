package com.hyl.component.gray.business.rule;

import cn.hutool.core.collection.CollUtil;
import com.hyl.component.gray.config.GrayConfigProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 2022-12-04 02:22
 * create by hyl
 * desc: 指定
 */
@Component("specific")
public class SpecificGrayRule extends GrayRule {

    @Override
    public boolean matchingRule(String grayTag, String grayValue) {
        if (!super.matchingRule(grayTag, grayValue)) {
            return false;
        }
        GrayConfigProperties.FirsTagConfig grayTagConfig = grayConfigProperties.getFirstTag().get(grayTag);
        if (CollUtil.isEmpty(grayTagConfig.getSpecificValue())) {
            return false;
        }
        Set<String> specificValue = new HashSet<>(grayTagConfig.getSpecificValue());
        return specificValue.contains(grayValue);
    }
}
