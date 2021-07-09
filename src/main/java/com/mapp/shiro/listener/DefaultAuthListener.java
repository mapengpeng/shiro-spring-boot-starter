package com.mapp.shiro.listener;

import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.entity.UserDetail;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;

/**
 * 默认认证监听器
 *
 * @author mapp
 */
@Component
public class DefaultAuthListener implements AuthListener {

    private CacheManager cacheManager;

    @Override
    public void onSuccess(UserDetail userDetail) {
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(ShiroConstants.USER_INFO_SESSION_ATTRIBUTE_NAME, userDetail);

        Cache<Object, Object> cache = cacheManager.getCache(ShiroConstants.SESSION_ID_CACHE_NAME);
        cache.put(userDetail.getUsername(), session.getId());
    }

    @Override
    public void onFailure(Object username, AuthenticationException ae) {
        Cache<Object, Object> cache = cacheManager.getCache(ShiroConstants.SESSION_ID_CACHE_NAME);
        cache.remove(username);
    }

    @Override
    public void onLogout(UserDetail userDetail) {
        Cache<Object, Object> cache = cacheManager.getCache(ShiroConstants.SESSION_ID_CACHE_NAME);
        cache.remove(userDetail.getUsername());
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
