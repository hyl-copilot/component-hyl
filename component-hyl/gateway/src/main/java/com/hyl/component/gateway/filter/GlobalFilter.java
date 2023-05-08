package com.hyl.component.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GlobalFilter {

	/**
	 * Process the Web request and (optionally) delegate to the next {@code WebFilter}
	 * through the given {@link GatewayFilterChain}.
	 * @param exchange the current server exchange
	 * exchange: 请求上下文，里面包含了Request、Response等信息
	 * @param chain provides a way to delegate to the next filter
	 * chain: 用来把请求委托给下一个过滤器
	 * @return {@code Mono<Void>} to indicate when request processing is complete
	 * MonoZ: 返回时表示当前过滤器逻辑流程结束
	 */
	Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);

}
