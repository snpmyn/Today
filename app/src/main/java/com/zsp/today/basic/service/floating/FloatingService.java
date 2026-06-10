package com.zsp.today.basic.service.floating;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

import com.zsp.today.R;
import com.zsp.today.basic.notification.NotificationKit;

import widget.notification.helper.NotificationHelper;

/**
 * @decs: 悬浮服务
 * @author: 郑少鹏
 * @date: 2026/6/9 20:07
 * @version: v 1.0
 */
public class FloatingService extends Service {
    /**
     * 显示悬浮动作
     */
    public static final String ACTION_SHOW_FLOATING = "com.zsp.today.action.SHOW_FLOATING";
    /**
     * 隐藏悬浮动作
     */
    public static final String ACTION_HIDE_FLOATING = "com.zsp.today.action.HIDE_FLOATING";
    /**
     * 根视图
     */
    private View rootView;
    /**
     * 是否已关联
     */
    private boolean attached;
    /**
     * 窗口管理器
     */
    private WindowManager windowManager;
    /**
     * 布局参数
     */
    private WindowManager.LayoutParams layoutParams;
    /**
     * 悬浮点击监听
     */
    private static OnFloatingClickListener onFloatingClickListener;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();
        // 窗口管理器
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 布局参数
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.x = 100;
        layoutParams.y = 300;
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        // ContextThemeWrapper
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, com.zsp.core.R.style.Theme_Today);
        // 根视图
        rootView = LayoutInflater.from(contextThemeWrapper).inflate(R.layout.floating_service, null, false);
        // 初始化触摸
        initTouch(rootView.findViewById(R.id.floatingServiceMb));
        // 创建通知
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            if (ACTION_SHOW_FLOATING.equals(intent.getAction())) {
                showFloatingView();
            }
            if (ACTION_HIDE_FLOATING.equals(intent.getAction())) {
                hideFloatingView();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        hideFloatingView();
        onFloatingClickListener = null;
        super.onDestroy();
    }

    /**
     * 初始化触摸
     *
     * @param view 视图
     */
    private void initTouch(@NonNull View view) {
        Handler handler = new Handler(Looper.getMainLooper());
        int slop = ViewConfiguration.get(this).getScaledTouchSlop();
        view.setOnTouchListener(new View.OnTouchListener() {
            int sx, sy;
            float tx, ty;
            boolean drag;
            int clickCount;
            long firstTime;
            final Runnable single = () -> {
                if ((clickCount == 1) && (null != onFloatingClickListener)) {
                    onFloatingClickListener.onSingleClick(view);
                }
                clickCount = 0;
            };

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacks(single);
                        sx = layoutParams.x;
                        sy = layoutParams.y;
                        tx = e.getRawX();
                        ty = e.getRawY();
                        drag = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = (e.getRawX() - tx);
                        float dy = (e.getRawY() - ty);
                        if ((Math.abs(dx) > slop) || (Math.abs(dy) > slop)) {
                            drag = true;
                            layoutParams.x = (sx + (int) dx);
                            layoutParams.y = (sy + (int) dy);
                            if (attached) {
                                windowManager.updateViewLayout(rootView, layoutParams);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (drag) {
                            snapToEdge();
                            clickCount = 0;
                            return true;
                        }
                        view.performClick();
                        clickCount++;
                        if (clickCount == 1) {
                            firstTime = System.currentTimeMillis();
                            handler.postDelayed(single, 300);
                        } else if (clickCount == 2) {
                            if ((System.currentTimeMillis() - firstTime) < 300) {
                                handler.removeCallbacks(single);
                                if (null != onFloatingClickListener) {
                                    onFloatingClickListener.onDoubleClick(view);
                                }
                            }
                            clickCount = 0;
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(single);
                        clickCount = 0;
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * 吸附至边缘
     */
    private void snapToEdge() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int target = (layoutParams.x < (displayMetrics.widthPixels / 2)) ? 0 : (displayMetrics.widthPixels - rootView.getWidth());
        ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.x, target);
        valueAnimator.setDuration(180);
        valueAnimator.addUpdateListener(a -> {
            layoutParams.x = (Integer) a.getAnimatedValue();
            if (attached) {
                windowManager.updateViewLayout(rootView, layoutParams);
            }
        });
        valueAnimator.start();
    }

    /**
     * 创建通知
     */
    private void createNotification() {
        NotificationHelper.getInstance(this).createNotificationChannel(NotificationKit.floatingNotificationInfo(this)[0], NotificationKit.floatingNotificationInfo(this)[1], NotificationKit.floatingNotificationInfo(this)[2]);
        Notification notification = NotificationHelper.getInstance(this).createCommonNotification(this, NotificationKit.periodicNotificationInfo(this)[0], getString(R.string.serviceIsRunning), getString(R.string.inFloatingOperation), R.drawable.ic_notification_white_56dp, FloatingService.class);
        startForeground(NotificationKit.floatingNotificationId(), notification);
    }

    /**
     * 显示悬浮视图
     */
    public void showFloatingView() {
        if (!attached) {
            windowManager.addView(rootView, layoutParams);
            attached = true;
        }
        rootView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏悬浮视图
     */
    public void hideFloatingView() {
        if (attached) {
            windowManager.removeViewImmediate(rootView);
            attached = false;
        }
    }

    /**
     * 设置悬浮点击监听
     *
     * @param onFloatingClickListener 悬浮点击监听
     */
    public static void setOnFloatingClickListener(OnFloatingClickListener onFloatingClickListener) {
        FloatingService.onFloatingClickListener = onFloatingClickListener;
    }

    /**
     * 悬浮点击监听
     */
    public interface OnFloatingClickListener {
        /**
         * 单击
         *
         * @param view 视图
         */
        void onSingleClick(View view);

        /**
         * 双击
         *
         * @param view 视图
         */
        void onDoubleClick(View view);
    }
}