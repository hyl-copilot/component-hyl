package com.hyl.component.gray.business.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 2022-12-04 23:23
 * create by hyl
 * desc:
 * @author hyl
 */

@Component
public class LocalCache extends AbstractDataCache {
    public static final Map<String, Set<String>> GRAY_TAG_DATA = new HashMap<>();

    @Override
    public boolean hasGrayTag() {
        return MapUtil.isNotEmpty(GRAY_TAG_DATA);
    }

    @Override
    public boolean isGrayLink(String grayTag, String tagValue) {
        Set<String> links = GRAY_TAG_DATA.get(grayTag);
        return CollUtil.isNotEmpty(links) && links.contains(tagValue);
    }

    @Override
    public void cacheGrayLink(String grayTag, String tagValue) {
        Set<String> links = GRAY_TAG_DATA.get(grayTag);
        if (Objects.isNull(links)){
            links = new HashSet<>();
        }
        links.add(tagValue);
        GRAY_TAG_DATA.put(grayTag, links);
    }

    @Override
    public Set<String> getGrayLink(String grayTag) {
        return GRAY_TAG_DATA.get(grayTag);
    }

    @Override
    public Map<String, Set<String>> getAllGrayLink() {
        return GRAY_TAG_DATA;
    }
}
