package com.mapp.shiro.config;


import com.mapp.shiro.listener.AuthListener;
import com.mapp.shiro.provider.AuthorityService;
import com.mapp.shiro.provider.UserDetailService;
import org.apache.shiro.session.SessionListener;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

/**
 * 用户自定义配置
 *
 * @author mapp
 */
public interface CustomConfig {

    /**
     * 获取 UserDetailService实现类
     * @return
     */
    UserDetailService getUserDetailService();

    /**
     * 获取 AuthorityService实现类
     * @return
     */
    AuthorityService getAuthorityService();

    /**
     * 获取自定义扩展Filter
     * @return
     */
    default Map<String, Filter> getFilters() {
        return null;
    }

    /**
     * 获取自定义扩展规则
     * @return
     */
    default Map<String, String> getFilterChainDefinition() {
        return null;
    }
}
