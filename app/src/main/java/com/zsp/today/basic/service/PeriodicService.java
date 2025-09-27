package com.zsp.today.basic.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.zsp.today.MainActivity;
import com.zsp.today.R;
import com.zsp.today.basic.notification.NotificationKit;

import widget.notification.helper.NotificationHelper;

/**
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
 * @desc 周期服务
 */
public class PeriodicService extends Service {
    private Handler handler;
    private int counter = 0;
    private final IBinder iBinder = new LocalBinder();
    private PeriodicServiceListener periodicServiceListener;

    public class LocalBinder extends Binder {
        public PeriodicService getService() {
            return PeriodicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.getInstance(this).createNotificationChannel(NotificationKit.periodicNotificationInfo(this)[0], NotificationKit.periodicNotificationInfo(this)[1], NotificationKit.periodicNotificationInfo(this)[2]);
        Notification notification = NotificationHelper.getInstance(this).createCommonNotification(this, NotificationKit.periodicNotificationInfo(this)[0], getString(R.string.serviceIsRunning), getString(R.string.inPeriodicOperation), R.drawable.ic_notification_white_56dp, MainActivity.class);
        startForeground(NotificationKit.periodicNotificationId(), notification);
        handler = new Handler();
        handler.post(updateTask);
    }

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            counter++;
            if (null != periodicServiceListener) {
                periodicServiceListener.execute(counter);
            }
            handler.postDelayed(this, 20000);
        }
    };

    public void setPeriodicServiceListener(PeriodicServiceListener periodicServiceListener) {
        this.periodicServiceListener = periodicServiceListener;
    }

    /**
     * 周期服务监听
     */
    public interface PeriodicServiceListener {
        /**
         * 执行
         *
         * @param count 数量
         */
        void execute(int count);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }
}