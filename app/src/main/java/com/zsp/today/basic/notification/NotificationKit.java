package com.zsp.today.basic.notification;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zsp.today.R;

/**
 * Created on 2025/9/24.
 *
 * @author 郑少鹏
 * @desc 通知配套原件
 */
public class NotificationKit {
    /**
     * 账目通知信息
     *
     * @param context 上下文
     * @return 账目通知信息数组
     */
    @NonNull
    public static String[] accountNotificationInfo(@NonNull Context context) {
        String channelId = "ChannelIdAccountNotification";
        String channelName = context.getString(R.string.accountNotificationChinese);
        String channelDescription = context.getString(R.string.accountNotificationEnglish);
        return new String[]{channelId, channelName, channelDescription};
    }

    /**
     * 账目通知 ID
     *
     * @return 账目通知 ID
     */
    public static int accountNotificationId() {
        return 1;
    }

    /**
     * 周期通知信息
     *
     * @param context 上下文
     * @return 周期通知信息数组
     */
    @NonNull
    public static String[] periodicNotificationInfo(@NonNull Context context) {
        String channelId = "ChannelIdPeriodicNotification";
        String channelName = context.getString(R.string.periodicNotificationChinese);
        String channelDescription = context.getString(R.string.periodicNotificationEnglish);
        return new String[]{channelId, channelName, channelDescription};
    }

    /**
     * 周期通知 ID
     *
     * @return 周期通知 ID
     */
    public static int periodicNotificationId() {
        return 2;
    }
}