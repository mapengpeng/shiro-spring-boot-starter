package com.mapp.shiro.util;

import com.mapp.shiro.config.CustomConfig;
import com.mapp.shiro.config.ShiroConfig;
import com.mapp.shiro.config.ShiroConstants;
import com.mapp.shiro.config.ShiroProperties;
import com.mapp.shiro.entity.OnLineUser;
import com.mapp.shiro.entity.UserDetail;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * shiro工具类
 *
 * @author mapp
 */
public class ShiroUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroUtil.class);

    public static  <T extends UserDetail> T getUser() {
        return (T)SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
    }

    public static List<OnLineUser> getOnLineUser() {
        Collection<Session> activeSessions = getSessionDao().getActiveSessions();
        List<OnLineUser> onLineUsers = new ArrayList<>();
        for (Session session : activeSessions) {
            UserDetail userDetail = (UserDetail) session.getAttribute(ShiroConstants.USER_INFO_SESSION_ATTRIBUTE_NAME);

            if (userDetail != null) {
                OnLineUser onLineUser = new OnLineUser();
                onLineUser.setSessionId(session.getId());
                onLineUser.setUsername(userDetail.getUsername());
                if (getShiroProperties().isForceLogoutEnable()) {
                    if (session.getAttribute(ShiroConstants.FORCE_LOGOUT_SESSION_ATTRIBUTE_NAME) != null)  {
                        onLineUser.setUsername(onLineUser.getUsername() + "[已强制下线，即将被清理！]");
                    }
                }
                onLineUsers.add(onLineUser);
            }

        }
        return onLineUsers;
    }

    public static boolean forceLogout(Serializable sessionId) {
        try {
            Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(sessionId));
            if (session != null) {
                session.setAttribute(ShiroConstants.FORCE_LOGOUT_SESSION_ATTRIBUTE_NAME, Boolean.TRUE);
            }
            return true;
        }catch (Exception e) {
            LOG.error("强制下线失败！ sessionId: {}", sessionId);
          return false;
        }
    }

    /**
     * 重新加载权限，菜单，角色更新时，调用该方法重新加载过滤器规则
     * @throws Exception
     */
    public static void reloadFilterChainDefinitionMap() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = getShiroFilterFactoryBean();
        LOG.info("清空filterChainDefinitionMap{}...", shiroFilterFactoryBean.getFilterChainDefinitionMap());
        shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

        ShiroConfig shiroConfig = new ShiroConfig(getShiroProperties(), getCustomConfig());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroConfig.getFilterChainDefinitionMap());
        LOG.info("重新加载filterChainDefinitionMap{}...", shiroFilterFactoryBean.getFilterChainDefinitionMap());

        AbstractShiroFilter filter = null;
        try {
            filter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("重新加载权限失败！");
            return;
        }
        PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) filter.getFilterChainResolver();
        DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
        LOG.info("清空filterChains{}...", filterChainManager.getFilterChains());
        filterChainManager.getFilterChains().clear();
        Map<String, String> filterChainDefinitionMap = shiroFilterFactoryBean.getFilterChainDefinitionMap();
        if (!CollectionUtils.isEmpty(filterChainDefinitionMap)) {
            filterChainDefinitionMap.forEach((x, y) -> {
                filterChainManager.createChain(x, y);
            });
        }
        LOG.info("重载filterChains{}...", filterChainManager.getFilterChains());

    }

    public static SessionDAO getSessionDao() {
        return SpringContextHelper.getBean(SessionDAO.class);
    }

    public static ShiroProperties getShiroProperties() {
        return SpringContextHelper.getBean(ShiroProperties.class);
    }

    public static ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        return SpringContextHelper.getBean(ShiroFilterFactoryBean.class);
    }

    public static CustomConfig getCustomConfig() {
        return SpringContextHelper.getBean(CustomConfig.class);
    }

}
