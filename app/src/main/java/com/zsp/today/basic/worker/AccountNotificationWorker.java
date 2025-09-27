package com.zsp.today.basic.worker;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zsp.today.MainActivity;
import com.zsp.today.R;
import com.zsp.today.basic.notification.NotificationKit;

import widget.notification.helper.NotificationHelper;

/**
 * Created on 2025/8/3.
 *
 * @author 郑少鹏
 * @desc 账目通知工作器
 * <p>
 * WorkManager 官方虽然称它可保证即使在应用退出甚至手机重启情况下仍执行之前注册的任务
 * 但国产手机因系统改动导致不可能
 */
public class AccountNotificationWorker extends Worker {
    private final Context context;

    public AccountNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.getInstance(context).createNotificationChannel(NotificationKit.accountNotificationInfo(context)[0], NotificationKit.accountNotificationInfo(context)[1], NotificationKit.accountNotificationInfo(context)[2]);
        Notification notification = NotificationHelper.getInstance(context).createCommonNotification(context, NotificationKit.accountNotificationInfo(context)[0], context.getString(R.string.todayAccount), context.getString(R.string.rememberToKeepAccount), R.drawable.ic_notification_white_56dp, MainActivity.class);
        NotificationHelper.getInstance(context).notify(NotificationKit.accountNotificationId(), notification);
        return Result.success();
    }
}