package com.mine.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * HttpWebFilter
 *
 * @author zhangsl
 * @date 2018/12/20 15:58
 */
@Slf4j
@Configuration
@Order(-1)
public class HttpWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String uri = request.getURI().getPath();
        String method = request.getMethod().name();
        log.info("请求 URI: {}, method: {}", uri, method);
        /**
         * 过滤器链的概念都是类似的，调用过滤器链的filter方法将请求转到下一个filter，
         * 如果该filter是最后一  个filter,那就转到
         * 该请求对应的handler,也就是controller方法或函数式endpoint
         */
        return chain.filter(exchange);
    }
}
