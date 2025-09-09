package widget.notification.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

/**
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
 * @desc 通知帮助
 */
public class NotificationHelper {
    /**
     * 通知管理器
     */
    private static NotificationManager notificationManager;

    public static NotificationHelper getInstance(@NonNull Context context) {
        notificationManager = context.getSystemService(NotificationManager.class);
        return NotificationHelper.InstanceHolder.INSTANCE;
    }

    /**
     * 创建通知渠道
     * <p>
     * Android 8.0+ 需要
     *
     * @param channelId          渠道 ID
     * @param channelName        渠道名
     * @param channelDescription 渠道描述
     */
    public void createNotificationChannel(String channelId, String channelName, String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.setShowBadge(false);
            // 创建通知渠道
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * 创建通知
     *
     * @param context   上下文
     * @param channelId 渠道 ID
     * @param title     标题
     * @param content   内容
     * @param icon      图标
     * @param cls       The component class that is to be used for the intent.
     * @return 通知
     */
    public Notification createNotification(Context context, String channelId, String title, String content, int icon, Class<?> cls) {
        // Intent
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setOngoing(true).setSmallIcon(icon).setAutoCancel(false).setContentTitle(title).setContentText(content).setContentIntent(pendingIntent).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    /**
     * 提醒
     *
     * @param notification 通知
     */
    public void notify(Notification notification) {
        notificationManager.notify(1, notification);
    }

    private static final class InstanceHolder {
        static final NotificationHelper INSTANCE = new NotificationHelper();
    }
}