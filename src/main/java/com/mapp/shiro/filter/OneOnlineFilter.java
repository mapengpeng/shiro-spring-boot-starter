package com.mapp.shiro.filter;

import cn.hutool.core.util.StrUtil;
import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.config.ShiroProperties;
import com.mapp.shiro.util.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 账号只允许一处登录拦截器
 *
 * @author mapp
 */
public class OneOnlineFilter extends OncePerRequestFilter {

    private CacheManager cacheManager;
    private ShiroProperties shiroProperties;

    public OneOnlineFilter(CacheManager cacheManager, ShiroProperties properties) {
        this.cacheManager = cacheManager;
        this.shiroProperties = properties;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();
        // 认证后再处理，未认证的会在ExtendFormAuthenticationFilter中处理
        if (subject.isAuthenticated()) {
            Session session = subject.getSession();
            Serializable id = session.getId();
            Cache<Object, Object> cache = cacheManager.getCache(ShiroConstants.SESSION_ID_CACHE_NAME);
            String userName = ShiroUtil.getUser().getUsername();
            Object o = cache.get(userName);
            if (o != null && !StrUtil.equals(o.toString(), id.toString())) {
                SecurityUtils.getSubject().logout();
                WebUtils.issueRedirect(request, response, shiroProperties.getKickoutUrl());
            }
        }

        chain.doFilter(request, response);
    }
}
