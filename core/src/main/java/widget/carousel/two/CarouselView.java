package widget.carousel.two;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Created on 2026/6/13.
 *
 * @author 郑少鹏
 * @desc 轮播视图
 * <p>
 * 物理流式非循环轮播
 */
public class CarouselView extends FrameLayout {
    /**
     * 轮播方向常量定义
     * <p>
     * 正向
     * <p>
     * 向右 / 向下滑动 -> 递增
     */
    public static final int DIRECTION_FORWARD = 1;
    /**
     * 轮播方向常量定义
     * <p>
     * 逆向
     * <p>
     * 向左 / 向上滑动 -> 递减
     */
    public static final int DIRECTION_BACKWARD = -1;
    /**
     * 绑定主线程 Looper 的 Handler
     * <p>
     * 用于驱动自动轮播的异步定时任务
     */
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    /**
     * 内部核心承载容器
     * <p>
     * 利用其高效复用机制实现卡片滑动
     */
    private RecyclerView recyclerView;
    /**
     * 外部解耦的业务数据绑定适配器
     */
    private CarouselAdapter<?> carouselAdapter;
    /**
     * 自定义视差特效布局管理器
     * <p>
     * 控制卡片几何尺寸与动态矩阵缩放
     */
    private CarouselLayoutManager carouselLayoutManager;
    /**
     * 外部页面精准居中对齐后的切换监听事件
     */
    private OnPageChangeListener onPageChangeListener;
    /**
     * 居中吸附对齐的核心辅助器
     */
    private LinearSnapHelper linearSnapHelper;
    /**
     * 记录上一次对外分发的精准位置
     * <p>
     * 用于进行去重防御
     * 防止重复回调
     */
    private int lastReportedPosition = RecyclerView.NO_POSITION;
    /**
     * 是否开启自动轮播标志位
     */
    private boolean autoScroll = false;
    /**
     * 自动轮播的驱动方向
     * <p>
     * 默认正向
     */
    private int autoScrollDirection = DIRECTION_FORWARD;
    /**
     * 自动轮播的毫秒时间间隔
     * <p>
     * 默认 3000ms
     */
    private long interval = 3000L;
    /**
     * 基础滚动速度系数
     * <p>
     * 毫秒 / 英寸
     * <p>
     * 值越小原生滚动越快
     */
    private float scrollSpeedMillisecondsPerInch = 150f;
    /**
     * 标记首次布局测绘时
     * <p>
     * 是否已经成功执行了居中 / 目标页位置的初始化定位
     */
    private boolean hasInitializedPosition = false;
    /**
     * 当前视图在几何逻辑上的精准中心点位置索引
     */
    private int currentLogicalPosition = RecyclerView.NO_POSITION;
    /**
     * 缓存异步加载或未测绘完成前
     * <p>
     * 外部调用指定跳转的目标索引位置
     */
    private int pendingInitialPosition = -1;
    /**
     * 弹动控制系数
     * <p>
     * 系统默认 - 1.0f
     * <p>
     * 通过干预 LinearSnapHelper 在松手刹车对齐时的【减速总时间 - Time】来改变视感物理手感
     * <p>
     * 值越小 - 如 0.3f
     * 减速时间被拉长，松手后卡片会像粘了胶水一样肉肉地、非常轻柔温和地吸过去，毫无反弹。
     * <p>
     * 值越大 - 如 2.2f
     * 减速时间被大幅压缩，卡片快速位移并陡然急停，在视觉上产生极强、极脆的撞击回弹吸附感。
     */
    private float scrollSpringinessMultiplier = 1.0f;
    /**
     * 自动轮播异步循环定时任务
     */
    private final Runnable autoTask = new Runnable() {
        @Override
        public void run() {
            // 条件防御
            // 未开启轮播或未装载数据时直接阻断
            if (!autoScroll || (carouselAdapter == null)) {
                return;
            }
            int realCount = carouselAdapter.getRealCount();
            // 单张卡片不执行轮播
            if (realCount <= 1) {
                return;
            }
            // 根据驱动方向
            // 校验边界并执行翻页
            // 触底自动往返调头，彻底解决定时器永久停摆的死局逻辑漏洞。
            if (autoScrollDirection == DIRECTION_FORWARD) {
                if (currentLogicalPosition >= realCount - 1) {
                    // 到达最后页
                    // 自动反转为逆向驱动并执行平滑后退
                    autoScrollDirection = DIRECTION_BACKWARD;
                    scrollPrevious();
                } else {
                    scrollNext();
                }
            } else {
                if (currentLogicalPosition <= 0) {
                    // 到达第一页
                    // 自动反转为正向驱动并执行平滑前进
                    autoScrollDirection = DIRECTION_FORWARD;
                    scrollNext();
                } else {
                    scrollPrevious();
                }
            }
            // 循环轮询投递
            handler.postDelayed(this, interval);
        }
    };

    /**
     * constructor
     *
     * @param context 上下文
     */
    public CarouselView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context      上下文
     * @param attributeSet AttributeSet
     */
    public CarouselView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /**
     * constructor
     *
     * @param context               上下文
     * @param attributeSet          AttributeSet
     * @param defaultStyleAttribute 默认样式属性
     */
    public CarouselView(@NonNull Context context, @Nullable AttributeSet attributeSet, int defaultStyleAttribute) {
        super(context, attributeSet, defaultStyleAttribute);
        init(context, attributeSet);
    }

    /**
     * 初始化
     * <p>
     * 初始化视图层级、核心组件与默认属性解析
     *
     * @param context      上下文
     * @param attributeSet AttributeSet
     */
    private void init(Context context, AttributeSet attributeSet) {
        // 关键配置
        // 允许子视图突破本容器边界绘制（流式卡片层叠或超出视觉的底层基础）
        setClipChildren(false);
        setClipToPadding(false);
        // 动态实例化核心滚动组件
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // 去除滑动到尽头边缘时的系统默认阴影 / 拉伸微效
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        // 再次向下层同步突破裁剪限制
        recyclerView.setClipChildren(false);
        recyclerView.setClipToPadding(false);
        // 实例化自定义布局管理器并注入
        carouselLayoutManager = new CarouselLayoutManager(context);
        recyclerView.setLayoutManager(carouselLayoutManager);
        // 重写 LinearSnapHelper
        // 接管对齐控制
        // 彻底解决高刷新率或手势介入时回弹感被抹平的问题
        linearSnapHelper = new LinearSnapHelper() {
            @Override
            protected LinearSmoothScroller createScroller(@NonNull RecyclerView.LayoutManager layoutManager) {
                // 安全检查
                // 如果布局管理器未实现向量供给接口
                // 无法预判方向
                // 则降级走系统默认逻辑
                if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
                    // 严格类型修复
                    // 强转为 LinearSmoothScroller 确保编译 100% 通过
                    return (LinearSmoothScroller) super.createScroller(layoutManager);
                }
                // 返回完全重写了物理减速曲线的自定义滑动器
                return new LinearSmoothScroller(getContext()) {
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // 根据设备屏幕密度 (DPI) 与自定义配置
                        // 计算出每像素理论消耗的毫秒数
                        return (scrollSpeedMillisecondsPerInch / displayMetrics.densityDpi);
                    }

                    @Override
                    protected int calculateTimeForDeceleration(int dx) {
                        // 计算出原生无干预情况下的默认减速对齐总耗时
                        int baseTime = super.calculateTimeForDeceleration(dx);
                        if (scrollSpringinessMultiplier <= 0f) {
                            return baseTime;
                        }
                        // 核心物理干预
                        // 总时间 = 默认时间 / 弹动系数
                        // 系数越大 -> 时间越短
                        // 对齐动作瞬间完成
                        // 由于惯性骤停
                        // 视觉回弹感异常强烈
                        return (int) (baseTime / scrollSpringinessMultiplier);
                    }

                    @Override
                    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                        if (carouselLayoutManager != null) {
                            // 精准计算当前目标卡片几何中心点距离整个容器正中央的目标偏差像素绝对值
                            int[] snapDistance = calculateDistanceToFinalSnap(carouselLayoutManager, targetView);
                            if (snapDistance != null) {
                                int dx = snapDistance[0];
                                int dy = snapDistance[1];
                                // 带入我们被系数干预后的减速公式
                                // 算出本次校正位移所需要的黄金时间
                                int time = calculateTimeForDeceleration(Math.abs(dx));
                                if (time > 0) {
                                    // 运用减速插值器更新滚动动画参数
                                    // 实施平滑居中吸附
                                    action.update(dx, dy, time, mDecelerateInterpolator);
                                }
                            }
                        }
                    }
                };
            }
        };
        // 将重构后的对齐辅助器锚定到 RecyclerView 上
        linearSnapHelper.attachToRecyclerView(recyclerView);
        addView(recyclerView);
        // 兼容 Android 低版本 (API 31 以下) 的 TypedArray 安全资源解析
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CarouselView);
            try {
                autoScroll = typedArray.getBoolean(R.styleable.CarouselView_carouselAutoScroll, false);
                interval = typedArray.getInteger(R.styleable.CarouselView_carouselScrollInterval, 3000);
                scrollSpeedMillisecondsPerInch = typedArray.getFloat(R.styleable.CarouselView_carouselScrollSpeed, 150f);
                carouselLayoutManager.setEnableScale(typedArray.getBoolean(R.styleable.CarouselView_carouselEnableScale, true));
                carouselLayoutManager.setMinScale(typedArray.getFloat(R.styleable.CarouselView_carouselMinScale, 0.82f));
                carouselLayoutManager.setEnableAlpha(typedArray.getBoolean(R.styleable.CarouselView_carouselEnableAlpha, true));
                carouselLayoutManager.setMinAlpha(typedArray.getFloat(R.styleable.CarouselView_carouselMinAlpha, 0.6f));
                int itemWidth = typedArray.getDimensionPixelSize(R.styleable.CarouselView_carouselItemWidth, 0);
                if (itemWidth > 0) {
                    carouselLayoutManager.setItemWidth(itemWidth);
                }
            } finally {
                typedArray.recycle();
            }
        }
        // 注册阻断式核心滑动双向监听
        registerScrollListener();
        // 初始化卡片非居中点击时的手势捕捉与智能对齐
        initItemClickListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 当容器测绘完毕且明确获知了卡片的物理宽度时
        // 动态计算并填充两侧的内边距
        // 确保卡片永远能够绝对居中
        if ((recyclerView.getWidth() > 0) && (carouselLayoutManager.getItemWidth() > 0)) {
            int padding = (recyclerView.getWidth() - carouselLayoutManager.getItemWidth()) / 2;
            if (recyclerView.getPaddingLeft() != padding) {
                // 动态设定左右 Padding
                // 上下保持 0
                // 配合 clipToPadding = false
                // 使得两侧隐藏的卡片在滑动时可见
                recyclerView.setPadding(padding, 0, padding, 0);
            }
            // 触发位置初始化定位检查
            checkAndInitializePosition();
        }
    }

    /**
     * 校验并驱动执行首次加载、数据变更时的视图首位居中定位
     */
    private void checkAndInitializePosition() {
        // 数据无效、或已经执行过初始化则直接跳过
        if ((carouselAdapter == null) || (carouselAdapter.getRealCount() <= 0) || hasInitializedPosition)
            return;
        if ((carouselLayoutManager.getItemWidth() > 0) && (recyclerView.getWidth() > 0)) {
            // 锁定标志位
            hasInitializedPosition = true;
            int realCount = carouselAdapter.getRealCount();
            // 如果外部未指定初始位置 (pendingInitialPosition = -1)
            // 则默认计算出中位数索引作为默认展示页
            int target = (pendingInitialPosition != -1) ? Math.min(pendingInitialPosition, realCount - 1) : (realCount - 1) / 2;
            target = Math.max(0, target);
            currentLogicalPosition = target;
            // 粗暴地将目标卡片瞬间对齐到无偏移的左侧锚点
            carouselLayoutManager.scrollToPositionWithOffset(target, 0);
            final int finalTarget = target;
            // 投递到消息队列末尾
            // 等待上一行粗暴滑动测绘绘制生效后
            // 执行二次微调绝对居中
            recyclerView.post(() -> {
                View centerView = carouselLayoutManager.findViewByPosition(finalTarget);
                if (centerView != null) {
                    // 利用 snapHelper 计算出微调物理偏差像素距离
                    int[] snapDistance = linearSnapHelper.calculateDistanceToFinalSnap(carouselLayoutManager, centerView);
                    if ((snapDistance != null) && (snapDistance[0] != 0 || snapDistance[1] != 0)) {
                        // 悄无声息地滚动像素偏差值
                        // 达成精准居中
                        recyclerView.scrollBy(snapDistance[0], snapDistance[1]);
                    }
                }
                // 尝试分发一次初始化切换通知
                dispatchPageSelectedIfChanged();
                // 如果开启了自动轮播
                // 部署定时轮询任务
                if (autoScroll) resumeAutoScroll();
            });
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    /**
     * 精准对齐位置发生真实物理跨越时安全分发页面切换通知
     */
    private void dispatchPageSelectedIfChanged() {
        if ((carouselAdapter == null) || (onPageChangeListener == null)) {
            return;
        }
        int realPosition = getCurrentItem();
        // 条件去重防御
        // 只有当位置明确有效
        // 且不同于上一次分发的位置时
        // 才激活回调
        if ((realPosition != RecyclerView.NO_POSITION) && (realPosition != lastReportedPosition)) {
            lastReportedPosition = realPosition;
            // 因 carouselAdapter 是 CarouselAdapter<?>
            // 直接通过位置拿到条目数据
            Object itemData = null;
            if ((realPosition >= 0) && (realPosition < carouselAdapter.getItemCount())) {
                itemData = carouselAdapter.getItemData(realPosition);
            }
            // 抛出位置和条目数据
            onPageChangeListener.onPageScrollEnd(realPosition, itemData);
        }
    }

    /**
     * 携带自定义物理阻尼变换与时间干预的方向性平滑卡片微调滚动
     *
     * @param position 位置
     */
    private void smoothScrollToPositionWithDirection(int position) {
        if ((recyclerView == null) || (carouselLayoutManager == null)) {
            return;
        }
        try {
            // 主动调用跳转的瞬间，立即向外分发滚动开始信号，以便上层安全、快速处理。
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStart();
            }
            // 创建并重载平滑滚动控制类
            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(getContext()) {
                @Contract(pure = true)
                @Override
                protected float calculateSpeedPerPixel(@NonNull DisplayMetrics displayMetrics) {
                    return (scrollSpeedMillisecondsPerInch / displayMetrics.densityDpi);
                }

                @Override
                protected int calculateTimeForDeceleration(int dx) {
                    // 同步注入自定义手感弹动变换时长公式
                    int baseTime = super.calculateTimeForDeceleration(dx);
                    if (scrollSpringinessMultiplier <= 0f) {
                        return baseTime;
                    }
                    return (int) (baseTime / scrollSpringinessMultiplier);
                }

                @Override
                protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                    // 精准锁定目标卡片
                    // 提取偏移量并进行插值平滑对齐
                    int[] snapDistance = linearSnapHelper.calculateDistanceToFinalSnap(carouselLayoutManager, targetView);
                    if (snapDistance != null) {
                        int dx = snapDistance[0];
                        int time = calculateTimeForDeceleration(Math.abs(dx));
                        if (time > 0) {
                            action.update(dx, snapDistance[1], time, mDecelerateInterpolator);
                        }
                    }
                }
            };
            linearSmoothScroller.setTargetPosition(position);
            // 意图先行 -> 解决滑移
            // 在启动物理滑行动画前，立刻锁定最新目标逻辑索引，并提前进行状态回调。
            // 这可让适配器瞬间隐藏旧页面按钮，显示目标新页面按钮，完美规避滑行中途状态穿帮。
            currentLogicalPosition = position;
            dispatchPageSelectedIfChanged();
            // 提交给布局管理器启动滚动动画
            carouselLayoutManager.startSmoothScroll(linearSmoothScroller);
        } catch (Exception e) {
            // 异常兜底：若发生未知测绘冲突，降级采用无动画的瞬间跳转。
            recyclerView.scrollToPosition(position);
            currentLogicalPosition = position;
            // 异常同步适配
            dispatchPageSelectedIfChanged();
        }
    }

    /**
     * 初始化点击非中央两侧侧翼卡片时手势捕捉并令其自动居中滑行的交互
     */
    private void initItemClickListener() {
        // 利用 GestureDetector 优雅识别轻触单击事件
        // 过滤掉长按或复杂拖拽冲突
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                // 通过触点坐标，向下检索当前落在 RecyclerView 里的哪一张卡片 View 上。
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int targetPosition = recyclerView.getChildAdapterPosition(childView);
                    // 提取当前居中位置
                    int currentPosition = (currentLogicalPosition != RecyclerView.NO_POSITION) ? currentLogicalPosition : carouselLayoutManager.getCenterPosition();
                    // 如果用户点击的不是当前正中央的那张卡片，则命令其平滑滑行对齐到中央。
                    if ((targetPosition != RecyclerView.NO_POSITION) && (targetPosition != currentPosition)) {
                        smoothScrollToPositionWithDirection(targetPosition);
                        // 消费此点击手势
                        return true;
                    }
                }
                return false;
            }
        });
        // 将手势探测器注入 RecyclerView 触摸总线
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // 交付手势探测器分析
                gestureDetector.onTouchEvent(e);
                // 绝不拦截触摸下发
                // 确保子卡片内部的点击事件（如卡片内按钮）依然能正常触发
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * 注册滑动联动阻断核心监听
     * <p>
     * 完美隔离解决手动人工干预与自动轮播之间的时序冲突
     */
    private void registerScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // 意图先行 -> 解决滑移
            // 内部临时变量标记：区分当前是否属于用户手指按在屏幕上直接拖拽的行为
            private boolean isDragging = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // 手指按下拖拽
                    isDragging = true;
                    // 核心互斥隔离
                    // 一旦用户手指开始触碰并拖拽卡片，立刻连根拔掉自动轮播计时器，防止滑动打架。
                    pauseAutoScroll();
                    // 手动手势接触并拖拽卡片开始的第一帧，立即触发滚动开始回调。
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageScrollStart();
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 拖拽静止或对齐动作刹车静止
                    isDragging = false;
                    // 当手势松开且滑动完全归于静止（对齐完毕）状态时
                    int center = carouselLayoutManager.getCenterPosition();
                    // 修正校准逻辑索引
                    if (center >= 0) currentLogicalPosition = center;
                    // 重新部署、恢复自动轮播定时器
                    resumeAutoScroll();
                    // 触发页面对齐切换事件分发
                    dispatchPageSelectedIfChanged();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // 意图先行 -> 解决滑移
                // 实时动态同步：在滑动位移发生变化的每一帧中，只有在手动人工拖动状态下，才实时覆盖中心逻辑索引。
                // 若是主动点击平滑滑动跳转，不执行此处的实时计算覆盖。
                // 规避点击跳转滑行到中途的过渡卡片重置并污染已锁定的目标 currentLogicalPosition。
                if (isDragging) {
                    int center = carouselLayoutManager.getCenterPosition();
                    if (center >= 0) {
                        currentLogicalPosition = center;
                    }
                }
            }
        });
    }

    /**
     * 代理并绑定业务逻辑适配器
     * <p>
     * 实现内部 RecyclerView 桥接
     *
     * @param carouselAdapter CarouselAdapter<?>
     */
    public void setCarouselAdapter(@NonNull final CarouselAdapter<?> carouselAdapter) {
        this.carouselAdapter = carouselAdapter;
        // 重置内部状态机标志位
        this.lastReportedPosition = RecyclerView.NO_POSITION;
        this.currentLogicalPosition = RecyclerView.NO_POSITION;
        this.hasInitializedPosition = false;
        // 代理并覆写原生 RecyclerView Adapter 对应接口
        // 匿名外层适配器 + 仅保留最基本几个标准重写
        RecyclerView.Adapter<RecyclerView.ViewHolder> wrapperAdapter = new RecyclerView.Adapter<>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return carouselAdapter.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                carouselAdapter.onBindViewHolder((CarouselAdapter.CarouselViewHolder) holder, position);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
                carouselAdapter.onBindViewHolder((CarouselAdapter.CarouselViewHolder) holder, position, payloads);
            }

            @Override
            public int getItemCount() {
                return carouselAdapter.getItemCount();
            }

            @Override
            public int getItemViewType(int position) {
                return carouselAdapter.getItemViewType(position);
            }
        };
        // 注册适配器数据观察者
        carouselAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged() {
                // 当适配器重新填装新数据源进行全量 onChanged 刷新时，安全重置视图状态标记并重新执行绝对居中对齐。
                hasInitializedPosition = false;
                wrapperAdapter.notifyDataSetChanged();
                // 若此时容器已物理测绘，立即触发定位校验，重塑视图到正确的焦点位置。
                if (recyclerView.getWidth() > 0) {
                    checkAndInitializePosition();
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                wrapperAdapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                wrapperAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                wrapperAdapter.notifyDataSetChanged();
            }
        });
        // 把该打通了信号管道的 wrapperAdapter 塞给系统 RecyclerView
        recyclerView.setAdapter(wrapperAdapter);
        // 如果此时容器已经完成了物理测绘
        // 直接执行位置初始化居中
        if (recyclerView.getWidth() > 0) {
            checkAndInitializePosition();
        }
    }

    /**
     * 动态修改手动 / 自动滑动松手后的对齐速度与视觉弹动激烈程度
     *
     * @param multiplier 调节后肉眼可见物理质感差异
     *                   推荐参数范围 0.3f ~ 2.5f
     *                   设为 0.4f: 对齐动画变慢，松手后像粘了胶水慢悠悠地吸过去，毫无反弹，质感柔和。
     *                   设为 1.0f: 系统原生默认对齐吸附速度
     *                   设为 2.2f: 大幅加快刹车对齐动作，松手后卡片砸向中心点，产生极其爽快的撞击 / 强吸附回弹感。
     */
    public void setScrollSpringiness(float multiplier) {
        if (multiplier <= 0) {
            return;
        }
        this.scrollSpringinessMultiplier = multiplier;
    }

    /**
     * 设置自动轮播驱动方向
     *
     * @param direction {@link #DIRECTION_FORWARD} 或 {@link #DIRECTION_BACKWARD}
     */
    public void setAutoScrollDirection(int direction) {
        if ((direction == DIRECTION_FORWARD) || (direction == DIRECTION_BACKWARD)) {
            this.autoScrollDirection = direction;
            if (autoScroll) {
                resumeAutoScroll();
            }
        }
    }

    /**
     * 激活 / 重置自动轮播计时器
     */
    public void resumeAutoScroll() {
        // 清理移出先前未完成的孤儿任务
        handler.removeCallbacks(autoTask);
        if (autoScroll) {
            handler.postDelayed(autoTask, interval);
        }
    }

    /**
     * 强行阻断并暂停自动轮播计时器
     */
    public void pauseAutoScroll() {
        handler.removeCallbacks(autoTask);
    }

    /**
     * 主动命令视图平滑翻滚并对齐到下一张卡片
     */
    public void scrollNext() {
        if ((carouselAdapter == null) || (currentLogicalPosition >= (carouselAdapter.getRealCount() - 1))) {
            return;
        }
        smoothScrollToPositionWithDirection(currentLogicalPosition + 1);
    }

    /**
     * 主动命令视图平滑翻滚并对齐到上一张卡片
     */
    public void scrollPrevious() {
        if (currentLogicalPosition <= 0) {
            return;
        }
        smoothScrollToPositionWithDirection(currentLogicalPosition - 1);
    }

    /**
     * 命令轮播视图强制跳转到指定的目标页
     *
     * @param position 目标索引
     * @param smooth   true 带物理手感平滑滑行过去
     *                 false 无视任何阻尼瞬间切过去
     */
    public void setCurrentItem(int position, boolean smooth) {
        if (carouselAdapter == null) {
            // 异步防御：如果此时外部数据还没加载好，先缓存位置，等数据就绪适配器绑定后自动跳转。
            this.pendingInitialPosition = position;
            return;
        }
        // 边界安全限位
        int target = Math.max(0, Math.min(position, carouselAdapter.getRealCount() - 1));
        this.pendingInitialPosition = target;
        currentLogicalPosition = target;
        // 如果此时布局还没开始测量，锁定标记位置等待初次测量触发。
        if ((carouselLayoutManager.getItemWidth() <= 0) || (recyclerView.getWidth() <= 0)) {
            this.hasInitializedPosition = false;
            return;
        }
        this.hasInitializedPosition = true;
        if (smooth) {
            // 执行带平滑弹动手感驱使的平滑滚页
            smoothScrollToPositionWithDirection(target);
        } else {
            // 非平滑 (瞬间) 跳转也属滚动状态切换
            // 需立刻向外通知
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStart();
            }
            // 瞬间生硬定位
            carouselLayoutManager.scrollToPositionWithOffset(target, 0);
            // 延时投递以修正物理像素偏差，完成无缝绝对居中。
            recyclerView.post(() -> {
                View centerView = carouselLayoutManager.findViewByPosition(target);
                if (centerView != null) {
                    int[] snapDistance = linearSnapHelper.calculateDistanceToFinalSnap(carouselLayoutManager, centerView);
                    if ((snapDistance != null) && ((snapDistance[0] != 0) || (snapDistance[1] != 0))) {
                        recyclerView.scrollBy(snapDistance[0], snapDistance[1]);
                    }
                }
                dispatchPageSelectedIfChanged();
            });
        }
    }

    /**
     * 获取当前处于正中央展示的卡片逻辑索引位置
     *
     * @return 当前处于正中央展示的卡片逻辑索引位置
     */
    public int getCurrentItem() {
        if (carouselAdapter == null) {
            return RecyclerView.NO_POSITION;
        }
        return (currentLogicalPosition != RecyclerView.NO_POSITION) ? currentLogicalPosition : carouselLayoutManager.getCenterPosition();
    }

    /**
     * 禁用条目刷新动画
     * <p>
     * 添加时长卡死为 0
     * itemAnimator.setAddDuration(0);
     * <p>
     * 移动时长卡死为 0
     * itemAnimator.setMoveDuration(0);
     * <p>
     * 移除时长卡死为 0
     * itemAnimator.setRemoveDuration(0);
     * <p>
     * 刷新时长卡死为 0
     * itemAnimator.setChangeDuration(0);
     */
    public void disableItemChangeAnimator() {
        if (recyclerView != null) {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator instanceof androidx.recyclerview.widget.SimpleItemAnimator) {
                // 直接彻底拔掉动画器
                recyclerView.setItemAnimator(null);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 沿视图树向上追溯
        // 强行把所有父容器的 clipChildren 和 clipToPadding 设为 false
        // 从而彻底解放开发者
        // 不用再去每个外层父布局的 xml 里繁琐地写这两行配置，内部自动完成视觉防裁剪优化。
        ViewParent viewParent = getParent();
        while (viewParent instanceof ViewGroup) {
            ViewGroup parentGroup = (ViewGroup) viewParent;
            parentGroup.setClipChildren(false);
            parentGroup.setClipToPadding(false);
            viewParent = parentGroup.getParent();
        }
        // 视窗绑定后 -> 恢复并启动轮播任务
        resumeAutoScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 视窗销毁时立刻拔掉计时器
        // 阻断内存泄漏防御
        pauseAutoScroll();
        // 清理主线程 Handler 残留消息队列
        // 杜绝内存泄漏
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 页面精准对齐切换后的事件监听回调接口
     */
    public interface OnPageChangeListener {
        /**
         * 页面滑动开始
         * <p>
         * 用于上层组件在第一帧进行交互
         */
        void onPageScrollStart();

        /**
         * 页面滑动结束
         *
         * @param position 位置
         * @param itemData 条目数据
         *                 外围强转
         */
        void onPageScrollEnd(int position, Object itemData);
    }
}