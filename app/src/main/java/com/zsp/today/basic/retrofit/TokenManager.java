package com.zsp.today.basic.retrofit;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc token 管理器
 */
public class TokenManager {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenManager.token = token;
    }

    public static boolean hasToken() {
        return (null != token) && !token.isEmpty();
    }
}