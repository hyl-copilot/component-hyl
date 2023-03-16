package com.hyl.component.gray.business.cache;

import java.util.Map;
import java.util.Set;

/**
 * 2022-12-02 00:37
 * create by hyl
 * desc:
 * @author hyl
 */
public abstract class AbstractDataCache {


    /**
     * 是否有数据
     * @return 是否有数据
     */
    public abstract boolean hasGrayTag();

    /**
     * 是否灰度
     * @param grayTag tag标签
     * @param tagValue tag值
     * @return 是否灰度
     */
    public abstract boolean isGrayLink(String grayTag, String tagValue) ;

    /**
     * 缓存灰度标识
     * @param grayTag tag标签
     * @param tagValue tag值
     */
    public abstract void cacheGrayLink(String grayTag, String tagValue);

    /**
     * 获取灰度标识
     * @param grayTag tag标签
     * @return 灰度数据
     */
    public abstract Set<String> getGrayLink(String grayTag);

    /**
     * 获取所有灰度数据
     * @return 灰度数据
     */
    public abstract Map<String, Set<String>> getAllGrayLink();
}
