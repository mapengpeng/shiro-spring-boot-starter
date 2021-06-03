package com.mapp.shiro.filter;

import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.config.ShiroProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 强制退出拦截器
 *
 * @author mapp
 */
public class ForceLogoutFilter extends OncePerRequestFilter {


    private ShiroProperties shiroProperties;

    public ForceLogoutFilter(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();
        // 认证后再处理，未认证的会在ExtendFormAuthenticationFilter中处理
        if (subject.isAuthenticated()) {
            Session session = subject.getSession();
            if (session.getAttribute(ShiroConstants.FORCE_LOGOUT_SESSION_ATTRIBUTE_NAME) != null) {
                subject.logout();
                request.setAttribute(ShiroConstants.DEFAULT_ERROR_MSG_ATTRIBUTE_NAME, ShiroConstants.FORCE_LOGOUT_ERROR_MSG);
                WebUtils.issueRedirect(request, response, shiroProperties.getForceLogoutUrl());
            }
        }

        chain.doFilter(request, response);
    }
}
