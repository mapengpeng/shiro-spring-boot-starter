package com.mapp.shiro.provider;

import com.mapp.shiro.entity.Authority;
import com.mapp.shiro.util.StaticData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *  @author mapp
 */
public class DefaultAuthorityService implements AuthorityService {

    @Override
    public Set<Authority> getAuthorityList() {

        return StaticData.AUTHORITIES;
    }
}
