package com.mapp.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * redis cache
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("all")
public class RedisCache<K, V> implements Cache<K, V> {

    private String name;
    private RedisTemplate redisTemplate;

    public RedisCache(String name, RedisTemplate redisTemplate) {
        if (name == null) {
            throw new IllegalArgumentException("Cache name cannot be null.");
        }
        this.name = name;
       this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K k) throws CacheException {
        return (V) redisTemplate.opsForHash().get(name, k);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForHash().put(name, k, v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V v = get(k);
        redisTemplate.opsForHash().delete(name, k);
        return v;
    }

    @Override
    public void clear() throws CacheException {
        Set keys = redisTemplate.opsForHash().keys(name);
        redisTemplate.opsForHash().delete(name, keys);
    }

    @Override
    public int size() {
        return redisTemplate.opsForHash().entries(name).size();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.opsForHash().keys(name);
    }

    @Override
    public Collection<V> values() {
        return redisTemplate.opsForHash().entries(name).values();
    }
}
