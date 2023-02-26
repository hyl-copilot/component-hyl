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

    /**
     * 匹配adapter适配器
     * @return
     */
    boolean matchingAdapter(String adapter,String key);

    /**
     * 执行适配器
     * @param args
     * @return
     */
    Object execute(String adapter,String key, Object[] args);

}