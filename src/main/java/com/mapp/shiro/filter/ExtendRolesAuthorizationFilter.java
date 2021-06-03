package com.mapp.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 角色权限 过滤器
 *
 * @author mapp
 */
public class ExtendRolesAuthorizationFilter extends ExtendAuthorizationFilter {

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        Subject subject = getSubject(request, response);
        // mappedValue 即是roles[]中配置的角色集合
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }

        Set<String> roles = CollectionUtils.asSet(rolesArray);

        for (String role : roles) {
            // 有任意一个角色即可
            if (subject.hasRole(role)) {
                return true;
            }
        }

        return false;
    }
}
