package com.ms.api.gateway.pit;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class AuthorizationJWTHeaderTokenRefresh extends AbstractGatewayFilterFactory<AuthorizationJWTHeaderTokenRefresh.Config> {

	@Autowired
	Environment env;

	public AuthorizationJWTHeaderTokenRefresh() {
		super(Config.class);
	}

	public static class Config {
		// Put configuration properties here
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");
			
			

			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
		return response.writeWith(Flux.just(buffer));
		
		//return response.setComplete();
	}

	
	
	
	
}
