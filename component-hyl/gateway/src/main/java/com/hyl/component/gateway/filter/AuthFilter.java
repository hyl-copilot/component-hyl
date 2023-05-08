package com.hyl.component.gateway.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hyl.component.gateway.config.GatewayConfig;
import com.hyl.component.gateway.config.ThirdAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Order(1) //拦截器优先级；值越小优先级越高
@Component
@Slf4j
public class AuthFilter implements GlobalFilter {

    @Resource
    private GatewayConfig gatewayConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request
        ServerHttpRequest request = exchange.getRequest();
        //2.获取请求url
        String url = request.getURI().getPath();
        //3.判断是否是白名单url
        List<String> whiteList = gatewayConfig.getWhiteList();
        if (whiteList != null && whiteList.contains(url)) {
            //白名单通过
            return chain.filter(exchange);
        }
        //获取 token 和 systemCode
        String token = request.getHeaders().getFirst("token");
        if (token != null) {
            return checkToken(exchange, chain, request, url, token);
        }
        String systemCode = request.getHeaders().getFirst("systemCode");
        if (systemCode != null && !"".equals(systemCode)) {
            return checkThirdAuth(exchange, chain, request, url, systemCode);
        }
        //设置校验未通过code 401 认证未通过
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }


    /**
     * token校验
     *
     * @param exchange
     * @param chain
     * @param request
     * @param url
     * @param token
     * @return
     */
    private Mono<Void> checkToken(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, String url, String token) {
        //根据redis中的token判断是否过期
        String userInfo = redisTemplate.opsForValue().get(token);
        if (userInfo == null) {
            log.info("token已过期");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            JSONObject body = new JSONObject();
            body.set("code",1);
            body.set("msg","token已过期,请重新登录");
            body.set("data","");
            byte[] bytes = JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
        //未过期、请求通过
        //将用户信息放入request中
        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header("userInfo", userInfo);
        //给token续期
        redisTemplate.expire(token, gatewayConfig.getExpireTime(), TimeUnit.SECONDS);
        return chain.filter(exchange);
    }

    /**
     * 第三方系统校验
     *
     * @param exchange
     * @param chain
     * @param request
     * @param url
     * @param systemCode
     * @return
     */
    private Mono<Void> checkThirdAuth(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, String url, String systemCode) {
        List<ThirdAuth> thirdAuthList = gatewayConfig.getThirdAuthList();
        if (thirdAuthList == null || thirdAuthList.size() == 0) {
            log.info("未开通任何第三方系统接口访问权限");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        Map<String, ThirdAuth> thirdAuthMap = thirdAuthList.stream()
                .collect(Collectors.toMap(ThirdAuth::getSystemCode, Function.identity()));
        if (!thirdAuthMap.containsKey(systemCode)) {
            log.info("第三方系统校验未通过,无任何接口访问权限,systemCode：{}", systemCode);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        ThirdAuth thirdAuth = thirdAuthMap.get(systemCode);
        //请求头中是否有密钥
        String key = request.getHeaders().getFirst("key");
        String secret = request.getHeaders().getFirst("secret");
        //使用Objects.equals()方法比较key和secret是否相等
        if (!Objects.equals(key, thirdAuth.getKey()) || !Objects.equals(secret, thirdAuth.getSecret())) {
            //校验未通过
            log.info("第三方系统校验未通过,systemCode：{},key：{},secret：{}", systemCode, key, secret);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        if (!thirdAuth.getUrlList().contains(url)) {
            //校验未通过
            log.info("第三方系统权限不足,systemCode：{},url：{}", systemCode, url);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //请求通过
        return chain.filter(exchange);
    }

}

