package com.mapp.shiro.provider;

import com.mapp.shiro.entity.Authority;

import java.util.Set;

/**
 * 角色资源接口
 *
 * @author mapp
 */
public interface AuthorityService {

    /**
     * 查询角色资源
     * @return
     */
    Set<Authority> getAuthorityList();
}
