package com.tr.demo.configuration.redis;

import com.tr.demo.configuration.properties.RedisConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableCaching
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisConfigProperties redisConfigProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if (redisConfigProperties.getHost() != null){
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(redisConfigProperties.getHost().trim());
            configuration.setPort(redisConfigProperties.getPort());
            return new LettuceConnectionFactory(configuration);
        }else
            return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}