package com.mapp.shiro.config;

import com.mapp.shiro.listener.AuthListener;
import com.mapp.shiro.listener.AuthListenerManager;
import com.mapp.shiro.listener.DefaultShiroAuthenticationListener;
import com.mapp.shiro.util.RedisUtil;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;


/**
 * shiro自动装配
 */
@Configuration
@DependsOn("redisTemplate")
@EnableConfigurationProperties(ShiroProperties.class)
@ComponentScan("com.mapp.shiro")
public class ShiroAutoConfigure {

    private ShiroProperties shiroProperties;
    private ShiroConfig shiroConfig;
    @Autowired
    private List<AuthListener> authListenerList;
    @Autowired
    private List<SessionListener> sessionListenerList;

    public ShiroAutoConfigure(ShiroProperties shiroProperties, CustomConfig customConfig, RedisTemplate redisTemplate) {
        RedisUtil.setRedisTemplate(redisTemplate);
        this.shiroProperties = shiroProperties;
        this.shiroConfig = new ShiroConfig(shiroProperties, customConfig);
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(shiroConfig.getCacheManager());
        securityManager.setSessionManager(sessionManager());
        securityManager.setSubjectFactory(shiroConfig.getSubjectFactory());
        // Realm验证
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        securityManager.setAuthenticator(authenticator);

        // 设置登录监听器
        AuthListenerManager authListenerManager = new AuthListenerManager();
        authListenerManager.setAuthListeners(authListenerList);
        DefaultShiroAuthenticationListener shiroAuthenticationListener = new DefaultShiroAuthenticationListener(authListenerManager);
        shiroAuthenticationListener.setCacheManager(shiroConfig.getCacheManager());
        authenticator.getAuthenticationListeners().add(shiroAuthenticationListener);
        securityManager.setRealms(shiroConfig.getRealms());

        return securityManager;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setGlobalSessionTimeout(shiroProperties.getSessionTimeOut() * ShiroConstants.MILLIS_PER_MINUTE);
        sessionManager.setSessionValidationInterval(shiroProperties.getSessionValidInterval() * ShiroConstants.MILLIS_PER_MINUTE);
        sessionManager.setSessionDAO(sessionDAO());
        // 默认session监听器
        sessionManager.getSessionListeners().addAll(sessionListenerList);
        return sessionManager;
    }

    @Bean
    public SessionDAO sessionDAO() {
        return new EnterpriseCacheSessionDAO();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setFilters(shiroConfig.getFilterMap());

        // 默认登录页面
        shiroFilterFactoryBean.setLoginUrl(shiroProperties.getLoginUrl());
        // 登录成功页面
        shiroFilterFactoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());
        // 未授权页面
        shiroFilterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroConfig.getFilterChainDefinitionMap());
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        return autoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
            DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
