package com.zsp.today.kit;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.MainActivity;
import com.zsp.today.application.App;
import com.zsp.today.module.login.UserDataBaseTable;

import java.lang.ref.SoftReference;

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
     * 分发
     *
     * @param appCompatActivity 活动
     */
    public void distribute(AppCompatActivity appCompatActivity) {
        if (App.getDebug()) {
            IntentJump.getInstance().jump(null, appCompatActivity, true, MainActivity.class);
            return;
        }
        IntentJump.getInstance().jump(null, appCompatActivity, true, (null == App.getAppInstance().getUserDataBaseTable()) ? LoginActivity.class : MainActivity.class);
    }

    /**
     * 登录
     *
     * @param appCompatActivity 活动
     * @param phoneNumber       手机号
     */
    public void login(AppCompatActivity appCompatActivity, String phoneNumber) {
        SoftReference<AppCompatActivity> softReference = new SoftReference<>(appCompatActivity);
        BocDialogKit.getInstance(appCompatActivity).bocCommonLoading(softReference.get().getString(com.zsp.core.R.string.login), null);
        localSave(appCompatActivity, phoneNumber);
    }

    /**
     * 本地保存
     *
     * @param appCompatActivity 活动
     * @param phoneNumber       手机号
     */
    private void localSave(AppCompatActivity appCompatActivity, String phoneNumber) {
        UserDataBaseTable userDataBaseTable = new UserDataBaseTable(phoneNumber, null);
        if (LitePalKit.getInstance().singleSave(userDataBaseTable)) {
            MmkvKit.defaultMmkv().encode(PoolConstant.LOGIN_$_PHONE_NUMBER, phoneNumber);
            BocDialogKit.getInstance(appCompatActivity).end();
            IntentJump.getInstance().jump(null, appCompatActivity, true, MainActivity.class);
        } else {
            BocDialogKit.getInstance(appCompatActivity).end();
        }
    }
}