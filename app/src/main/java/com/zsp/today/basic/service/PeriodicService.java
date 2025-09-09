package com.zsp.today.basic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.zsp.today.MainActivity;
import com.zsp.today.R;

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
        NotificationHelper.getInstance(this).createNotificationChannel(getClass().getSimpleName(), getString(R.string.periodicNotificationChinese), getString(R.string.periodicNotificationEnglish));
        startForeground(1, NotificationHelper.getInstance(this).createNotification(this, getClass().getSimpleName(), getString(R.string.serviceIsRunning), getString(R.string.inPeriodicOperation), R.drawable.ic_notification_white_56dp, MainActivity.class));
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