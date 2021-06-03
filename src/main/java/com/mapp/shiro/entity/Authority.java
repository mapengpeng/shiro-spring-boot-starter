package com.mapp.shiro.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限绑定
 *
 * roles[ADMIN,OTHER,...]
 *
 * url ： 请求资源地址
 * roles :  访问该资源需要的角色，读多个角色，逗号（,）分开
 *
 * /user/index => ADMI,TEST,OTHER,...
 * @author mapp
 */
@Data
public class Authority implements Serializable {

    private String url;
    private String roles;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoles() {
        return "roles[" + roles + "]";
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
