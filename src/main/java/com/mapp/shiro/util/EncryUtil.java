package com.mapp.shiro.util;

import com.mapp.shiro.config.ShiroProperties;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 *
 * @author mapp
 */
public class EncryUtil {

    public static String encryPwd(Object source) {
        SimpleHash hash = new SimpleHash(shiroProperties().getAlgorithmName(), source, shiroProperties().getSalt(), shiroProperties().getIterations());
        return hash.toHex();
    }

    public static ShiroProperties shiroProperties() {
        return SpringContextHelper.getBean(ShiroProperties.class);
    }
}
