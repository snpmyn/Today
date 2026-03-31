package widget.nest;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.NonNull;

/**
 * @decs: 嵌套滑动冲突帮助者
 * @author: 郑少鹏
 * @date: 2026/3/28 19:42
 * @version: v 1.0
 * <p>
 * 适用场景
 * - WebView + RecyclerView (轮播)
 * - RecyclerView 嵌套 RecyclerView
 * - ViewPager / ViewPager2 嵌套
 * <p>
 * 核心思想
 * 1. DOWN 不拦截，让父容器有机会参与。
 * 2. MOVE 根据滑动方向决定归属
 * 3. UP / CANCEL 释放控制权
 */
public class NestScrollConflictHelper {
    /**
     * 按下坐标
     * <p>
     * 用于判断滑动方向
     */
    private float downX, downY;
    /**
     * 滑动阈值
     * <p>
     * 防止轻微抖动误判
     */
    private final int touchSlop;
    /**
     * 是否已经决定方向
     * <p>
     * 防止抖动
     */
    private boolean isDirectionDecided = false;

    /**
     * constructor
     *
     * @param view 视图
     */
    public NestScrollConflictHelper(@NonNull View view) {
        this.touchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
    }

    /**
     * 处理触摸冲突
     *
     * @param view       当前触摸视图
     * @param event      触摸事件
     * @param isVertical 是否纵向滚动
     */
    public void handleTouch(@NonNull View view, MotionEvent event, boolean isVertical) {
        // 这里必须用 ViewParent
        // 不能强转成 View
        // 否则可能崩溃
        ViewParent viewParent = view.getParent();
        if (null == viewParent) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下位置
                downX = event.getX();
                downY = event.getY();
                // 重置方向锁定
                isDirectionDecided = false;
                // 不要在 DOWN 阶段拦截
                // 让父容器有机会判断是否拦截
                viewParent.requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算滑动距离
                float dx = (event.getX() - downX);
                float dy = (event.getY() - downY);
                // 防抖
                // 滑动距离过小时不处理
                // 避免误判
                if (!isDirectionDecided && (Math.abs(dx) < touchSlop) && (Math.abs(dy) < touchSlop)) {
                    break;
                }
                // 只在第一次判断方向
                if (!isDirectionDecided) {
                    // 判断滑动方向
                    boolean isHorizontalMove = (Math.abs(dx) > Math.abs(dy));
                    if (isVertical) {
                        // 横向滑动 → 父容器处理
                        // 纵向滑动 → 当前 View 处理
                        viewParent.requestDisallowInterceptTouchEvent(!isHorizontalMove);
                    } else {
                        // 横向滑动 → 当前 View 处理
                        // 纵向滑动 → 父容器处理
                        viewParent.requestDisallowInterceptTouchEvent(isHorizontalMove);
                    }
                    // 锁定方向
                    isDirectionDecided = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 手指抬起 / 事件取消 → 恢复默认
                viewParent.requestDisallowInterceptTouchEvent(false);
                isDirectionDecided = false;
                break;
        }
    }
}