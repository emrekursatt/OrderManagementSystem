package com.tr.demo.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.demo.annotations.CustomerPrincipal;
import com.tr.demo.configurations.properties.JwtProperties;
import com.tr.demo.model.CustomerPrincipalModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.crypto.SecretKey;

@Component
public class CustomerPrincipalArgumentResolver  implements HandlerMethodArgumentResolver {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SecretKey secretKey;

    public CustomerPrincipalArgumentResolver(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getJwtSecret().getBytes());
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CustomerPrincipal.class) &&
                parameter.getParameterType().equals(CustomerPrincipalModel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws JsonProcessingException {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }

        token = token.substring(7); // "Bearer " kısmını çıkarma

        // Token'dan kullanıcı ID'sini çıkarma
        String userKey = "customer:" + extractUserIdFromToken(token);
        String userPrincipalJson = redisTemplate.opsForValue().get(userKey);

        if (userPrincipalJson == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // JSON'dan CustomerPrincipalModel nesnesine dönüştürme
        return objectMapper.readValue(userPrincipalJson, CustomerPrincipalModel.class);
    }


    private Long extractUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject()); // Customer id
    }
}
