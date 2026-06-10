package com.zsp.today.main.kit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zsp.today.R;
import com.zsp.today.basic.restore.kit.RestoreKit;
import com.zsp.today.basic.version.kit.VersionKit;
import com.zsp.today.basic.worker.AccountNotificationWorker;
import com.zsp.today.main.adapter.MainActivityFragmentStateAdapter;
import com.zsp.today.widget.FloatingService;

import java.util.concurrent.TimeUnit;

import util.animation.AnimationManager;
import util.listener.AppListener;
import util.view.ViewUtils;
import widget.notification.fragment.NotificationEnableDialogFragment;
import widget.notification.kit.NotificationKit;
import widget.notification.listener.NotificationEnableDialogOnClickListener;
import widget.permissionx.kit.PermissionKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.toast.ToastKt;

/**
 * Created on 2021/1/27
 *
 * @author zsp
 * @desc 主页配套元件
 */
public class MainActivityKit {
    /**
     * 开始页面
     *
     * @param appCompatActivity 活动
     * @param viewPager2        ViewPager2
     */
    public void startPage(AppCompatActivity appCompatActivity, @NonNull ViewPager2 viewPager2) {
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(new MainActivityFragmentStateAdapter(appCompatActivity));
    }

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
        PermissionxKit.execute(appCompatActivity, true, PermissionKit.notification(), R.string.receiveNotificationAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                accountNotification(appCompatActivity);
                RestoreKit.getInstance().restore(appCompatActivity, () -> VersionKit.check(appCompatActivity, false));
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
                public void nextTime() {
                    RestoreKit.getInstance().restore(appCompatActivity, () -> VersionKit.check(appCompatActivity, false));
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

    /**
     * 初始化悬浮服务
     *
     * @param appCompatActivity 活动
     */
    public void initFloatService(AppCompatActivity appCompatActivity) {
        // 不可直弹悬浮视图 + 只能启动服务
        if (Settings.canDrawOverlays(appCompatActivity)) {
            // 1. 先确保服务已经启动（如果是首次启动）
            startFloatService(appCompatActivity);
            AppListener.getInstance().registerCallback(areForeground -> {
                // 2. 根据前后台状态控制显示或隐藏
                if (areForeground) {
                    // App 回到前台
                    // 显示悬浮视图
                    Intent showIntent = new Intent(appCompatActivity, FloatingService.class);
                    showIntent.setAction(FloatingService.ACTION_SHOW_FLOATING);
                    appCompatActivity.startService(showIntent);
                } else {
                    // App 退到后台
                    // 隐藏悬浮视图
                    Intent hideIntent = new Intent(appCompatActivity, FloatingService.class);
                    hideIntent.setAction(FloatingService.ACTION_HIDE_FLOATING);
                    appCompatActivity.startService(hideIntent);
                }
            });
        } else {
            // 没权限时只能引导用户去设置页
            requestOverlayPermission(appCompatActivity);
        }
        FloatingService.setOnFloatingClickListener(new FloatingService.OnFloatingClickListener() {
            /**
             * 单击
             * @param view 视图
             */
            @Override
            public void onSingleClick(View view) {
                ToastKt.showToast("单击");
            }

            /**
             * 双击
             * @param view 视图
             */
            @Override
            public void onDoubleClick(View view) {
                ToastKt.showToast("双击");
            }
        });
    }

    /**
     * 开始悬浮服务
     *
     * @param appCompatActivity 活动
     */
    private void startFloatService(AppCompatActivity appCompatActivity) {
        Intent intent = new Intent(appCompatActivity, FloatingService.class);
        intent.setAction(FloatingService.ACTION_SHOW_FLOATING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appCompatActivity.startForegroundService(intent);
        } else {
            appCompatActivity.startService(intent);
        }
    }

    /**
     * 申请悬浮权限
     *
     * @param appCompatActivity 活动
     */
    private void requestOverlayPermission(@NonNull AppCompatActivity appCompatActivity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + appCompatActivity.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appCompatActivity.startActivity(intent);
    }
}