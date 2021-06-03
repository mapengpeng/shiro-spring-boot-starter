package com.mapp.shiro.util;

import com.mapp.shiro.entity.Authority;

import java.util.HashSet;
import java.util.Set;

/**
 * 静态模拟数据
 */
public class StaticData {

    public static final Set<Authority> AUTHORITIES = new HashSet<>();

    static {
        Authority one = new Authority();
        one.setUrl("/user/index");
        one.setRoles("ADMIN,TEST");

        Authority two = new Authority();
        two.setUrl("/onLineUser");
        two.setRoles("/ADMIN");

        AUTHORITIES.add(one);
        AUTHORITIES.add(two);
    }
}
