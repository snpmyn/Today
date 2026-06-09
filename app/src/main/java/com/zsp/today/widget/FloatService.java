package com.zsp.today.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.zsp.today.R;

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
    public static final String ACTION_SHOW_FLOAT = "com.qtone.action.SHOW_FLOAT";
    /**
     * 外部控制悬浮窗隐藏的 Action
     */
    public static final String ACTION_HIDE_FLOAT = "com.qtone.action.HIDE_FLOAT";
    /**
     * 悬浮点击监听
     */
    private static OnFloatClickListener onFloatClickListener;
    private View floatView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    /**
     * 设置悬浮点击监听
     *
     * @param onFloatClickListener 悬浮点击监听
     */
    public static void setOnFloatClickListener(OnFloatClickListener onFloatClickListener) {
        FloatService.onFloatClickListener = onFloatClickListener;
    }

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
        // FloatService 自身的 Context 没有携带 AppTheme
        // 导致 MaterialButton 在检查 Theme 时失败
        // ContextThemeWrapper 给它手动套上 AppTheme
        // 故可正常创建
        // 将 R.style.Theme.Today 改为 R.style.Theme_Today
        /*ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, R.style.Theme_Today);*/
        //noinspection AndroidLintInflateParams
        floatView = LayoutInflater.from(this).inflate(R.layout.float_service, null);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 100;
        layoutParams.y = 300;
        windowManager.addView(floatView, layoutParams);
        initTouch();
    }

    private void initTouch() {
        View btn = floatView.findViewById(R.id.floatServiceMb);
        final int tagKey = R.id.floatServiceMb;
        btn.setOnTouchListener(new View.OnTouchListener() {
            // 点击位移阈值
            private static final int CLICK_ACTION_THRESHOLD = 10;
            // 双击判定的最大时间间隔（毫秒）
            // 300ms 比较符合人类手速
            private static final long DOUBLE_CLICK_DELAY = 300;
            private final Handler handler = new Handler(Looper.getMainLooper());
            int startX, startY;
            float touchX, touchY;
            // 用于处理双击逻辑的变量
            private int clickCount = 0;
            // 单击的延迟任务
            private final Runnable singleClickRunnable = () -> {
                btn.setTag(tagKey, "single");
                btn.performClick();
                // 记得重置计数器
                clickCount = 0;
            };
            private long firstClickTime = 0;

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
                        // 先判断当前触摸轨迹是否属于“点击”而非“拖拽”
                        if (Math.abs(deltaX) < CLICK_ACTION_THRESHOLD && Math.abs(deltaY) < CLICK_ACTION_THRESHOLD) {
                            clickCount++;
                            if (clickCount == 1) {
                                // 第一次点击
                                // 记录时间并启动一个延时任务
                                firstClickTime = System.currentTimeMillis();
                                handler.postDelayed(singleClickRunnable, DOUBLE_CLICK_DELAY);
                            } else if (clickCount == 2) {
                                long secondClickTime = System.currentTimeMillis();
                                // 如果第二次点击的时间与第一次在阈值内
                                if (secondClickTime - firstClickTime < DOUBLE_CLICK_DELAY) {
                                    // 核心
                                    // 取消之前排队的单击延时任务，防止单击、双击同时触发。
                                    handler.removeCallbacks(singleClickRunnable);
                                    btn.setTag(tagKey, "double");
                                    btn.performClick();
                                }
                                // 重置计数器
                                clickCount = 0;
                            }
                        }
                        return true;
                }
                return false;
            }
        });
        // 设置点击监听器接收由 performClick() 触发的系统通知
        btn.setOnClickListener(v -> {
            if (onFloatClickListener == null) {
                return;
            }
            Object tag = v.getTag(tagKey);
            if ("double".equals(tag)) {
                onFloatClickListener.onDoubleClick(v);
            } else {
                // 如果 tag 没拿到或者为 "single"
                // 一律视为单击走安全降级
                onFloatClickListener.onSingleClick(v);
            }
            v.setTag(tagKey, null);
        });
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

    /**
     * 悬浮点击监听
     */
    public interface OnFloatClickListener {
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