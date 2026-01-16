package com.zsp.today.application.kit;

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
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
import widget.dialog.bocdialog.kit.BocDialogKit;

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
}