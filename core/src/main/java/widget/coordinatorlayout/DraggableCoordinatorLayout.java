package widget.coordinatorlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.customview.widget.ViewDragHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/7/17.
 *
 * @author 郑少鹏
 * @desc DraggableCoordinatorLayout
 */
public class DraggableCoordinatorLayout extends CoordinatorLayout {
    private final ViewDragHelper viewDragHelper;
    private final List<View> draggableChildren = new ArrayList<>();
    private ViewDragListener viewDragListener;
    private boolean draggable = true;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public DraggableCoordinatorLayout(Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     */
    public DraggableCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NotNull View view, int i) {
                return (view.getVisibility() == VISIBLE) && viewIsDraggableChild(view) && draggable;
            }

            @Override
            public void onViewCaptured(@NonNull View view, int i) {
                if (null != viewDragListener) {
                    viewDragListener.onViewCaptured(view, i);
                }
            }

            @Override
            public void onViewReleased(@NonNull View view, float v, float v1) {
                if (null != viewDragListener) {
                    viewDragListener.onViewReleased(view, v, v1);
                }
            }

            @Override
            public int getViewHorizontalDragRange(@NotNull View view) {
                return view.getWidth();
            }

            @Override
            public int getViewVerticalDragRange(@NotNull View view) {
                return view.getHeight();
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View view, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View view, int top, int dy) {
                return top;
            }
        };
        viewDragHelper = ViewDragHelper.create(this, dragCallback);
    }

    public void addDraggableChild(@NotNull View child) {
        if (child.getParent() != this) {
            throw new IllegalArgumentException();
        }
        draggableChildren.add(child);
    }

    public void removeDraggableChild(@NotNull View child) {
        if (child.getParent() != this) {
            throw new IllegalArgumentException();
        }
        draggableChildren.remove(child);
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        viewDragHelper.processTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private boolean viewIsDraggableChild(View view) {
        return draggableChildren.isEmpty() || draggableChildren.contains(view);
    }

    public void setViewDragListener(ViewDragListener viewDragListener) {
        this.viewDragListener = viewDragListener;
    }

    /**
     * A listener to use when a child view is being dragged.
     */
    public interface ViewDragListener {
        /**
         * 视图已捕获
         *
         * @param view View
         * @param i    int
         */
        void onViewCaptured(@NonNull View view, int i);

        /**
         * 视图已释放
         *
         * @param view View
         * @param v    float
         * @param v1   float
         */
        void onViewReleased(@NonNull View view, float v, float v1);
    }
}

