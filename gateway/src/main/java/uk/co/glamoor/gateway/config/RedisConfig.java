package uk.co.glamoor.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }
//
//    @Bean
//    public ReactiveRedisTemplate<String, String> redisTemplate(ReactiveRedisConnectionFactory factory) {
//        return new ReactiveRedisTemplate<>(factory, RedisConfig.redisSerializationContext());
//    }

//    @Bean
//    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
//                .<String, String>newSerializationContext(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .hashKey(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .build();
//
//        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
//    }

//
//    private static RedisSerializationContext<String, String> redisSerializationContext() {
//        return RedisSerializationContext.<String, String>newSerializationContext(new StringRedisSerializer())
//                .key(new StringRedisSerializer())
//                .value(new StringRedisSerializer())
//                .build();
//    }
}

