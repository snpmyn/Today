package com.zsp.today.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zsp.today.R;

import pool.module.splash.SplashActivity;

/**
 * Created on 2025/8/3.
 *
 * @author 郑少鹏
 * @desc 账目通知工作器
 * <p>
 * WorkManager 官方虽然称它可以保证即使在应用退出甚至手机重启情况下，之前注册的任务仍然将会得到执行。
 * 但在国产手机中是不可能的，因为系统做了改动。
 * 但在国产机上测试退出后，再进来也会执行之前的任务，这个时候可能会有重复的任务执行。
 */
public class AccountNotificationWorker extends Worker {
    private final Context context;
    private final String channelId = "account_notification_worker";

    public AccountNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // 创建通知
        createNotification();
        return Result.success();
    }

    /**
     * 创建通知
     */
    private void createNotification() {
        // 通知管理器
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建通知渠道
        createNotificationChannel(notificationManager);
        // Intent
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.ic_notification_white_56dp).setContentTitle(context.getString(R.string.todayAccount)).setContentText(context.getString(R.string.rememberToKeepAccount)).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        // 显示通知
        notificationManager.notify(1, builder.build());
    }

    /**
     * 创建通知渠道
     * <p>
     * Android 8.0+ 需要
     *
     * @param notificationManager 通知管理器
     */
    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道
            NotificationChannel notificationChannel = new NotificationChannel(channelId, context.getString(R.string.accountNotificationChinese), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(context.getString(R.string.accountNotificationEnglish));
            // 创建通知渠道
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}