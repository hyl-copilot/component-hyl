package com.hyl.component.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
//springfox提供了一个接口SwaggerResourcesProvider，用户可自行编写api资源，这正是我们需要的。
public class ResourcesProvider implements SwaggerResourcesProvider {
    public static final String API_URI = "/v3/api-docs"; //swagger2对应的是/v2/api-docs
    @Value("${spring.application.name}")
    private String gatewayApplicationName;
    @Autowired
    private RouteLocator routeLocator;

    @Override
    public List<SwaggerResource> get() {
        //保存我们手动设置的api文档资源信息
        List<SwaggerResource> resources = new ArrayList<>();
        //取出gateway的route
        routeLocator.getRoutes()
                //将除了网关外的其他微服务对应的路由筛选出来
                .filter(route -> route.getUri().getHost() != null
                        && !gatewayApplicationName.equals(route.getUri().getHost())
                        && !route.getId().startsWith("ReactiveCompositeDiscoveryClient")
                )
                .subscribe(route -> {
                    //获取对应路由的servlet.context-path
                    String contextPath = route.getUri().getHost();
                    //拼接对应微服务的api文档路径，每个微服务对应一个SwaggerResource，但这里的路径的ip地址和端口是网关的，所以我们在网关的swagger-ui中可以看到。然后由网关转发到对应的微服务地址去。
                    resources.add(swaggerResource(route.getId(),
                            "/" + contextPath + API_URI));
                });
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String url) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setUrl(url);
        swaggerResource.setSwaggerVersion("3.0");
        return swaggerResource;
    }
}