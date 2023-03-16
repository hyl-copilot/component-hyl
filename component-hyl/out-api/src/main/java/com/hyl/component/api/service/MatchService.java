package com.hyl.component.api.service;

/**
 * @author hyl
 */
public interface MatchService {
    /**
     * 是否匹配路由规则
     * @param  route 路由点
     * @param key 路由值
     * @return 是否路由
     */
    boolean matchingRule(String route,String key);

    /**
     * 路由转发
     * @param route 路由点
     * @param key 路由值
     * @param args 参数
     * @return 执行路由方法
     */
    Object routing(String route,String key, Object[] args);

}
