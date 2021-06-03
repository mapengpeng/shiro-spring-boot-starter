package com.mapp.shiro.listener;


import com.mapp.shiro.entity.UserDetail;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.cache.CacheManagerAware;

/**
 * 认证监听器
 *
 * @author mapp
 */
public interface AuthListener extends CacheManagerAware {

    /**
     * 登录认证成功
     * @param userDetail
     */
    void onSuccess(UserDetail userDetail);

    /**
     * 登录认证失败
     * @param username
     * @param ae 异常信息
     */
    void onFailure(Object username, AuthenticationException ae);

    /**
     * 退出登录
     * @param userDetail
     */
    void onLogout(UserDetail userDetail);
}
