package top.lightlink.common;

public class Constants {
    public static final String REDIS_KEY = "PERSONALMEETING:";
    public static final String REDIS_KEY_LOGIN_USER = REDIS_KEY + "LOGIN_USER:";
    public static final String REDIS_KEY_UID_TOKEN = REDIS_KEY + "UID_TOKEN:";
    public static final long LOGIN_USER_EXPIRE = 24 * 60 * 60 * 1000;
    public static final String TOKEN_USER = "tokenuser";
    public static final String WS_TEST = "ws_test";
}