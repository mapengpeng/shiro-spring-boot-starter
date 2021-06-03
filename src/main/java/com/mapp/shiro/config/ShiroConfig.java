package com.mapp.shiro.config;

import cn.hutool.core.collection.CollUtil;
import com.mapp.shiro.cache.RedisCacheManager;
import com.mapp.shiro.entity.Authority;
import com.mapp.shiro.filter.*;
import com.mapp.shiro.listener.AuthListener;
import com.mapp.shiro.listener.DefaultAuthListener;
import com.mapp.shiro.listener.DefaultSessionListener;
import com.mapp.shiro.provider.AuthorityService;
import com.mapp.shiro.provider.DefaultAuthorityService;
import com.mapp.shiro.provider.DefaultUserService;
import com.mapp.shiro.provider.UserDetailService;
import com.mapp.shiro.realm.ShiroCasRealm;
import com.mapp.shiro.realm.UserPasswordReam;
import com.mapp.shiro.util.RedisUtil;
import com.mapp.shiro.util.ShiroUtil;
import com.mapp.shiro.util.SpringContextHelper;
import lombok.Getter;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

import javax.servlet.Filter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * shiro配置
 */
@Getter
public class ShiroConfig {

    private ShiroProperties shiroProperties;
    private CustomConfig customConfig;
    private SubjectFactory subjectFactory;
    private UserDetailService userDetailService;
    private AuthorityService authorityService;
    private CacheManager cacheManager;
    private List<SessionListener> sessionListeners = new ArrayList<>();
    private List<AuthListener> authListeners = new ArrayList<>();
    private Map<String, Filter> filterMap = new ConcurrentHashMap<>();
    private Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
    private List<Realm> realms = new ArrayList<>();

    private String[] staticRescource = {"/static/**", "/js/**", "/css/**", "/images/**", "/favicon.ico", "/api/**"};

    public ShiroConfig(ShiroProperties shiroProperties, CustomConfig customConfig) {
        this.shiroProperties = shiroProperties;
        this.customConfig = customConfig;

        this.subjectFactory = new DefaultWebSubjectFactory();
        afterPropertiesSet();
    }

    private void afterPropertiesSet() {

        loadlCustomService();

        loadCacheManager();

        loadFilters();

        loadFilterChainDefinitionMap();

        loadListeners();

        loadRealms();


        if (shiroProperties.isCasEnable()) {
            this.subjectFactory = new CasSubjectFactory();
        }
    }


    private void loadFilters() {

        ExtendFormAuthenticationFilter formAuthenticationFilter = new ExtendFormAuthenticationFilter(shiroProperties);
        filterMap.put("authc", formAuthenticationFilter);
        ExtendPermissionsAuthorizationFilter permissionsAuthorizationFilter = new ExtendPermissionsAuthorizationFilter();
        filterMap.put("perms", permissionsAuthorizationFilter);
        ExtendRolesAuthorizationFilter rolesAuthorizationFilter = new ExtendRolesAuthorizationFilter();
        filterMap.put("roles", rolesAuthorizationFilter);

        if (shiroProperties.isCaptchaEnable()) {
            filterMap.put(ShiroConstants.CAPTCHA_FILTER_NAME, new CaptchaFilter());
        }
        if (shiroProperties.isForceLogoutEnable()) {
            filterMap.put(ShiroConstants.FORCE_LOGOUT_FILTER_NAME, new ForceLogoutFilter(shiroProperties));
        }
        if (shiroProperties.isOneOnlineEnable()) {
            filterMap.put(ShiroConstants.ONE_ONLINE_FILTER_NAME, new OneOnlineFilter(cacheManager, shiroProperties));
        }
        if (shiroProperties.isCasEnable()) {
            filterMap.put(ShiroConstants.CAS_FILTER_NAME, new CasFilter());
            LogoutFilter logoutFilter = new LogoutFilter();
            logoutFilter.setRedirectUrl(shiroProperties.getCasServer() + "logout?service=" + shiroProperties.getService());
            filterMap.put("logout", logoutFilter);
        }
        if (getCustomConfig() != null) {
            if (CollUtil.isNotEmpty(getCustomConfig().getFilters())) {
                getCustomConfig().getFilters().forEach((k, v) -> {
                    filterMap.putIfAbsent(k, v);
                });
            }
        }
    }

    private void loadCacheManager() {
        switch (shiroProperties.getCacheType()) {
            case MAP:
                this.cacheManager = new MemoryConstrainedCacheManager();
                break;
            case REDIS:
                this.cacheManager = new RedisCacheManager(RedisUtil.getRedisTemplate());
                break;
            case EHCACHE:
                this.cacheManager = new EhCacheManager();
                break;
            default:
                this.cacheManager = new MemoryConstrainedCacheManager();
        }

    }

    private void loadFilterChainDefinitionMap() {
        Arrays.stream(staticRescource).forEach(x -> {
            filterChainDefinitionMap.putIfAbsent(x, "anon");
        });
        if (shiroProperties.isForceLogoutEnable()) {
            filterChainDefinitionMap.put(shiroProperties.getForceLogoutUrl(), "anon");
        }
        if (shiroProperties.isOneOnlineEnable()) {
            filterChainDefinitionMap.put(shiroProperties.getKickoutUrl(), "anon");
        }

        if (CollUtil.isNotEmpty(shiroProperties.getChainMap())) {
            shiroProperties.getChainMap().forEach((k, v) -> {
                filterChainDefinitionMap.putIfAbsent(k, v);
            });
        }
        if (shiroProperties.isCaptchaEnable()) {
            filterChainDefinitionMap.putIfAbsent(ShiroConstants.CAPTCHA_URL, ShiroConstants.CAPTCHA_FILTER_NAME);
        }

        if (shiroProperties.isCasEnable()) {
            filterChainDefinitionMap.put("/toIndex", ShiroConstants.CAS_FILTER_NAME);
        }

        filterChainDefinitionMap.put("/logout", "logout");

        // 角色权限模式
        Set<Authority> authorityList = authorityService.getAuthorityList();
        if (!CollectionUtils.isEmpty(authorityList)) {
            for (Authority authority : authorityList) {
                filterChainDefinitionMap.putIfAbsent(authority.getUrl(), authority.getRoles());
            }

        }

        if (getCustomConfig() != null) {
            if (CollUtil.isNotEmpty(getCustomConfig().getFilterChainDefinition())) {
                getCustomConfig().getFilterChainDefinition().forEach((k, v) -> {
                    filterChainDefinitionMap.putIfAbsent(k, v);
                });
            }
        }

        if (!shiroProperties.isShiroEnable()) {
            filterChainDefinitionMap.put("/**", "anon");
        } else {
            filterChainDefinitionMap.put("/**", "authc");
        }

        if (shiroProperties.isForceLogoutEnable()) {
            filterChainDefinitionMap.put("/**", filterChainDefinitionMap.get("/**") + "," + ShiroConstants.FORCE_LOGOUT_FILTER_NAME);
        }

        if (shiroProperties.isOneOnlineEnable()) {
            filterChainDefinitionMap.put("/**", filterChainDefinitionMap.get("/**") + "," + ShiroConstants.ONE_ONLINE_FILTER_NAME);
        }
    }

    private void loadlCustomService() {
        if (getCustomConfig() != null && getCustomConfig().getUserDetailService()!= null) {
            this.userDetailService = getCustomConfig().getUserDetailService();
        } else {
            this.userDetailService = new DefaultUserService();
        }

        if (getCustomConfig() != null && getCustomConfig().getAuthorityService() != null) {
            this.authorityService = getCustomConfig().getAuthorityService();
        } else {
            this.authorityService = new DefaultAuthorityService();
        }
    }

    private void loadListeners() {
        this.sessionListeners.add(new DefaultSessionListener());
        this.authListeners.add(new DefaultAuthListener());

        if (getCustomConfig() != null && CollUtil.isNotEmpty(getCustomConfig().getSessionListeners())) {
            this.sessionListeners.addAll(getCustomConfig().getSessionListeners());
        }
        if (getCustomConfig() != null && CollUtil.isNotEmpty(getCustomConfig().getAuthListeners())) {
            this.authListeners.addAll(getCustomConfig().getAuthListeners());
        }
    }

    private void loadRealms() {
        UserPasswordReam userPasswordReam = new UserPasswordReam(getUserDetailService());
        realms.add(userPasswordReam);
        if (shiroProperties.isCasEnable()) {
            realms.add(new ShiroCasRealm(getUserDetailService(), shiroProperties));
        }
    }
}
