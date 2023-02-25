package com.hyl.component.gray.business.rule;

import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Component;

/**
 * 2022-12-04 02:22
 * create by hyl
 * desc: 随机
 */
@Component("random")
public class RandomGrayRule extends GrayRule {

    @Override
    public boolean matchingRule(String grayTag, String grayValue) {
        if (!super.matchingRule(grayTag, grayValue)) {
            return false;
        }
        return RandomUtil.randomBoolean();
    }
}
