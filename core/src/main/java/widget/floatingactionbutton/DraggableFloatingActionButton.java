package widget.floatingactionbutton;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import timber.log.Timber;
import util.statusbar.StatusBarUtils;

/**
 * @decs: 可拖动浮动操作按钮
 * @author: 郑少鹏
 * @date: 2025/10/4 16:26
 * @version: v 1.0
 */
public class DraggableFloatingActionButton extends FloatingActionButton {
    private float dX, dY;
    private float downX, downY;
    private int lastAction;
    /**
     * 左右边距
     */
    private float edgeMarginX = 20;
    /**
     * 上下边距
     */
    private float edgeMarginY = 20;
    /**
     * 点击判定最大移动距离
     */
    private static final float CLICK_THRESHOLD = 10.0F;
    /**
     * 点击放大倍数
     */
    private static final float CLICK_SCALE = 1.2F;
    /**
     * 点击动画时长
     */
    private static final long CLICK_ANIM_DURATION = 100;

    public enum EdgeMode {
        /**
         * 不吸附
         */
        NONE,
        /**
         * 吸附左右边缘
         */
        LEFT_RIGHT,
        /**
         * 四角吸附
         */
        FOUR_CORNER
    }

    private EdgeMode edgeMode = EdgeMode.FOUR_CORNER;

    public DraggableFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public DraggableFloatingActionButton(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public DraggableFloatingActionButton(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        // 设置前景水波纹效果
        try {
            TypedValue typedValue = new TypedValue();
            boolean found = getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true);
            if (found && (typedValue.resourceId != 0)) {
                setForeground(ContextCompat.getDrawable(getContext(), typedValue.resourceId));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        setOnTouchListener((view, event) -> {
            View parent = (View) view.getParent();
            int statusBarHeight = StatusBarUtils.getStatusBarHeight(getContext());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = (event.getRawX() - view.getX());
                    dY = (event.getRawY() - view.getY());
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    // 点击动画反馈
                    view.animate().scaleX(CLICK_SCALE).scaleY(CLICK_SCALE).setDuration(CLICK_ANIM_DURATION).start();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float newX = (event.getRawX() - dX);
                    float newY = (event.getRawY() - dY);
                    // 限制在父布局范围 + 边距 + 状态栏
                    newX = Math.max(edgeMarginX, Math.min(newX, parent.getWidth() - view.getWidth() - edgeMarginX));
                    newY = Math.max(edgeMarginY + statusBarHeight, Math.min(newY, parent.getHeight() - view.getHeight() - edgeMarginY));
                    view.setX(newX);
                    view.setY(newY);
                    lastAction = MotionEvent.ACTION_MOVE;
                    return true;
                case MotionEvent.ACTION_UP:
                    // 抬起恢复缩放
                    view.animate().scaleX(1.0F).scaleY(1.0F).setDuration(CLICK_ANIM_DURATION).start();
                    float dx = (event.getRawX() - downX);
                    float dy = (event.getRawY() - downY);
                    // 点击判定
                    if (Math.sqrt(dx * dx + dy * dy) < CLICK_THRESHOLD) {
                        view.performClick();
                        return false;
                    }
                    // 拖动吸附动画
                    if (lastAction == MotionEvent.ACTION_MOVE) {
                        float targetX = view.getX();
                        float targetY = view.getY();
                        switch (edgeMode) {
                            case LEFT_RIGHT:
                                targetX = (view.getX() + view.getWidth() / 2.0F < parent.getWidth() / 2.0F) ? edgeMarginX : (parent.getWidth() - view.getWidth() - edgeMarginX);
                                targetY = Math.max(edgeMarginY + statusBarHeight, Math.min(view.getY(), parent.getHeight() - view.getHeight() - edgeMarginY));
                                break;
                            case FOUR_CORNER:
                                targetX = (view.getX() + view.getWidth() / 2.0F < parent.getWidth() / 2.0F) ? edgeMarginX : (parent.getWidth() - view.getWidth() - edgeMarginX);
                                targetY = (view.getY() + view.getHeight() / 2.0F < parent.getHeight() / 2.0F) ? (edgeMarginY + statusBarHeight) : (parent.getHeight() - view.getHeight() - edgeMarginY);
                                break;
                            case NONE:
                                break;
                        }
                        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "x", view.getX(), targetX);
                        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "y", view.getY(), targetY);
                        objectAnimatorX.setDuration(300);
                        objectAnimatorY.setDuration(300);
                        objectAnimatorX.setInterpolator(new DecelerateInterpolator());
                        objectAnimatorY.setInterpolator(new DecelerateInterpolator());
                        objectAnimatorX.start();
                        objectAnimatorY.start();
                    }
                    return true;
                default:
                    return false;
            }
        });
    }

    public void setEdgeMode(EdgeMode mode) {
        this.edgeMode = mode;
    }

    public void setEdgeMarginX(float marginX) {
        this.edgeMarginX = marginX;
    }

    public void setEdgeMarginY(float marginY) {
        this.edgeMarginY = marginY;
    }
}