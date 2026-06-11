package com.zsp.today.application.kit;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.zsp.today.BuildConfig;
import com.zsp.today.application.App;
import com.zsp.today.main.MainActivity;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.today.module.setting.kit.SharedPreferencesKit;
import com.zsp.youmeng.UmKit;

import litepal.kit.LitePalKit;
import pool.module.login.LoginActivity;
import pool.value.PoolConstant;
import util.intent.IntentJump;
import util.mmkv.MmkvKit;
import widget.broadcast.CrossAppBroadcastHelper;
import widget.broadcast.value.CrossAppBroadcastConstant;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.toast.ToastKt;

/**
 * Created on 2021/9/22
 *
 * @author zsp
 * @desc 应用配套元件
 */
public class AppKit {
    /**
     * 动态配色
     * <p>
     * Android 12+
     */
    public static void dynamicColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (SharedPreferencesKit.getInstance().dynamicColor()) {
                DynamicColors.applyToActivitiesIfAvailable(App.getAppInstance());
            }
        }
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     */
    public static void distribute(AppCompatActivity appCompatActivity) {
        if (App.getDebug()) {
            IntentJump.getInstance().jump(null, appCompatActivity, true, MainActivity.class);
            return;
        }
        UserDataBaseTable userDataBaseTable = App.getAppInstance().getUserDataBaseTable();
        if (null == userDataBaseTable) {
            IntentJump.getInstance().jump(null, appCompatActivity, true, LoginActivity.class);
        } else {
            UmKit.getInstance().userProfileMobile(userDataBaseTable.getPhoneNumber());
            IntentJump.getInstance().jump(null, appCompatActivity, true, MainActivity.class);
        }
    }

    /**
     * 登录
     *
     * @param appCompatActivity 活动
     * @param phoneNumber       手机号
     */
    public static void login(AppCompatActivity appCompatActivity, String phoneNumber) {
        BocDialogKit.getInstance(appCompatActivity).bocCommonLoading(appCompatActivity.getString(com.zsp.core.R.string.login), null);
        localSave(appCompatActivity, phoneNumber);
    }

    /**
     * 本地保存
     *
     * @param appCompatActivity 活动
     * @param phoneNumber       手机号
     */
    private static void localSave(AppCompatActivity appCompatActivity, String phoneNumber) {
        UserDataBaseTable userDataBaseTable = new UserDataBaseTable(phoneNumber, null);
        if (LitePalKit.getInstance().singleSave(userDataBaseTable)) {
            MmkvKit.defaultMmkv().encode(PoolConstant.LOGIN_$_PHONE_NUMBER, phoneNumber);
            UmKit.getInstance().onProfileSignIn(null, phoneNumber);
            UmKit.getInstance().userProfileMobile(phoneNumber);
            BocDialogKit.getInstance(appCompatActivity).end();
            IntentJump.getInstance().jump(null, appCompatActivity, true, MainActivity.class);
        } else {
            BocDialogKit.getInstance(appCompatActivity).end();
        }
    }

    /**
     * 初始化跨 APP 双向通信广播
     * <p>
     * 正式版发送 KILL_APP
     * 测试版收到 KILL_APP -> 测试版向正式版发送 LAUNCH_APP -> 测试版自身 KILL
     * 正式版收到 LAUNCH_APP -> 延迟 500ms -> 启动测试版 -> 正式版自身 KILL
     * <p>
     * 正式版延迟 500ms 是为避开测试版自身 KILL
     *
     * @param app App
     */
    public static void initCrossAppBroadcast(App app) {
        // 当前应用发送的 Action 标识
        String currentAction = (BuildConfig.ENVIRONMENT_TYPE == 2) ? CrossAppBroadcastHelper.ACTION_TO_APP_A : CrossAppBroadcastHelper.ACTION_TO_APP_B;
        // 目标应用接收的 Action 标识
        String targetAction = (BuildConfig.ENVIRONMENT_TYPE == 2) ? CrossAppBroadcastHelper.ACTION_TO_APP_B : CrossAppBroadcastHelper.ACTION_TO_APP_A;
        // 目标包名
        String targetPackageName = (BuildConfig.ENVIRONMENT_TYPE == 2) ? "com.zsp.today.develop" : "com.zsp.today.product";
        // 配置目标应用参数
        CrossAppBroadcastHelper.setTargetConfig(currentAction, targetAction, targetPackageName);
        // 设置广播消息接收的对外暴露回调接口
        CrossAppBroadcastHelper.setBroadcastCallback((action, message) -> {
            if (TextUtils.equals(message, CrossAppBroadcastConstant.KILL_APP)) {
                CrossAppBroadcastHelper.sendMessage(app, CrossAppBroadcastConstant.LAUNCH_APP);
                ToastKt.showToast(app.getPackageName());
            } else if (TextUtils.equals(message, CrossAppBroadcastConstant.LAUNCH_APP)) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    ToastKt.showToast(app.getPackageName());
                }, 500);
            }
        });
    }
}