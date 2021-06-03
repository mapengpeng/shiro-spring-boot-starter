package com.mapp.shiro.listener;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认session监听器
 */
public class DefaultSessionListener implements SessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionListener.class);

    @Override
    public void onStart(Session session) {
        LOG.info("session start ...");
    }

    @Override
    public void onStop(Session session) {
        LOG.info("session stop ...");
    }

    @Override
    public void onExpiration(Session session) {
        LOG.info("session expire ...");
    }
}
