package com.mapp.shiro.filter;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.mapp.shiro.config.ShiroConstants;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 验证码拦截器
 *
 * @author mapp
 */
public class CaptchaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 1);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ((HttpServletRequest)request).getSession().setAttribute(ShiroConstants.CAPTCHA_SESSION_ATTRIBUTE_NAME, captcha.getCode());
            captcha.write(outputStream);
        }
    }
}
