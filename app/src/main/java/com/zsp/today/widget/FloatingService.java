package com.zsp.today.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
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

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;

import com.zsp.today.R;

import java.lang.ref.WeakReference;

/**
 * Created on 2026/6/8.
 *
 * @author 郑少鹏
 * @desc 悬浮服务
 */
public class FloatingService extends Service {
    /**
     * 外部控制悬浮显示的 Action
     */
    public static final String ACTION_SHOW_FLOATING = "com.zsp.today.action.SHOW_FLOATING";
    /**
     * 外部控制悬浮隐藏的 Action
     */
    public static final String ACTION_HIDE_FLOATING = "com.zsp.today.action.HIDE_FLOATING";
    /**
     * 悬浮点击监听
     * <p>
     * 用静态弱引用
     * 允许外部静态设置
     * 同时规避内存泄漏
     */
    private static WeakReference<OnFloatingClickListener> onFloatingClickListener;
    /**
     * 悬浮视图
     */
    private View floatingView;
    /**
     * 窗口管理器
     */
    private WindowManager windowManager;
    /**
     * 布局参数
     */
    private WindowManager.LayoutParams layoutParams;
    /**
     * 悬浮视图是否已添加到 WindowManager 中
     */
    private boolean isFloatingViewAddToWindowManager = false;

    /**
     * 设置悬浮点击监听
     *
     * @param onFloatingClickListener 悬浮点击监听
     */
    public static void setOnFloatingClickListener(OnFloatingClickListener onFloatingClickListener) {
        if (null == onFloatingClickListener) {
            FloatingService.onFloatingClickListener = null;
        } else {
            FloatingService.onFloatingClickListener = new WeakReference<>(onFloatingClickListener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 窗口管理器
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 初始化布局参数
        initLayoutParams();
        // 动态创建视图
        createFloatView();
        // 创建通知
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_SHOW_FLOATING.equals(action)) {
                showFloatingView();
            } else if (ACTION_HIDE_FLOATING.equals(action)) {
                hideFloatingView();
            }
        }
        // 确保服务被系统意外杀死后能尝试重启
        return START_STICKY;
    }

    /**
     * 初始化布局参数
     */
    private void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 100;
        layoutParams.y = 300;
    }

    /**
     * 创建悬浮视图
     */
    private void createFloatView() {
        if (null != floatingView) {
            return;
        }
        // FloatingService 自身的 Context 没有携带 AppTheme
        // 导致 MaterialButton 在检查 Theme 时失败
        // ContextThemeWrapper 给它手动套上 AppTheme
        // 故可正常创建
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, com.zsp.core.R.style.Theme_Today);
        //noinspection AndroidLintInflateParams
        floatingView = LayoutInflater.from(contextThemeWrapper).inflate(R.layout.floating_service, null);
        // 初始化触摸
        initTouch();
    }

    /**
     * 初始化触摸
     */
    private void initTouch() {
        View btn = floatingView.findViewById(R.id.floatingServiceMb);
        final int tagKey = R.id.floatingServiceMb;
        btn.setOnTouchListener(new View.OnTouchListener() {
            // 点击位移阈值
            private static final int CLICK_ACTION_THRESHOLD = 10;
            // 移动防抖采样阈值
            // 防止高刷屏或手指微颤导致高频更新布局引起卡顿
            private static final int MOVE_POLL_THRESHOLD = 5;
            // 双击判定的最大时间间隔（毫秒）
            // 300ms 比较符合人类手速
            private static final long DOUBLE_CLICK_DELAY = 300;
            private final Handler handler = new Handler(Looper.getMainLooper());
            int startX, startY;
            float touchX, touchY;
            // 用于处理双击逻辑的变量
            private int clickCount = 0;
            // 标记当前触摸轨迹是否已经确认为拖拽状态
            private boolean isDragging = false;
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
                // 采用 getActionMasked 支持多点触控过滤
                // 避免多指按下时悬浮窗无故物理跳变
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // 新一轮触摸开始时
                        // 强行移除可能残留在队列中的上一次单击延迟任务
                        // 规避多击造成的计数紊乱
                        handler.removeCallbacks(singleClickRunnable);
                        startX = layoutParams.x;
                        startY = layoutParams.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaMoveX = event.getRawX() - touchX;
                        float deltaMoveY = event.getRawY() - touchY;
                        // 防抖判定
                        // 总位移超过防抖阈值时才进行控件位置更新
                        // 提升滑动平滑度降低 CPU 消耗
                        if (Math.abs(deltaMoveX) > MOVE_POLL_THRESHOLD || Math.abs(deltaMoveY) > MOVE_POLL_THRESHOLD) {
                            isDragging = true;
                            layoutParams.x = startX + (int) deltaMoveX;
                            layoutParams.y = startY + (int) deltaMoveY;
                            // 当 View 已经添加到窗口时才处理更新
                            if (floatingView != null && isFloatingViewAddToWindowManager) {
                                windowManager.updateViewLayout(floatingView, layoutParams);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        float deltaX = event.getRawX() - touchX;
                        float deltaY = event.getRawY() - touchY;
                        // 先判断当前触摸轨迹是否属于“点击”而非“拖拽”
                        if (!isDragging && Math.abs(deltaX) < CLICK_ACTION_THRESHOLD && Math.abs(deltaY) < CLICK_ACTION_THRESHOLD) {
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
                                    // 取消之前排队的单击延时任务
                                    // 防止单击、双击同时触发
                                    handler.removeCallbacks(singleClickRunnable);
                                    btn.setTag(tagKey, "double");
                                    btn.performClick();
                                }
                                // 重置计数器
                                clickCount = 0;
                            }
                        } else {
                            // 若判定为拖拽事件结束
                            // 直接清空连击计数器
                            // 并将 tag 显式隔离防止部分 ROM 误触发 onClick
                            clickCount = 0;
                            btn.setTag(tagKey, "drag_interrupted");
                        }
                        return true;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // 一旦有第二根手指触摸屏幕
                        // 直接拦截并终止本轮拖拽
                        // 有效抑制多指连动带来的坐标抖动
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        // 异常取消流程下重置计数器并移除定时器
                        clickCount = 0;
                        handler.removeCallbacks(singleClickRunnable);
                        return true;
                }
                return false;
            }
        });
        // 设置点击监听器接收由 performClick() 触发的系统通知
        btn.setOnClickListener(v -> {
            // 从静态弱引用中尝试获取外部监听器实例
            OnFloatingClickListener listener = ((null != onFloatingClickListener) ? onFloatingClickListener.get() : null);
            if (null == listener) {
                return;
            }
            Object tag = v.getTag(tagKey);
            if ("double".equals(tag)) {
                listener.onDoubleClick(v);
            } else if ("single".equals(tag)) {
                listener.onSingleClick(v);
            }
            // 拦截掉 "drag_interrupted" 或 null
            // 不向下分发 + 确保拖拽后松手绝不误触单击
            v.setTag(tagKey, null);
        });
    }

    /**
     * 创建通知
     */
    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("floating_channel", "Floating Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        Notification notification = new NotificationCompat.Builder(this, "floating_channel").setContentTitle("悬浮按钮运行中").setSmallIcon(android.R.drawable.ic_dialog_info).build();
        // 针对 Android 14 (API 34) 及以上系统
        // 适配前台服务类型的声明
        // 杜绝系统崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // SPECIAL_USE 权限权限极高且不易受限
            // Google 审核非常严格
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }
    }

    /**
     * 显示悬浮视图
     */
    public void showFloatingView() {
        if (!isFloatingViewAddToWindowManager && (null != windowManager) && (null != floatingView)) {
            // 通过 addView 真正加入窗口
            // 并使用 isFloatingViewAddToWindowManager 控制状态
            // 防止重复添加崩溃
            windowManager.addView(floatingView, layoutParams);
            isFloatingViewAddToWindowManager = true;
        }
        // 当 View 属于加载状态时
        // 确保其显现
        if ((null != floatingView) && (floatingView.getVisibility() != View.VISIBLE)) {
            floatingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏悬浮视图
     */
    public void hideFloatingView() {
        // 隐藏时不仅需要 setGONE
        // 最好的方案是直接从窗口管理器中彻底移除
        // 防止其透明区域继续拦截、干扰用户的屏幕底层点击
        if (isFloatingViewAddToWindowManager && (null != windowManager) && (null != floatingView)) {
            // 移出窗口管理队列前
            // 最好先将可见性置为 GONE
            // 防止 removeView 的异步脱离瞬间引起视图闪烁或未解绑状态下的非法调用
            floatingView.setVisibility(View.GONE);
            windowManager.removeView(floatingView);
            isFloatingViewAddToWindowManager = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 彻底移除悬浮窗并释放引用
        if (isFloatingViewAddToWindowManager && (null != windowManager) && (null != floatingView)) {
            windowManager.removeView(floatingView);
            isFloatingViewAddToWindowManager = false;
        }
        floatingView = null;
        // 修改：清空静态弱引用
        onFloatingClickListener = null;
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