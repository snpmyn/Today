package com.zsp.today.basic.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zsp.today.MainActivity;
import com.zsp.today.R;

import widget.notification.helper.NotificationHelper;

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

    public AccountNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.getInstance(context).createNotificationChannel(getClass().getSimpleName(), context.getString(R.string.accountNotificationChinese), context.getString(R.string.accountNotificationEnglish));
        NotificationHelper.getInstance(context).notify(NotificationHelper.getInstance(context).createNotification(context, getClass().getSimpleName(), context.getString(R.string.todayAccount), context.getString(R.string.rememberToKeepAccount), R.drawable.ic_notification_white_56dp, MainActivity.class));
        return Result.success();
    }
}