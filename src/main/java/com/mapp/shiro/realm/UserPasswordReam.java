package com.mapp.shiro.realm;


import cn.hutool.core.util.StrUtil;
import com.mapp.shiro.entity.UserDetail;
import com.mapp.shiro.provider.UserDetailService;
import com.mapp.shiro.util.EncryUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 用户名，密码登录
 *
 * @author mapp
 */
public class UserPasswordReam extends AuthorizingRealm {

    private UserDetailService userDetailService;

    public UserPasswordReam(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        UserDetail userDetail = (UserDetail) principals.getPrimaryPrincipal();
        info.setRoles(userDetailService.getRoles(userDetail.getUsername()));
        info.setStringPermissions(userDetailService.getPermissions(userDetail.getUsername()));
        return info;
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String pwd = EncryUtil.encryPwd(token.getPassword());

        UserDetail detail = userDetailService.loadUserDetail(username);
        if (detail == null) {
            throw new AuthenticationException("用户名或密码错误！");
        }
        if (StrUtil.equals(pwd, detail.getPassword())) {

            return new SimpleAuthenticationInfo(detail, token.getPassword(), getName());
        }else {
            throw new AuthenticationException("用户名或密码错误！");
        }
    }
}
