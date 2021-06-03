package com.mapp.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 重写权限过滤器
 *
 * @author mapp
 */
public class ExtendPermissionsAuthorizationFilter extends ExtendAuthorizationFilter {

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        Subject subject = getSubject(request, response);
        String[] permsArray = (String[]) mappedValue;

        if (permsArray == null || permsArray.length == 0) {
            return true;
        }

        Set<String> perms = CollectionUtils.asSet(permsArray);
        for (String perm : perms) {
            if (subject.isPermitted(perm)) {
                return true;
            }
        }
        return false;
    }
}
