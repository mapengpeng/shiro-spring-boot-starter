package com.mapp.shiro.util;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisUtil {

    public static RedisTemplate redisTemplate = null;

    public static RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }
}
