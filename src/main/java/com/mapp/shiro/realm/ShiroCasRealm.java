package com.mapp.shiro.realm;

import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.config.ShiroProperties;
import com.mapp.shiro.entity.UserDetail;
import com.mapp.shiro.provider.UserDetailService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * cas集成
 *
 * @author mapp
 */
@SuppressWarnings("deprecation ")
public class ShiroCasRealm extends CasRealm {

    private static Logger LOG = LoggerFactory.getLogger(ShiroCasRealm.class);

    private UserDetailService userDetailService;

    public ShiroCasRealm(UserDetailService userDetailService, ShiroProperties shiroProperties) {
        this.userDetailService = userDetailService;
        setCasServerUrlPrefix(shiroProperties.getCasServer());
        setCasService(shiroProperties.getService() + ShiroConstants.CAS_FILTER_URL);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        AuthenticationInfo info = super.doGetAuthenticationInfo(token);
        String username = (String) info.getPrincipals().getPrimaryPrincipal();
        UserDetail userDetail = userDetailService.loadUserDetail(username);
        if (userDetail == null) {
            LOG.error("登录失败");
            throw new AuthenticationException("用户名或密码错误！");
        }

        return new SimpleAuthenticationInfo(userDetail, token.getCredentials(), getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        UserDetail userDetail = (UserDetail) principals.getPrimaryPrincipal();
        info.setRoles(userDetailService.getRoles(userDetail.getUsername()));
        info.setStringPermissions(userDetailService.getPermissions(userDetail.getUsername()));
        return info;
    }
}
