package com.mapp.shiro.filter;

import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.util.CommonUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 重写了权限拦截失败后的处理，增加了ajax处理
 *
 * @author mapp
 */
public abstract class ExtendAuthorizationFilter extends AuthorizationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            if (CommonUtil.isAjaxRequest(WebUtils.toHttp(request))) {
                CommonUtil.ajaxFail(WebUtils.toHttp(response), HttpServletResponse.SC_FORBIDDEN, ShiroConstants.AUTH_FAILD_MSG);
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
        } else {
            if (CommonUtil.isAjaxRequest(WebUtils.toHttp(request))) {
                CommonUtil.ajaxFail(WebUtils.toHttp(response), HttpServletResponse.SC_FORBIDDEN, ShiroConstants.UNAUTHORIZED_FAILD_MSG);
            } else {
                String unauthorizedUrl = getUnauthorizedUrl();
                if (StringUtils.hasText(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
        return false;
    }
}
