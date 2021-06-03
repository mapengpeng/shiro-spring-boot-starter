package com.mapp.shiro.filter;

import cn.hutool.core.util.StrUtil;
import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.config.ShiroProperties;
import com.mapp.shiro.util.CommonUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 表单登录拦截器
 *
 * 增加验证码拦截开关
 * 增加ajax验证处理
 * 修改异常信息提示
 *
 * @author mapp
 */
public class ExtendFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ExtendFormAuthenticationFilter.class);

    private ShiroProperties shiroProperties;

    public ExtendFormAuthenticationFilter(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            } else {
                return true;
            }
        } else {
            // Ajax
            if (CommonUtil.isAjaxRequest(WebUtils.toHttp(request))) {
                CommonUtil.ajaxFail(WebUtils.toHttp(response), HttpServletResponse.SC_UNAUTHORIZED, ShiroConstants.AUTH_FAILD_MSG);
            }else {
                saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {

        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                    "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {
            if (shiroProperties.isCaptchaEnable()) {
                String captcha = WebUtils.getCleanParam(request, ShiroConstants.CAPTCHA_PARM_NAME);
                String s = String.valueOf(WebUtils.toHttp(request).getSession().getAttribute(ShiroConstants.CAPTCHA_SESSION_ATTRIBUTE_NAME));
                if (!StrUtil.equalsIgnoreCase(captcha, s)) {
                    throw new AuthenticationException(ShiroConstants.CAPTCHA_ERROR_MSG);
                }
            }
            Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        // Ajax
        if (CommonUtil.isAjaxRequest(WebUtils.toHttp(request))) {
            CommonUtil.ajaxSuccess(WebUtils.toHttp(response), HttpServletResponse.SC_OK, ShiroConstants.LOGIN_SUCCESS_MSG);
        } else {
            issueSuccessRedirect(request, response);
        }
        //we handled the success redirect directly, prevent the chain from continuing:
        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        // Ajax
        if (CommonUtil.isAjaxRequest(WebUtils.toHttp(request))) {
            CommonUtil.ajaxFail(WebUtils.toHttp(response), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ShiroConstants.LOGIN_FAILD_MSG);
        } else {
            setFailureAttribute(request, e);
        }
        //login failed, let request continue back to the login page:
        return true;
    }

    @Override
    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
        // 直接写入异常提示
        request.setAttribute(ShiroConstants.DEFAULT_ERROR_MSG_ATTRIBUTE_NAME, ae.getMessage());
    }
}
