package com.hyl.component.gray.business.rule;

import cn.hutool.core.map.MapUtil;
import com.hyl.component.gray.config.GrayConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 2022-12-04 02:21
 * create by hyl
 * desc: 路由规则
 */
public abstract class GrayRule {

    @Autowired
    protected GrayConfigProperties grayConfigProperties;

    /**
     * 匹配规则
     * Matching rule
     *
     * @param grayTag   当前灰度tag
     * @param grayValue 当前灰度tag解析值
     * @return 是否执行灰度转发
     */
    public boolean matchingRule(String grayTag, String grayValue) {
        Map<String, GrayConfigProperties.FirsTagConfig> firstTag = grayConfigProperties.getFirstTag();
        if (MapUtil.isEmpty(firstTag) || !firstTag.containsKey(grayTag)) {
            return false;
        }
        return true;
    }
}
