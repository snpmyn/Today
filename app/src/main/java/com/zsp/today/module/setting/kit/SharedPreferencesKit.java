package com.zsp.today.module.setting.kit;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.zsp.today.application.App;

/**
 * @decs: SharedPreferences 配套原件
 * @author: 郑少鹏
 * @date: 2025/9/29 16:15
 * @version: v 1.0
 */
public class SharedPreferencesKit {
    private static SharedPreferences sharedPreferences;

    public static SharedPreferencesKit getInstance() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppInstance());
        return SharedPreferencesKit.InstanceHolder.INSTANCE;
    }

    /**
     * 动态配色
     *
     * @return 动态配色否
     */
    public boolean dynamicColor() {
        return sharedPreferences.getBoolean("DynamicColor", true);
    }

    /**
     * 首页短语
     *
     * @return 首页短语类型
     */
    public String homePageQuote() {
        return sharedPreferences.getString("HomePageQuote", "taoism");
    }

    private static final class InstanceHolder {
        static final SharedPreferencesKit INSTANCE = new SharedPreferencesKit();
    }
}