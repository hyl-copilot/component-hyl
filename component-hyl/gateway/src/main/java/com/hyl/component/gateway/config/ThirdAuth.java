package com.hyl.component.gateway.config;

import lombok.Data;

import java.util.List;

@Data
public class ThirdAuth {
    private String systemCode;
    private String systemName;
    private String key;
    private String secret;
    private List<String> urlList;
}