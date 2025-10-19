package com.zsp.today.application;

import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.umeng.analytics.MobclickAgent;
import com.zsp.amap.kit.AmapLocationKit;
import com.zsp.amap.listener.AmapLocationKitListener;
import com.zsp.amap.value.AmapConstant;
import com.zsp.today.BuildConfig;
import com.zsp.today.application.kit.AppKit;
import com.zsp.today.basic.value.Folder;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.youmeng.UmKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fragmentation.configure.FragmentationInitConfig;
import litepal.configure.LitePalInitConfig;
import litepal.kit.LitePalKit;
import lottie.configure.LottieInitConfig;
import pool.application.BasePoolApp;
import pool.module.login.LoginActivity;
import pool.module.splash.kit.SplashActivityKit;
import timber.log.Timber;
import util.list.ListUtils;
import util.mmkv.MmkvKit;
import util.number.NumberFormatUtils;
import util.rxbus.RxBus;
import widget.crash.CrashManager;
import widget.permissionx.kit.PermissionKit;
import widget.status.manager.StatusManager;

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
     * @return 手机号
     */
    public String getPhoneNumber() {
        UserDataBaseTable userDataBaseTable = getUserDataBaseTable();
        return (null == userDataBaseTable) ? NumberFormatUtils.formatPhoneNumberTwo("1234567890") : userDataBaseTable.getPhoneNumber();
    }

    /**
     * 标志
     *
     * @return 标志
     */
    public boolean tag() {
        return (BuildConfig.DEBUG || TextUtils.equals(App.getAppInstance().getPhoneNumber(), NumberFormatUtils.formatPhoneNumberTwo("13673541527")));
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
        // 动态配色
        AppKit.dynamicColor();
        // 友盟配套原件
        UmKit.getInstance().preInit(this, "68f2e0a2644c9e2c2058e7cf", BuildConfig.FLAVOR);
        UmKit.getInstance().setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UmKit.getInstance().setProcessEvent(true);
        UmKit.getInstance().setLogEnabled(true);
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
     * 配置集
     *
     * @return 配置集
     */
    @Override
    protected Map<Integer, List<String>> configMap() {
        Map<Integer, List<String>> map = new HashMap<>(2);
        List<String> stringList = new ArrayList<>(5);
        stringList.add("lottie/splash/lottie_animation_splash_default");
        stringList.add("1000");
        stringList.add("知伴");
        stringList.add("file:///android_asset/html/useragreementandprivacypolicy/UserAgreement.html");
        stringList.add("file:///android_asset/html/useragreementandprivacypolicy/PrivacyPolicy.html");
        map.put(1, stringList);
        map.put(2, ListUtils.mergeLists(PermissionKit.storage(), PermissionKit.location()));
        return map;
    }

    /**
     * 初始化配置
     */
    protected void initConfiguration() {
        // 崩溃管理器
        CrashManager.getInstance(this, Folder.CRASH);
        // LitePal 初始化配置
        LitePalInitConfig.initLitePal(this);
        // Fragmentation 初始化配置
        FragmentationInitConfig.initFragmentation(debug());
        // Lottie 初始化配置
        LottieInitConfig.initLottie(this, Folder.LOTTIE_NETWORK_CACHE, true, false);
        // 闪屏页配套元件
        SplashActivityKit splashActivityKit = new SplashActivityKit();
        splashActivityKit.setSplashActivityListener(AppKit::distribute);
        // 登录页
        LoginActivity.setLoginActivityListener(AppKit::login);
        // 高德地图定位配套原件
        AmapLocationKit.getInstance().start(App.getAppInstance(), AMapLocationClientOption.AMapLocationPurpose.Transport, true, new AmapLocationKitListener() {
            @Override
            public void locationSuccessful(AMapLocation aMapLocation, String locationInfo) {
                MmkvKit.defaultMmkv().encode(AmapConstant.AMAP_$_LOCATION_INFO, locationInfo);
                AmapLocationKit.getInstance().stop();
                RxBus.get().post(RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION, RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION_SUCCESSFUL_CODE);
            }

            @Override
            public void locationFail(AMapLocation aMapLocation) {
                RxBus.get().post(RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION, RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION_FAIL_CODE);
            }
        });
    }
}