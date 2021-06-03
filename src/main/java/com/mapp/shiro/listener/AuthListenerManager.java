package com.mapp.shiro.listener;

import com.mapp.shiro.entity.UserDetail;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听器管理器
 *
 * @author mapp
 */
public class AuthListenerManager implements AuthListener {

    private List<AuthListener> authListeners;
    private CacheManager cacheManager;

    public AuthListenerManager() {
        this.authListeners = new ArrayList<>();
    }

    public void setAuthListeners(List<AuthListener> authListeners) {
        if (authListeners == null) {
            this.authListeners = new ArrayList<>();;
        }else {
            this.authListeners = authListeners;
        }

        applyCacheManagerToAuthListener();
    }

    private void applyCacheManagerToAuthListener() {
        authListeners.forEach(x -> {
            x.setCacheManager(getCacheManager());
        });
    }

    public List<AuthListener> getAuthListeners() {
        return authListeners;
    }

    @Override
    public void onSuccess(UserDetail userDetail) {
        authListeners.forEach(x -> {
            x.onSuccess(userDetail);
        });
    }

    @Override
    public void onFailure(Object username, AuthenticationException ae) {
        authListeners.forEach(x -> {
            x.onFailure(username, ae);
        });
    }

    @Override
    public void onLogout(UserDetail userDetail) {
        authListeners.forEach(x -> {
            x.onLogout(userDetail);
        });
    }


    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        applyCacheManagerToAuthListener();
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
