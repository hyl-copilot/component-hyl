package com.hyl.component.gray.business.rule;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 2022-12-04 02:53
 * create by hyl
 * desc:
 * @author hyl
 */
@Component
@Slf4j
public class GrayRuleFactory {

    private final Map<String, AbstractGrayRule> grayRules;

    @Autowired
    public GrayRuleFactory(List<AbstractGrayRule> grayRules) {
        if (CollUtil.isEmpty(grayRules)) {
            this.grayRules = new HashMap<>();
            return;
        }
        this.grayRules = grayRules.stream().collect(Collectors.toMap(rule -> {
            Component component = rule.getClass().getAnnotation(Component.class);
            Service service = rule.getClass().getAnnotation(Service.class);
            return Optional.ofNullable(component).map(Component::value).orElse(Optional.ofNullable(service).map(Service::value).orElse(""));
        }, Function.identity(), (e1, e2) -> e2));
    }

    public boolean matchingRule(String rule, String grayTag, String grayValue) {
        AbstractGrayRule grayRule = grayRules.get(rule);
        if (Objects.isNull(grayRule)) {
            return false;
        }
        return grayRule.matchingRule(grayTag, grayValue);
    }


}
