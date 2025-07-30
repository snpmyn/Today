package com.zsp.today.application;

import com.zsp.today.BuildConfig;
import com.zsp.today.kit.AppKit;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.today.value.Folder;

import fragmentation.configure.FragmentationInitConfig;
import litepal.configure.LitePalInitConfigure;
import litepal.kit.LitePalKit;
import pool.application.BasePoolApp;
import pool.login.LoginActivity;
import timber.log.Timber;
import widget.crash.CrashManagerInitConfigure;
import widget.status.manager.StatusManager;
import widget.tbs.configure.TbsInitConfigure;

/**
 * Created on 2025/7/10.
 *
 * @author 郑少鹏
 * @desc 应用
 */
public class App extends BasePoolApp {
    private static App appInstance;

    /**
     * 获单例
     *
     * @return 单例
     */
    public static App getAppInstance() {
        return appInstance;
    }

    /**
     * 获取用户数据库表
     *
     * @return 用户数据库表
     */
    public UserDataBaseTable getUserDataBaseTable() {
        return LitePalKit.getInstance().findFirst(UserDataBaseTable.class);
    }

    /**
     * 获取手机号
     *
     * @param haveAppointValue 有指定值否
     * @param appointValue     指定值
     * @return 手机号
     */
    public String getPhoneNumber(boolean haveAppointValue, String appointValue) {
        UserDataBaseTable userDataBaseTable = getUserDataBaseTable();
        return ((null != userDataBaseTable) ? userDataBaseTable.getPhoneNumber() : (haveAppointValue ? appointValue : "12345678910"));
    }

    /**
     * 应用程序创调
     * <p>
     * 创和实例化任何应用程序状态变量或共享资源变量，方法内获 Application 单例。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("%s onCreate", getClass().getSimpleName());
        // Application 本已单例
        appInstance = this;
        // 状态管理器布局 ID
        StatusManager.BASE_LOADING_LAYOUT_ID = com.zsp.core.R.layout.status_loading_with_animation;
        StatusManager.BASE_EMPTY_LAYOUT_ID = com.zsp.core.R.layout.status_empty_with_animation;
        // 初始化配置
        initConfiguration();
    }

    /**
     * 调试否
     *
     * @return 调试否
     */
    @Override
    protected Boolean debug() {
        return BuildConfig.DEBUG;
    }

    /**
     * 初始化配置
     */
    protected void initConfiguration() {
        // 崩溃管理器
        CrashManagerInitConfigure.getInstance(this, Folder.CRASH);
        // LitePal
        LitePalInitConfigure.initLitePal(this);
        // Fragmentation
        FragmentationInitConfig.initFragmentation(debug());
        // TBS
        TbsInitConfigure.initTbs();
        // 应用配套元件
        AppKit appKit = new AppKit();
        // 登录页监听
        LoginActivity.setLoginActivityListener(appKit::login);
    }
}
