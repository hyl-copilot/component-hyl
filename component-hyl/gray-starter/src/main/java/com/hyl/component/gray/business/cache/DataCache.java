package com.hyl.component.gray.business.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 2022-12-02 00:37
 * create by hyl
 * desc:
 */
public abstract class DataCache {


    /**
     * 是否有数据
     * @return
     */
    public abstract boolean hasGrayTag();

    public abstract boolean isGrayLink(String grayTag, String tagValue) ;

    public abstract void cacheGrayLink(String grayTag, String tagValue);

    public abstract Set<String> getGrayLink(String grayTag);

    public abstract Map<String, Set<String>> getAllGrayLink();
}
