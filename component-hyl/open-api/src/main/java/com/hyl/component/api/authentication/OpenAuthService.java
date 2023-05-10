package com.hyl.component.api.authentication;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAuthService {

    @Resource
    private ThirdAuthConfig thirdAuthConfig;

    public boolean auth(String systemCode, String appKey, String appSecret) {
        if (Objects.isNull(thirdAuthConfig) || CollUtil.isEmpty(thirdAuthConfig.getThirdAuthList())) {
            log.info("第三方系统校验未通过,无任何接口访问权限,systemCode：{}", systemCode);
            return false;
        }
        Map<String, ThirdAuth> thirdAuthMap = thirdAuthConfig.getThirdAuthList().stream()
                .collect(Collectors.toMap(ThirdAuth::getSystemCode, Function.identity()));
        if (!thirdAuthMap.containsKey(systemCode)) {
            log.info("第三方系统校验未通过,无任何接口访问权限,systemCode：{}", systemCode);
            return false;
        }
        ThirdAuth thirdAuth = thirdAuthMap.get(systemCode);
        //使用Objects.equals()方法比较key和secret是否相等
        if (!Objects.equals(appKey, thirdAuth.getAppKey()) || !Objects.equals(appSecret, thirdAuth.getAppSecret())) {
            //校验未通过
            log.info("第三方系统{}校验未通过,systemCode：{},appKey：{},appSecret：{}", thirdAuth.getSystemName(), systemCode, appKey, appSecret);
            return false;
        }
        return true;
    }
}
