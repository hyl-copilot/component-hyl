package com.hyl.component.gateway.config;

import lombok.Data;

import java.util.List;

@Data
public class ThirdAuth {
    private String systemCode;
    private String systemName;
    private String appKey;
    private String appSecret;
    private List<String> urlList;
}