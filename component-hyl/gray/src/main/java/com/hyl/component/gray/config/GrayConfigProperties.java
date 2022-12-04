package com.hyl.component.gray.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 2022-12-01 21:37
 * create by hyl
 * desc:
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gray")
@ConditionalOnProperty(prefix = "gray", name = "enable", havingValue = "true")
public class GrayConfigProperties {

    /**
     * 模式：
     * distribute 分发
     * forward 转发
     */
    public static final String MODEL_DISTRIBUTE = "distribute";
    public static final String SWITCH_OPEN = "open";

    @Value("${gray.switch:close}")
    private String graySwitch;

    @Value("${gray.model:distribute}")
    private String model;

    /**
     * 优先级 grayPath > grayNacos
     */
    private String grayPath;

    private NacosGaryConfig grayNacos;


    private List<SimpleTagConfig> simpleTag;


    @Data
    public static class SimpleTagConfig {
        /**
         * tag 支持 * ？通配符匹配
         */
        private String path;
        /**
         * 百分比 10%
         */
        private String proportion;
    }

    private Map<String, FirsTagConfig> firstTag;


    @Data
    public static class FirsTagConfig {
        /**
         * 是否启用灰度测试
         */
        private boolean open;
        /**
         * 灰度规则 : random(随机) ，orderly(有序)，specific(指定)
         */
        private String rule;
        /**
         * # rule 为 specific时配置生效
         */
        List<String> specificValue;
        /**
         * 百分比 10%
         */
        private String proportion;
        /**
         * 灰度tag 缓存前缀，默认gray_{tag}:
         */
        private String prefix;
        /**
         * 灰度tag 缓存时间，不配置默认缓存7天
         */
        private Long cacheTime;

    }

    @Data
    public static class NacosGaryConfig {
        private String addr;

        private String namespace;

        private String group;

        /**
         * 元数据
         */
        private Map<String, String> metadata;
    }


}
