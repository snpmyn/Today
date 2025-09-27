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
 * 通知帮助者
 * <p>
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
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
     *                           Android 通知渠道通过 channelId 唯一标识
     *                           如果写了两个渠道，但用同一 ID，则后面创建的会覆盖前面的，系统里只显示一个通知类别。
     *                           <p>
     *                           创建渠道后不可修改或覆盖
     *                           一旦渠道创建，name 和 description 基本不可修改 (除非卸载重装 App)。
     *                           如果调试时改了名字，但 ID 没变，则系统仍然只显示旧的。
     *                           调试时，可先卸载 App 再安装，避免渠道信息被缓存。
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
     * 创建普通通知
     *
     * @param context   上下文
     * @param channelId 渠道 ID
     *                  Android 通知渠道通过 channelId 唯一标识
     *                  如果写了两个渠道，但用同一 ID，则后面创建的会覆盖前面的，系统里只显示一个通知类别。
     *                  <p>
     *                  创建渠道后不可修改或覆盖
     *                  一旦渠道创建，name 和 description 基本不可修改 (除非卸载重装 App)。
     *                  如果调试时改了名字，但 ID 没变，则系统仍然只显示旧的。
     *                  调试时，可先卸载 App 再安装，避免渠道信息被缓存。
     * @param title     标题
     * @param content   内容
     * @param icon      图标
     * @param cls       The component class that is to be used for the intent.
     * @return 通知
     */
    public Notification createCommonNotification(Context context, String channelId, String title, String content, int icon, Class<?> cls) {
        // Intent
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setOngoing(true).setSmallIcon(icon).setAutoCancel(false).setContentTitle(title).setContentText(content).setContentIntent(pendingIntent).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    /**
     * 创建进度通知
     *
     * @param context       上下文
     * @param channelId     渠道 ID
     *                      Android 通知渠道通过 channelId 唯一标识
     *                      如果写了两个渠道，但用同一 ID，则后面创建的会覆盖前面的，系统里只显示一个通知类别。
     *                      <p>
     *                      创建渠道后不可修改或覆盖
     *                      一旦渠道创建，name 和 description 基本不可修改 (除非卸载重装 App)。
     *                      如果调试时改了名字，但 ID 没变，则系统仍然只显示旧的。
     *                      调试时，可先卸载 App 再安装，避免渠道信息被缓存。
     * @param title         标题
     * @param content       内容
     * @param icon          图标
     * @param progress      进度
     * @param indeterminate 不定
     * @param ongoing       正在进行
     * @return 通知
     */
    public Notification createProgressNotification(Context context, String channelId, String title, String content, int icon, int progress, boolean indeterminate, boolean ongoing) {
        return new NotificationCompat.Builder(context, channelId).setSmallIcon(icon).setContentTitle(title).setContentText(content).setProgress(100, progress, indeterminate).setOngoing(ongoing).build();
    }

    /**
     * 唤醒
     *
     * @param notificationId 通知 ID
     *                       <p>
     *                       系统用来区分不同的通知实例
     *                       <p>
     *                       同一 ID
     *                       新通知会覆盖旧通知
     *                       <p>
     *                       不同 ID
     *                       系统会显示多个通知
     * @param notification   通知
     */
    public void notify(int notificationId, Notification notification) {
        notificationManager.notify(notificationId, notification);
    }

    private static final class InstanceHolder {
        static final NotificationHelper INSTANCE = new NotificationHelper();
    }
}