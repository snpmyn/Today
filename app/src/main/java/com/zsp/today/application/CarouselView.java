package com.zsp.today.application;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 2026/6/3.
 *
 * @author 郑少鹏
 * @desc 轮播视图（人工强制纠正点击卡片时的滚动位移与视觉方向）
 */
public class CarouselView extends FrameLayout {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private CarouselAdapter<?> carouselAdapter;
    private CarouselLayoutManager carouselLayoutManager;
    private OnPageChangeListener onPageChangeListener;
    private int lastReportedPosition = RecyclerView.NO_POSITION;
    private int visibleCount = 3;
    private boolean autoScroll = false;
    private long interval = 3000L;
    private float scrollSpeedMillisecondsPerInch = 150f;
    private LinearSnapHelper linearSnapHelper;

    private final Runnable autoTask = new Runnable() {
        @Override
        public void run() {
            if (!autoScroll) {
                return;
            }
            scrollNext();
            handler.postDelayed(this, interval);
        }
    };

    public interface OnPageChangeListener {
        void onPageSelected(int position);
    }

    public CarouselView(@NonNull Context context) {
        this(context, null);
    }

    public CarouselView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        recyclerView = new RecyclerView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setClipToPadding(false);

        carouselLayoutManager = new CarouselLayoutManager(getContext());
        recyclerView.setLayoutManager(carouselLayoutManager);

        linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        addView(recyclerView);

        registerScrollListener();
        initItemClickListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (totalWidth > 0 && visibleCount > 0) {
            int itemWidth = totalWidth / visibleCount;
            carouselLayoutManager.setItemWidth(itemWidth);

            int padding = (totalWidth - itemWidth) / 2;
            if (recyclerView.getPaddingLeft() != padding) {
                recyclerView.setPadding(padding, 0, padding, 0);
                recyclerView.post(() -> {
                    if (carouselAdapter != null && carouselAdapter.getRealCount() > 0) {
                        recyclerView.scrollToPosition(carouselLayoutManager.getCenterPosition());
                    }
                });
            }
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.onPageChangeListener = listener;
    }

    private void dispatchPageSelectedIfChanged() {
        if (carouselAdapter == null || onPageChangeListener == null) {
            return;
        }
        int realPosition = getCurrentItem();
        if (realPosition != RecyclerView.NO_POSITION && realPosition != lastReportedPosition) {
            lastReportedPosition = realPosition;
            onPageChangeListener.onPageSelected(realPosition);
        }
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeedMillisecondsPerInch = speed;
    }

    public void setVisibleCount(int count) {
        if (count <= 0 || this.visibleCount == count) {
            return;
        }
        this.visibleCount = count;
        requestLayout();
    }

    public int getVisibleCount() {
        return visibleCount;
    }

    /**
     * 【核心修正】：通过人工计算出的物理偏移量来进行绝对控向的滚动
     *
     * @param position 目标位置
     * @param offsetDx 精准物理中心距（自带绝对真实方向）
     */
    private void smoothScrollToPositionWithDirection(int position, final int offsetDx) {
        if (recyclerView == null || carouselLayoutManager == null) {
            return;
        }
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return scrollSpeedMillisecondsPerInch / displayMetrics.densityDpi;
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_START;
            }

            @Override
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                // 不再让系统或辅助器算方向，直接采用点击时测量出来的、方向绝对正确的物理相对位移
                return offsetDx;
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        carouselLayoutManager.startSmoothScroll(linearSmoothScroller);
    }

    private void initItemClickListener() {
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int targetPosition = recyclerView.getChildAdapterPosition(childView);
                    int currentPosition = carouselLayoutManager.getCenterPosition();

                    if (targetPosition != RecyclerView.NO_POSITION && targetPosition != currentPosition) {
                        // 1. 获取控件本身的物理中心点
                        int rvCenterX = recyclerView.getWidth() / 2;
                        // 2. 获取被点击子条目的实时物理中心点
                        int viewCenterX = (childView.getLeft() + childView.getRight()) / 2;

                        // 3. 物理像素差值计算：
                        // 当点击左侧：viewCenterX < rvCenterX，offsetDx 为负数，强制 RecyclerView 内容向右移动（左侧向右滚居中）。
                        // 当点击右侧：viewCenterX > rvCenterX，offsetDx 为正数，强制 RecyclerView 内容向左移动（右侧向左滚居中）。
                        int offsetDx = viewCenterX - rvCenterX;

                        // 执行精确控向平滑滚动
                        smoothScrollToPositionWithDirection(targetPosition, offsetDx);
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    private void registerScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    pauseAutoScroll();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeAutoScroll();
                    dispatchPageSelectedIfChanged();
                }
            }
        });
    }

    public CarouselAdapter<?> getCarouselAdapter() {
        return carouselAdapter;
    }

    public void setCarouselAdapter(final CarouselAdapter<?> adapter) {
        this.carouselAdapter = adapter;
        this.lastReportedPosition = RecyclerView.NO_POSITION;

        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
                ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                if (carouselLayoutManager.getItemWidth() > 0) {
                    lp.width = carouselLayoutManager.getItemWidth();
                }
                holder.itemView.setLayoutParams(lp);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((CarouselAdapter) carouselAdapter).onBindViewHolder((CarouselAdapter.CarouselViewHolder) holder, position);
                ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                if (lp != null && carouselLayoutManager.getItemWidth() > 0 && lp.width != carouselLayoutManager.getItemWidth()) {
                    lp.width = carouselLayoutManager.getItemWidth();
                    holder.itemView.setLayoutParams(lp);
                }
            }

            @Override
            public int getItemCount() {
                return adapter.getItemCount();
            }

            @Override
            public int getItemViewType(int position) {
                return adapter.getItemViewType(position);
            }
        });

        if (adapter.getRealCount() > 0) {
            recyclerView.scrollToPosition(adapter.getStartPosition());
            recyclerView.post(this::dispatchPageSelectedIfChanged);
        }
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean enable) {
        autoScroll = enable;
        if (enable) {
            resumeAutoScroll();
        } else {
            pauseAutoScroll();
        }
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void resumeAutoScroll() {
        handler.removeCallbacks(autoTask);
        if (autoScroll) {
            handler.postDelayed(autoTask, interval);
        }
    }

    public void pauseAutoScroll() {
        handler.removeCallbacks(autoTask);
    }

    public void scrollNext() {
        int current = carouselLayoutManager.getCenterPosition();
        if (current == RecyclerView.NO_POSITION) {
            return;
        }
        // 自动轮播或下一页调用时，使用默认测算间距，方向默认向左滚动
        int itemWidth = carouselLayoutManager.getItemWidth() > 0 ?
                carouselLayoutManager.getItemWidth() : (recyclerView.getWidth() / visibleCount);
        smoothScrollToPositionWithDirection(current + 1, itemWidth);
    }

    public void scrollPrevious() {
        int current = carouselLayoutManager.getCenterPosition();
        if (current == RecyclerView.NO_POSITION) {
            return;
        }
        // 自动轮播或上一页调用时，使用负间距，方向默认向右滚动
        int itemWidth = carouselLayoutManager.getItemWidth() > 0 ?
                carouselLayoutManager.getItemWidth() : (recyclerView.getWidth() / visibleCount);
        smoothScrollToPositionWithDirection(current - 1, -itemWidth);
    }

    public void setCurrentItem(int position, boolean smooth) {
        if (carouselAdapter == null) {
            return;
        }
        int target = carouselAdapter.getStartPosition() + position;
        if (smooth) {
            // 被代码直接指定跳转到特定位置时，通过视图中实际的相对距离来决定方向
            int current = carouselLayoutManager.getCenterPosition();
            int itemWidth = carouselLayoutManager.getItemWidth() > 0 ?
                    carouselLayoutManager.getItemWidth() : (recyclerView.getWidth() / visibleCount);
            int estimatedDx = (target - current) * itemWidth;
            smoothScrollToPositionWithDirection(target, estimatedDx);
        } else {
            recyclerView.scrollToPosition(target);
        }
    }

    public int getCurrentItem() {
        if (carouselAdapter == null) {
            return RecyclerView.NO_POSITION;
        }
        int center = carouselLayoutManager.getCenterPosition();
        return carouselAdapter.getRealPosition(center);
    }

    public void setMinScale(float scale) {
        carouselLayoutManager.setMinScale(scale);
    }

    public void setMinAlpha(float alpha) {
        carouselLayoutManager.setMinAlpha(alpha);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public CarouselLayoutManager getCarouselLayoutManager() {
        return carouselLayoutManager;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resumeAutoScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pauseAutoScroll();
    }
}