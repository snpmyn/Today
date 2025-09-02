package com.zsp.today.value;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.zsp.today.application.App;

/**
 * Created on 2025/9/2.
 *
 * @author 郑少鹏
 * @desc 权限枚举
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public enum PermissionEnum {
    /**
     * MANAGE_EXTERNAL_STORAGE
     * <p>
     * {@link App#permissionList()} 声明
     * {@link pool.module.splash.kit.SplashActivityKit#requestPermissions(FragmentActivity)} 申请
     */
    MANAGE_EXTERNAL_STORAGE(Manifest.permission.MANAGE_EXTERNAL_STORAGE),
    /**
     * ACCESS_FINE_LOCATION
     * <p>
     * {@link App#permissionList()} 声明
     * {@link pool.module.splash.kit.SplashActivityKit#requestPermissions(FragmentActivity)} 申请
     */
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    /**
     * ACCESS_BACKGROUND_LOCATION
     * <p>
     * {@link App#permissionList()} 声明
     * {@link pool.module.splash.kit.SplashActivityKit#requestPermissions(FragmentActivity)} 申请
     */
    ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    /**
     * POST_NOTIFICATIONS
     * <p>
     * {@link com.zsp.today.kit.MainActivityKit#checkPostNotificationsPermission(AppCompatActivity)} 声明
     * {@link com.zsp.today.kit.MainActivityKit#checkPostNotificationsPermission(AppCompatActivity)} 申请
     */
    POST_NOTIFICATIONS(PermissionX.permission.POST_NOTIFICATIONS),
    /**
     * SEND_SMS
     * <p>
     * {@link com.zsp.today.module.dangerous.kit.DangerousActivityKit#checkSendSmsPermission(AppCompatActivity)} 声明
     * {@link com.zsp.today.module.dangerous.kit.DangerousActivityKit#checkSendSmsPermission(AppCompatActivity)} 申请
     */
    SEND_SMS(Manifest.permission.SEND_SMS);
    /**
     * 权限
     */
    private final String permission;

    /**
     * constructor
     *
     * @param permission 权限
     */
    PermissionEnum(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}