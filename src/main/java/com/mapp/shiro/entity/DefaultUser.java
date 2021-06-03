package com.mapp.shiro.entity;

import lombok.Data;

@Data
public class DefaultUser implements UserDetail {

    private String username;

    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
