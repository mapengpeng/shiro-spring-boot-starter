package com.mapp.shiro.provider;

import com.mapp.shiro.entity.UserDetail;

import java.util.Set;

/**
 * 用户接口实现
 */
public interface UserDetailService {

    /**
     * 获取用户信息
     * @param username
     * @return
     */
    UserDetail loadUserDetail(String username);

    /**
     * 获取用户角色
     * @param username
     * @return
     */
    Set<String> getRoles(String username);

    /**
     * 获取用户权限信息
     * @param username
     * @return
     */
    Set<String> getPermissions(String username);

}
