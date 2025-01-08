package com.tr.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.demo.configuration.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tr.demo.advice.constants.UserServiceConstants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@Component
public class JwtTokenProvider {

    private static final String SCOPES = "scopes";
    private static final String STATUS = "status";

    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final SecretKey secretKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // SecretKey değerini configuration'dan alarak initialize ediyoruz
    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties, RedisTemplate<String, String> redisTemplate) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getJwtSecret().getBytes());
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(final Authentication authentication) {
        final CustomerPrincipal customerPrincipal = (CustomerPrincipal) authentication.getPrincipal();
        String token = getAccessToken(customerPrincipal.getId(), populateClaims(customerPrincipal));

        saveCustomerPrincipalInRedis(customerPrincipal);

        return token;
    }

    private void saveCustomerPrincipalInRedis(CustomerPrincipal customerPrincipal) {
        try {
            String customerKey = "user:" + customerPrincipal.getId();

            // JSON formatına dönüştür ve Redis'e kaydet
            String customerPrincipalJson = objectMapper.writeValueAsString(Map.of(
                    "userId", customerPrincipal.getId(),
                    "status", customerPrincipal.getCustomerStatus(),
                    "username", customerPrincipal.getUsername(),
                    "email", customerPrincipal.getEmail(),
                    "enabled", customerPrincipal.isEnabled()
            ));

            // Redis’e kaydetme işlemi
            redisTemplate.opsForValue().set(customerKey, customerPrincipalJson, jwtProperties.getJwtExpirationInMs(), TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize  CustomerPrincipal", e);
        }
    }

    public String getAccessToken(Long id, Claims claims) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtProperties.getJwtExpirationInMs());
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();

        redisTemplate.opsForValue().set(id.toString(), token, expiryDate.getTime(), TimeUnit.MILLISECONDS);
        return token;
    }

    public String generateRefreshToken(final CustomerPrincipal customerPrincipal) {
        final String refreshToken = RandomStringUtils.randomAlphanumeric(40);
        final Date refreshTokenExpiryDate = new Date(new Date().getTime() + jwtProperties.getJwtRefreshExpirationInMs());
        final String tokenKey = getRefreshTokenKey(customerPrincipal.getId());
        redisTemplate.opsForValue().set(tokenKey, refreshToken, refreshTokenExpiryDate.getTime(), TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    public Claims populateClaims(CustomerPrincipal customerPrincipal) {
        Claims claims = Jwts.claims().setSubject(Long.toString(customerPrincipal.getId()));
        claims.put(SCOPES, customerPrincipal.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        claims.put(STATUS, customerPrincipal.getCustomerStatus());
        return claims;
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return tokenExist(token);
        } catch (SignatureException ex) {
            log.debug("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.debug("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.debug("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.debug("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.debug("JWT claims string is empty.");
        }
        return false;
    }

    public boolean validateRefreshToken(final String token, Long id) {
        final String existingToken = redisTemplate.opsForValue().get(getRefreshTokenKey(id));
        return StringUtils.isNotEmpty(existingToken) && Objects.equals(existingToken, token);
    }

    public boolean invalidateTokens(final String token) {
        final Long id = extractMemberNoFromToken(token);
        final Boolean deleted = redisTemplate.delete(id.toString());
        final Boolean deletedRefreshToken = redisTemplate.delete(getRefreshTokenKey(id));
        return (Objects.nonNull(deleted) && deleted) && (Objects.nonNull(deletedRefreshToken) && deletedRefreshToken);
    }

    Long extractMemberNoFromToken(final String token) {
        return Long.parseLong(getClaimsByToken(token).getSubject());
    }

    String extractChannelFromToken(final String token) {
        return (String) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get(X_CHANNEL_TYPE);
    }

    String extractTokenFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(AUTHORIZATION);
        return extractTokenFromAuthorizationHeader(bearerToken);
    }

    public String extractTokenFromAuthorizationHeader(final String authorizationHeaderValue) {
        if (StringUtils.isEmpty(authorizationHeaderValue)) {
            return EMPTY;
        }
        if (!authorizationHeaderValue.startsWith(BEARER + SPACE)) {
            return EMPTY;
        }
        return authorizationHeaderValue.split(SPACE)[1];
    }

    private Claims getClaimsByToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private String getRefreshTokenKey(Long id) {
        return REFRESH_TOKEN_PREFIX + id.toString();
    }

    private boolean tokenExist(final String token) {
        final String existingToken = redisTemplate.opsForValue().get(extractMemberNoFromToken(token).toString());
        return StringUtils.isNotEmpty(existingToken) && Objects.equals(existingToken, token);
    }
}
