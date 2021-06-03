package com.mapp.shiro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * shiro配置
 *
 * @author mapp
 */
@Data
@ConfigurationProperties(prefix = "mapp.shiro")
public class ShiroProperties {

    // 是否启用shiro
    private boolean shiroEnable = true;
    // 是否使用验证码
    private boolean captchaEnable = true;

    // 是否启用强制退出
    private boolean forceLogoutEnable = false;
    // 强制退出地址
    private String forceLogoutUrl = "/forceLogout";

    // 是否开启账号只在一处登录限制
    private boolean oneOnlineEnable = false;
    // 账号在别处登录后，退出地址
    private String kickoutUrl = "/kickout";

    // 是否开启cas
    private boolean casEnable = false;
    // cas服务器认证地址
    private String casServer;
    // 认证成功后跳转地址，即子系统访问地址
    private String service;

    // 密码加密方式
    private String algorithmName = "SHA-256";
    // 加密盐
    private String salt = "1a2b3c4k5j8f9d_?!";
    // 迭代次数
    private int iterations = 2;

    // session过期时间 单位 分钟
    private long sessionTimeOut = 120;
    // session调度时间 单位 分钟
    private long sessionValidInterval = 10;

    // 登录页地址
    private String loginUrl = "/login";
    // 未授权页面
    private String unauthorizedUrl = "/403";
    // 登录成功页面
    private String successUrl = "/index";

    // 拦截器过滤规则
    private Map<String, String> chainMap;

    private CacheType cacheType;

    public String getLoginUrl() {
        if (isCasEnable()) {
            return getCasServer() + "login?service=" + (service.endsWith("/") ? service : service + "/")  + ShiroConstants.CAS_FILTER_URL;
        }
        return loginUrl;
    }

    public String getService() {
        return service.endsWith("/") ? service : service + "/";
    }

    public String getCasServer() {
        return casServer.endsWith("/") ? casServer : casServer + "/";
    }
}
