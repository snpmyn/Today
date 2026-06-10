package com.zsp.today.basic.service;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import timber.log.Timber;

/**
 * Created on 2026/6/10.
 *
 * @author 郑少鹏
 * @desc APP 切换管理器
 * <p>
 * AppSwitchManager.init(this);
 * <p>
 * AppSwitchManager.requestExit(this, "com.qtone.scandemo.test");
 * AppSwitchManager.waitAndLaunch(this, "com.qtone.scandemo.test");
 */
public class AppSwitchManager {
    public static final String ACTION_EXIT = "com.qtone.app.ACTION_EXIT";
    public static final String ACTION_EXIT_FINISH = "com.qtone.app.ACTION_EXIT_FINISH";
    private static boolean initialized = false;

    /**
     * 初始化
     *
     * @param application Application
     */
    public static void init(Application application) {
        if (initialized) {
            return;
        }
        initialized = true;
        registerExitReceiver(application);
    }

    /**
     * 注册退出广播
     *
     * @param application Application
     */
    private static void registerExitReceiver(Application application) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT);
        ContextCompat.registerReceiver(application, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 1. 关闭所有 Activity
                /*ActivityManager.getInstance().finishAll();*/
                // 2. 释放 SDK（可改成回调）
                try {
                    Class<?> clazz = Class.forName("com.huagaoscan.sdk.HGScanManager");
                    Object instance = clazz.getMethod("getInstance").invoke(null);
                    clazz.getMethod("unInit").invoke(instance);
                } catch (Exception exception) {
                    Timber.e(exception);
                }
                // 3. 通知发送方：我准备退出了
                Intent finishIntent = new Intent(ACTION_EXIT_FINISH);
                finishIntent.setPackage(context.getPackageName());
                context.sendBroadcast(finishIntent);
                // 4. 杀进程
                Process.killProcess(Process.myPid());
            }
        }, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    /**
     * 请求退出
     * <p>
     * A 调用请求退出 B
     *
     * @param context       上下文
     * @param targetPackage 目标包
     */
    public static void requestExit(@NonNull Context context, String targetPackage) {
        Intent intent = new Intent(ACTION_EXIT);
        intent.setPackage(targetPackage);
        context.sendBroadcast(intent);
    }

    /**
     * 等待启动
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void waitAndLaunch(Context context, String packageName) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, Intent intent) {
                context.unregisterReceiver(this);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (null != launchIntent) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(launchIntent);
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_EXIT_FINISH);
        ContextCompat.registerReceiver(context, broadcastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }
}