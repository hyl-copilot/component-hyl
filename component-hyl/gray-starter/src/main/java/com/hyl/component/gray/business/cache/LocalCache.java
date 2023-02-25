package com.hyl.component.gray.business.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 2022-12-04 23:23
 * create by hyl
 * desc:
 */

@Component
public class LocalCache extends DataCache {
    public static Map<String, Set<String>> grayTagData = new HashMap<>();

    @Override
    public boolean hasGrayTag() {
        return MapUtil.isNotEmpty(grayTagData);
    }

    @Override
    public boolean isGrayLink(String grayTag, String tagValue) {
        Set<String> links = grayTagData.get(grayTag);
        return CollUtil.isNotEmpty(links) && links.contains(tagValue);
    }

    @Override
    public void cacheGrayLink(String grayTag, String tagValue) {
        Set<String> links = grayTagData.get(grayTag);
        if (Objects.isNull(links)){
            links = new HashSet<>();
        }
        links.add(tagValue);
        grayTagData.put(grayTag, links);
    }

    @Override
    public Set<String> getGrayLink(String grayTag) {
        return grayTagData.get(grayTag);
    }

    @Override
    public Map<String, Set<String>> getAllGrayLink() {
        return grayTagData;
    }
}
