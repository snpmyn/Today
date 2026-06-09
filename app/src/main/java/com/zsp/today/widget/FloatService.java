package com.zsp.today.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.zsp.today.R;

import widget.toast.ToastKt;

/**
 * Created on 2026/6/8.
 *
 * @author 郑少鹏
 * @desc 悬浮服务
 */
public class FloatService extends Service {
    /**
     * 外部控制悬浮窗显示的 Action
     */
    public static final String ACTION_SHOW_FLOAT = "com.zsp.today.action.SHOW_FLOAT";
    /**
     * 外部控制悬浮窗隐藏的 Action
     */
    public static final String ACTION_HIDE_FLOAT = "com.zsp.today.action.HIDE_FLOAT";
    private View floatView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundServiceCompat();
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_SHOW_FLOAT.equals(action)) {
                showFloatView();
            } else if (ACTION_HIDE_FLOAT.equals(action)) {
                hideFloatView();
            }
        }
        // 确保服务被系统意外杀死后能尝试重启
        return START_STICKY;
    }

    /**
     * 显示悬浮视图
     */
    public void showFloatView() {
        if (floatView != null && floatView.getVisibility() != View.VISIBLE) {
            floatView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏悬浮视图
     */
    public void hideFloatView() {
        if (floatView != null && floatView.getVisibility() == View.VISIBLE) {
            floatView.setVisibility(View.GONE);
        }
    }

    private void createFloatView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //noinspection AndroidLintInflateParams
        floatView = LayoutInflater.from(this).inflate(R.layout.layout_float_button, null);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 100;
        layoutParams.y = 300;
        windowManager.addView(floatView, layoutParams);
        initTouch();
    }

    private void initTouch() {
        View btn = floatView.findViewById(R.id.float_btn);
        btn.setOnTouchListener(new View.OnTouchListener() {
            // 点击位移阈值
            private static final int CLICK_ACTION_THRESHOLD = 10;
            int startX, startY;
            float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = layoutParams.x;
                        startY = layoutParams.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = startX + (int) (event.getRawX() - touchX);
                        layoutParams.y = startY + (int) (event.getRawY() - touchY);
                        // 当 View 为 GONE 时不处理更新
                        // 防止隐身状态下被误拖动
                        if (floatView.getVisibility() == View.VISIBLE) {
                            windowManager.updateViewLayout(floatView, layoutParams);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        float deltaX = event.getRawX() - touchX;
                        float deltaY = event.getRawY() - touchY;
                        if (Math.abs(deltaX) < CLICK_ACTION_THRESHOLD && Math.abs(deltaY) < CLICK_ACTION_THRESHOLD) {
                            // 触发点击
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });
        // 可以在这里或外部直接设置点击监听
        btn.setOnClickListener(v -> ToastKt.showToast("悬浮"));
    }

    private void startForegroundServiceCompat() {
        Notification notification = new NotificationCompat.Builder(this, "float_channel").setContentTitle("悬浮按钮运行中").setSmallIcon(android.R.drawable.ic_dialog_info).build();
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("float_channel", "Float Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ((null != windowManager) && (null != floatView)) {
            windowManager.removeView(floatView);
        }
    }
}