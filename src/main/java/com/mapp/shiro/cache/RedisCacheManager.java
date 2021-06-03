package com.mapp.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis 缓存管理器
 *
 * @author mapp
 */
@SuppressWarnings("all")
public class RedisCacheManager extends AbstractCacheManager {

    private RedisTemplate redisTemplate;

    public RedisCacheManager(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected Cache createCache(String name) throws CacheException {
        return new RedisCache(name, redisTemplate);
    }
}
