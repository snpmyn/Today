package com.zsp.today.kit;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.permissionx.guolindev.PermissionX;
import com.zsp.today.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import util.animation.AnimationManager;
import util.view.ViewUtils;
import widget.notification.fragment.NotificationEnableDialogFragment;
import widget.notification.kit.NotificationKit;
import widget.notification.listener.NotificationEnableDialogOnClickListener;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;

/**
 * Created on 2021/1/27
 *
 * @author zsp
 * @desc 主页配套元件
 */
public class MainActivityKit {
    /**
     * 执行
     *
     * @param appCompatActivity 活动
     */
    public void execute(AppCompatActivity appCompatActivity) {
        checkPostNotificationsPermission(appCompatActivity);
    }

    /**
     * 检查发送通知权限
     *
     * @param appCompatActivity 活动
     */
    private void checkPostNotificationsPermission(AppCompatActivity appCompatActivity) {
        PermissionxKit.execute(appCompatActivity, List.of(PermissionX.permission.POST_NOTIFICATIONS), R.string.receiveNotificationAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                accountNotification(appCompatActivity);
                RestoreKit.getInstance().restore(appCompatActivity);
            }

            @Override
            public void allGrantedContrary() {
                showNotificationEnableDialog(appCompatActivity);
            }
        });
    }

    /**
     * 显示通知允对话框
     *
     * @param appCompatActivity 活动
     */
    private void showNotificationEnableDialog(AppCompatActivity appCompatActivity) {
        NotificationKit notificationKit = new NotificationKit();
        if (!notificationKit.notificationEnable(appCompatActivity)) {
            NotificationEnableDialogFragment notificationEnableDialogFragment = new NotificationEnableDialogFragment();
            notificationEnableDialogFragment.setNotificationEnableDialogOnClickListener(new NotificationEnableDialogOnClickListener() {
                @Override
                public void talkAboutItNext() {
                    RestoreKit.getInstance().restore(appCompatActivity);
                }

                @Override
                public void goToOpen() {
                    notificationKit.setNotification(appCompatActivity, true);
                }
            });
            notificationEnableDialogFragment.show(appCompatActivity.getSupportFragmentManager(), notificationEnableDialogFragment.TAG);
        }
    }

    /**
     * 账目通知
     *
     * @param appCompatActivity 活动
     */
    private void accountNotification(AppCompatActivity appCompatActivity) {
        // 可定义最短重复间隔 15 分钟（与 JobScheduler API 相同）
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(AccountNotificationWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(appCompatActivity).enqueue(periodicWorkRequest);
    }

    /**
     * 隐藏底部导航视图
     *
     * @param bottomNavigationView 底部导航视图
     */
    public void hideBottomNavigationView(BottomNavigationView bottomNavigationView) {
        ViewUtils.hideView(bottomNavigationView, View.GONE);
    }

    /**
     * 显示底部导航视图
     *
     * @param bottomNavigationView 底部导航视图
     */
    public void showBottomNavigationView(BottomNavigationView bottomNavigationView) {
        ViewUtils.showView(bottomNavigationView);
        AnimationManager.alphaShow(bottomNavigationView, 1000, null);
    }
}