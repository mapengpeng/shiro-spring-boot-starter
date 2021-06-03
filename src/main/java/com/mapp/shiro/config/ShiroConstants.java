package com.mapp.shiro.config;

/**
 * 配置常量类
 */
public class ShiroConstants {

    public static final String CAPTCHA_SESSION_ATTRIBUTE_NAME = "captcha_attr";
    public static final String CAPTCHA_PARM_NAME = "captcha";
    public static final String CAPTCHA_URL = "/captcha";

    public static final String CAPTCHA_FILTER_NAME = "captcha";
    public static final String FORCE_LOGOUT_FILTER_NAME = "forceLogout";
    public static final String ONE_ONLINE_FILTER_NAME = "oneOnline";
    public static final String CAS_FILTER_NAME = "cas";
    public static final String CAS_FILTER_URL = "toIndex";


    public static final String USER_INFO_SESSION_ATTRIBUTE_NAME = "user_info_session_attr";
    public static final String FORCE_LOGOUT_SESSION_ATTRIBUTE_NAME = "force_logout_session_attr";

    public static final String DEFAULT_ERROR_MSG_ATTRIBUTE_NAME = "shiroLoginFailure";
    public static final String SESSION_ID_CACHE_NAME = "sessionIdCache";

    public static final String LOGIN_SUCCESS_MSG = "登录成功";
    public static final String LOGIN_FAILD_MSG = "登录失败";
    public static final String AUTH_FAILD_MSG = "认证失败，请重新登录";
    public static final String CAPTCHA_ERROR_MSG = "请输入正确的验证码";
    public static final String FORCE_LOGOUT_ERROR_MSG = "您已被强制下线！";
    public static final String KICKOUT_ERROR_MSG = "您的账号已在其他地方登录，您已掉线！";

    public static final String UNAUTHORIZED_FAILD_MSG = "没有权限进行该操作！";

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    // session存活时长 2小时
    public static final long DEFAULT_GLOBAL_SESSION_TIMEOUT = 2 * MILLIS_PER_HOUR;
    // session 调度器调度间隔 10分钟
    public static final long SESSION_VALID_INTERVAL = 10 * MILLIS_PER_MINUTE;


}
