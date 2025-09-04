package com.pruebatecnica.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    @Value("${jwt.secret-key}")
    private String secretKey;
    
    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header");
                return handleUnauthorized(exchange);
            }
            
            String token = authHeader.substring(7);
            
            try {
                Claims claims = validateToken(token);
                
                // Verificar que sea un access token
                String tokenType = claims.get("type", String.class);
                if (!"access".equals(tokenType)) {
                    log.warn("Invalid token type: {}", tokenType);
                    return handleUnauthorized(exchange);
                }
                
                // Agregar información del usuario a los headers para los microservicios
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(r -> r.header("X-User-Id", claims.get("userId", String.class))
                                     .header("X-User-Email", claims.getSubject())
                                     .header("X-Token-Type", tokenType))
                        .build();
                
                log.debug("JWT validation successful for user: {}", claims.getSubject());
                return chain.filter(modifiedExchange);
                
            } catch (JwtException e) {
                log.error("JWT validation failed: {}", e.getMessage());
                return handleUnauthorized(exchange);
            }
        };
    }
    
    private Claims validateToken(String token) throws JwtException {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        String errorResponse = """
                {
                    "success": false,
                    "httpStatus": 401,
                    "appCode": "AUTH_TOKEN_INVALID",
                    "message": "Token de acceso inválido o expirado",
                    "data": null,
                    "errors": [],
                    "meta": {
                        "service": "api-gateway",
                        "timestamp": "%s"
                    }
                }
                """.formatted(java.time.Instant.now().toString());
        
        byte[] bytes = errorResponse.getBytes();
        exchange.getResponse().getHeaders().setContentLength(bytes.length);
        
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }
    
    public static class Config {
        // Configuration properties if needed
    }
}