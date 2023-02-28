package com.hyl.component.out_api.service;

public interface MatchService {
    /**
     * 是否匹配路由规则
     * @return
     */
    boolean matchingRule(String route,String key);

    /**
     * 路由转发
     * @param args
     * @return
     */
    Object routing(String route,String key, Object[] args);

}
