package com.hyl.component.gray.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 2022-12-02 00:06
 * create by hyl
 * desc:
 */
@Component
@Slf4j
public class NacosClientService {

    @Autowired
    private GrayConfigProperties grayConfigProperties;

    @Value("${spring.application.name}")
    private String serviceId;

    private static List<ServiceInstance> grayServices = new ArrayList<>();;


    /**
     * 订阅 gary 服务列表
     * @return
     */
    @PostConstruct
    public void subscribeGrayServer(){
        GrayConfigProperties.NacosGaryConfig grayCofig = grayConfigProperties.getGrayNacos();
        if (Objects.isNull(grayCofig)){
            log.warn("gray nacos config not found");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr",Optional.ofNullable(grayCofig.getAddr()).orElse("localhost:8848"));
            properties.setProperty("namespace",grayCofig.getNamespace());
            //监听服务（启动，修改，关闭都会监听到）
            NamingService namingService = NamingFactory.createNamingService(properties);
            namingService.subscribe(serviceId, grayCofig.getGroup(), event -> {
                try {
                    //获取所有该服务的列表
                    List<Instance> grayInstances = namingService.getAllInstances(serviceId,grayCofig.getGroup());
                    if (CollUtil.isEmpty(grayInstances)){
                        return;
                    }
                    grayServices = NacosServiceDiscovery.hostToServiceInstanceList(grayInstances,serviceId);
                } catch (NacosException e) {
                    e.printStackTrace();
                }
            });
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
        }
    }

    public ServiceInstance getOneGrayInstance(){
        log.debug("当前灰度服务列表：{}", JSONUtil.toJsonStr(grayServices));
        Map<String, String> metadata = grayConfigProperties.getGrayNacos().getMetadata();
        try {
            //获取注册serviceId
            //获取负载节点
            if (CollUtil.isNotEmpty(grayServices) && MapUtil.isNotEmpty(metadata)){
                grayServices = grayServices.stream().filter(serviceInstance -> {
                    Map<String, String> instanceMetadata = serviceInstance.getMetadata();
                    for (Map.Entry<String, String> entry : metadata.entrySet()) {
                        if (StrUtil.isNotBlank(entry.getValue()) &&
                                !StrUtil.equals(instanceMetadata.get(entry.getKey()),entry.getValue())){
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
            }
            //获取灰度服务
            if (CollUtil.isEmpty(grayServices)) {
                return null;
            }
            //随机返回一个节点
            return grayServices.stream().findAny().get();
        }catch (Exception e){
            log.error("获取灰度节点异常",e);
        }
        return null;
    }



}
