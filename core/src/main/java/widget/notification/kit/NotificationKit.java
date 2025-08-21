package widget.notification.kit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

/**
 * Created on 2019/8/8.
 *
 * @author 郑少鹏
 * @desc 通知配套元件
 */
public class NotificationKit {
    /**
     * 通知配套元件请求码
     */
    public static final int NOTIFICATION_KIT_REQUEST_CODE = 0x002;

    /**
     * 通知允
     *
     * @param context 上下文
     * @return 通知允
     */
    public boolean notificationEnable(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        // areNotificationsEnabled 于 API 19+ 有效
        // API 19- 返 true（默开）
        return notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * 设置通知
     *
     * @param appCompatActivity 活动
     * @param respond           响应否
     */
    public void setNotification(AppCompatActivity appCompatActivity, boolean respond) {
        Intent intent = new Intent();
        // 跳通知设置页
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, appCompatActivity.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", appCompatActivity.getPackageName());
            intent.putExtra("app_uid", appCompatActivity.getApplicationInfo().uid);
        }
        if (respond) {
            appCompatActivity.startActivityForResult(intent, NOTIFICATION_KIT_REQUEST_CODE);
        } else {
            appCompatActivity.startActivity(intent);
        }
    }
}