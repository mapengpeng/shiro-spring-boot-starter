package com.mapp.shiro.listener;

import com.mapp.shiro.entity.UserDetail;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认认证监听器
 *
 * @author mapp
 */
public class DefaultShiroAuthenticationListener implements AuthenticationListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultShiroAuthenticationListener.class);

    private CacheManager cacheManager;
    private AuthListenerManager authListenerManager;

    public DefaultShiroAuthenticationListener(AuthListenerManager authListenerManager) {
        this(null, authListenerManager);
    }

    public DefaultShiroAuthenticationListener(CacheManager cacheManager, AuthListenerManager authListenerManager) {
        this.authListenerManager = authListenerManager;
        if (cacheManager != null) {
            applyCacheManagerToAuthListenerManager();
        }
    }

    private void applyCacheManagerToAuthListenerManager() {
        if (authListenerManager != null) {
            authListenerManager.setCacheManager(getCacheManager());
        }
    }

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        LOG.info("authenticate onSuccess ...");
        UserDetail userInfo = (UserDetail) info.getPrincipals().getPrimaryPrincipal();
        authListenerManager.getAuthListeners().forEach(x -> {
            x.onSuccess(userInfo);
        });
    }

    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException ae) {
        LOG.info("authenticate onFailure ...");
        authListenerManager.getAuthListeners().forEach(x -> {
            x.onFailure(token.getPrincipal(), ae);
        });
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        LOG.info("authenticate onLogout ...");
        UserDetail userInfo = (UserDetail) principals.getPrimaryPrincipal();
        authListenerManager.getAuthListeners().forEach(x -> {
            x.onLogout(userInfo);
        });
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        applyCacheManagerToAuthListenerManager();
    }

    public AuthListenerManager getAuthListenerManager() {
        return authListenerManager;
    }

    public void setAuthListenerManager(AuthListenerManager authListenerManager) {
        this.authListenerManager = authListenerManager;
    }
}
