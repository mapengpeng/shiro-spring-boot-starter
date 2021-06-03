package com.mapp.shiro.provider;

import com.mapp.shiro.entity.DefaultUser;
import com.mapp.shiro.entity.UserDetail;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DefaultUserService implements UserDetailService {

    private HashMap<String, DefaultUser> userMap = new HashMap<>();

    public DefaultUserService() {
        DefaultUser one = new DefaultUser();
        one.setUsername("test");
        one.setPassword("cb4e19a385f2fb9dbb38a5e98ff96f0a1e10e5553436f41767927bce1eecb6b5");

        DefaultUser two = new DefaultUser();
        two.setUsername("admin");
        two.setPassword("cb4e19a385f2fb9dbb38a5e98ff96f0a1e10e5553436f41767927bce1eecb6b5");

        DefaultUser three = new DefaultUser();
        three.setUsername("admin3");
        three.setPassword("cb4e19a385f2fb9dbb38a5e98ff96f0a1e10e5553436f41767927bce1eecb6b5");

        userMap.put("test", one);
        userMap.put("admin", two);
        userMap.put("admin3", three);
    }

    @Override
    public UserDetail loadUserDetail(String username) {
        return userMap.get(username);
    }

    @Override
    public Set<String> getRoles(String username) {
        Set<String> roles = new HashSet<>();
        if ("admin".equalsIgnoreCase(username)) {
            roles.add("ADMIN");
        }
        return roles;
    }

    @Override
    public Set<String> getPermissions(String username) {
        Set<String> permissions = new HashSet<>();
        return permissions;
    }

}
