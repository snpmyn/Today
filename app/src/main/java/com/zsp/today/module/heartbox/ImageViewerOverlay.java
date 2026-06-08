package com.zsp.today.module.heartbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.zsp.today.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

/**
 * Created on 2026/6/5.
 *
 * @author 郑少鹏
 * @desc 图片查看浮层
 * <p>
 * 完美兼顾差分阻尼与边界回弹机制
 * <p>
 * 1. 差分移动流 (Differential Motion Fluency)
 * 通过在 ACTION_MOVE 中按帧跟踪相对增量 dx / dy
 * 使渲染帧率能够完美贴合高刷屏 (90Hz / 120Hz / 144Hz)
 * 彻底拒绝传统的绝对坐标映射带来的卡顿感
 * 2. 空间锚点解耦 (Space Anchor Decoupling)
 * 引入绝对物理落点锚点 downTouch 与动态帧增量动点 lastTouch 双轨制
 * 彻底解决传统单动点在差分追踪时因高频重置导致的全局总跨度断路
 * 进而引发的点击 / 回弹逻辑判定死锁问题
 * 3. 渐进式对数级弹性阻尼 (Asymptotic Logarithmic Damping)
 * 当图像边缘越过视口边界后
 * 越过量越大
 * 当前帧允许移动的步长增量就按对数级衰减
 * 公式表现为 dx_damper = dx * (1.0 - clip(overX / limit))
 * 提供高级且细腻的手势回馈
 */
public class ImageViewerOverlay extends FrameLayout {
    /**
     * 加载策略
     * <p>
     * 提供给上层的架构分流设计
     */
    public enum LoadStrategy {
        /**
         * 内置原生线程池 + HttpURLConnection
         */
        NATIVE,
        /**
         * 主流 Glide 框架
         */
        GLIDE
    }

    /**
     * 加载策略
     * <p>
     * 默认
     * 内置原生线程池 + HttpURLConnection
     */
    private LoadStrategy loadStrategy = LoadStrategy.NATIVE;
    /**
     * 线程池
     * <p>
     * 线程池与异步控制
     * 专门用于原生 NATIVE 策略
     * 防止并发雪崩与内存泄漏
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    /**
     * Future<?>
     */
    private Future<?> loadFuture;
    /**
     * 线程消息调度器
     */
    private final Handler handler = new Handler(Looper.getMainLooper());
    /**
     * 极限缩小界限
     * <p>
     * 动力学与控制参数 - 支持弹性回弹
     */
    private float minScale = 0.5f;
    /**
     * 极限放大界限
     * <p>
     * 动力学与控制参数 - 支持超高清大图探索
     */
    private float maxScale = 6.0f;
    /**
     * 双击默认放大的缩放因子
     * <p>
     * 动力学与控制参数 - 基于自适应基础缩放 baseScale
     */
    private float doubleTapTargetScale = 2.5f;
    /**
     * 边缘越界弹性上限像素值
     * <p>
     * 动力学与控制参数 - 提升视觉延展性
     */
    private float overscrollLimit = 120f;
    /**
     * 回弹阻尼动画时长
     * <p>
     * 动力学与控制参数 - 兼具高级减速感
     */
    private int springDuration = 260;
    /**
     * 双击与矩阵突变过度动画时长
     */
    private int zoomAnimDuration = 300;
    /**
     * 单指下滑退出触发阈值
     * <p>
     * 占屏幕总高度的比例 - 默认 0.35f
     * 即手势向下滑动超过屏幕 35% 高度即触发物理销毁
     */
    private float exitDragThreshold = 0.35f;
    /**
     * 处于静止 / 无交互状态
     * <p>
     * 触摸手势状态机模式
     */
    private static final int TOUCH_NONE = 0;
    /**
     * 单指处于拖拽位移状态
     * <p>
     * 触摸手势状态机模式 - 大图浏览模式
     */
    private static final int TOUCH_DRAG = 1;
    /**
     * 双指处于矩阵缩放剪切交互中
     * <p>
     * 触摸手势状态机模式
     */
    private static final int TOUCH_PINCH = 2;
    /**
     * 单指满足下滑退出条件判定
     * <p>
     * 触摸手势状态机模式 - 进入渐变缩放退出轨道
     */
    private static final int TOUCH_EXIT_DRAG = 3;
    /**
     * 核心通用子视图容器
     */
    private View bgView;
    private AccessibleImageView ivImage;
    private ProgressBar pbLoading;
    private TextView tvError;

    /**
     * 实时反映在 ImageView 上的主运行矩阵
     * <p>
     * 仿射变换变换矩阵
     * 二维图形渲染核心
     */
    private final Matrix matrix = new Matrix();
    /**
     * 手势触发 ACTION_DOWN 时刻保存的状态快照矩阵
     * <p>
     * 仿射变换变换矩阵
     * 二维图形渲染核心
     */
    private final Matrix savedMatrix = new Matrix();
    /**
     * 当前图像累计旋转角度 [0, 360)
     * <p>
     * 图像几何状态辅助量
     */
    private float currentRotation = 0f;
    /**
     * 宽高比判定是否为全景长图
     * <p>
     * 图像几何状态辅助量 - 判定标准为比值 >= 2.2f
     */
    private boolean isLongImage = false;
    /**
     * 使图片恰好等比居中填满视口的“基准缩放比”
     * <p>
     * 图像几何状态辅助量
     */
    private float baseScale = 1.0f;
    /**
     * 物理触摸追踪辅助变量
     */
    private int touchMode = TOUCH_NONE;
    /**
     * 锁定单次手势最原始的物理触点，用于全局滑动区间与判定
     * <p>
     * 物理触摸追踪辅助变量 - 用于全局滑动区间与判定
     */
    private final PointF downTouch = new PointF();
    /**
     * 专职负责计算相连两帧之间的差分微元 dx, dy
     * <p>
     * 物理触摸追踪辅助变量 - 动态动点
     */
    private final PointF lastTouch = new PointF();
    /**
     * 双指缩放时的几何几何中点（矩阵缩放中心点锚点）
     * <p>
     * 物理触摸追踪辅助变量 - 矩阵缩放中心点锚点
     */
    private final PointF midPoint = new PointF();
    /**
     * 双指多点触控时的初始物理间距绝对像素值
     * <p>
     * 物理触摸追踪辅助变量
     */
    private float initDist = 1f;
    /**
     * Android 系统级判定“滑动行为”的物理最小平移死区阈值
     * <p>
     * 物理触摸追踪辅助变量
     */
    private final float touchSlop;
    /**
     * 高精双击事件检测计数器
     */
    private long lastTapTime = 0;
    /**
     * 有效双击时间差窗口限制
     * <p>
     * 毫秒
     */
    private static final long DOUBLE_TAP_MS = 300;
    /**
     * 关闭监听
     * <p>
     * 声明周期控制解耦回调
     */
    private OnCloseListener onCloseListener;

    /**
     * 关闭监听
     */
    public interface OnCloseListener {
        /**
         * 关闭
         * <p>
         * 当浮层完全退出销毁、移出父布局视图树时触发的外部生命周期回调
         */
        void onClose();
    }

    /**
     * constructor
     *
     * @param context 上下文
     */
    public ImageViewerOverlay(Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context      上下文
     * @param attributeSet AttributeSet
     */
    public ImageViewerOverlay(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        // 初始化系统级的滑动死区值
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        inflate(context);
    }

    /**
     * 加载
     * <p>
     * 视图结构树组装与基础按钮交互挂载
     *
     * @param context 上下文
     */
    private void inflate(Context context) {
        LayoutInflater.from(context).inflate(R.layout.overlay_image_viewer, this, true);
        bgView = findViewById(R.id.bg_view);
        ivImage = findViewById(R.id.iv_image);
        pbLoading = findViewById(R.id.pb_loading);
        tvError = findViewById(R.id.tv_error);
        // 防御性容错
        // 若未定义独立背景
        // 则将当前根容器视为遮罩背景
        if (bgView == null) {
            bgView = this;
        }
        // 绑定旋转与关闭控制链
        findViewById(R.id.iv_rotate_left).setOnClickListener(v -> rotateByMatrix(-90));
        findViewById(R.id.iv_rotate_right).setOnClickListener(v -> rotateByMatrix(90));
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        // 劫持 ImageView 的原生 Touch 体系
        // 转入高精度自研手势物理引擎中
        ivImage.setOnTouchListener(this::handleImageTouch);
    }

    // ======================================================
    //  面向业务层的公开配置 API (Getter / Setter 链式拓展)
    // ======================================================

    public void setLoadStrategy(LoadStrategy strategy) {
        this.loadStrategy = strategy;
    }

    public LoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    public void setExitDragThreshold(float threshold) {
        this.exitDragThreshold = threshold;
    }

    public float getExitDragThreshold() {
        return exitDragThreshold;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public float getDoubleTapTargetScale() {
        return doubleTapTargetScale;
    }

    public void setDoubleTapTargetScale(float doubleTapTargetScale) {
        this.doubleTapTargetScale = doubleTapTargetScale;
    }

    public float getOverscrollLimit() {
        return overscrollLimit;
    }

    public void setOverscrollLimit(float overscrollLimit) {
        this.overscrollLimit = overscrollLimit;
    }

    public int getSpringDuration() {
        return springDuration;
    }

    public void setSpringDuration(int springDuration) {
        this.springDuration = springDuration;
    }

    public int getZoomAnimDuration() {
        return zoomAnimDuration;
    }

    public void setZoomAnimDuration(int zoomAnimDuration) {
        this.zoomAnimDuration = zoomAnimDuration;
    }

    // ======================================================
    //  生命周期控制与多维度分流加载核心架构 API
    // ======================================================

    /**
     * 显示
     * <p>
     * 将图片查看器挂载到当前宿主 Activity / Fragment 根节点布局
     * 并自适应拉起异步流
     *
     * @param rootView 当前页面的根容器
     *                 如 DecorView 或 ContentParent (ViewGroup)
     * @param imageUrl 图片的全路径网络或本地资源 String
     */
    public void show(ViewGroup rootView, String imageUrl) {
        // 保证重入幂等性
        // 全量清理历史残留状态
        resetState();
        if (getParent() == null) {
            rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        // 激进地取消机制
        // 若上一次网络请求任务仍在运行 -> 无条件掐断线 -> 避免抢占宽带与内存
        if (loadFuture != null && !loadFuture.isDone()) {
            loadFuture.cancel(true);
        }
        // 核心加载分流执行链
        if (loadStrategy == LoadStrategy.GLIDE) {
            executeGlideLoad(imageUrl);
        } else {
            executeNativeLoad(imageUrl);
        }
    }

    /**
     * 原生线程池驱动链
     * <p>
     * 零第三方依赖 + 高度底层自闭环
     *
     * @param imageUrl 图片链接
     */
    private void executeNativeLoad(String imageUrl) {
        WeakReference<ImageViewerOverlay> weakOverlay = new WeakReference<>(this);
        loadFuture = EXECUTOR_SERVICE.submit(() -> loadUrlInBackground(imageUrl, weakOverlay));
    }

    /**
     * 外部生态 Glide 驱动链
     * <p>
     * 通过反射规避编译锁 + 解耦宿主工程
     *
     * @param imageUrl 图片链接
     */
    private void executeGlideLoad(String imageUrl) {
        Glide.with(getContext()).asBitmap().load(imageUrl).into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                onImageLoaded(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                ivImage.setImageBitmap(null);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                pbLoading.setVisibility(View.GONE);
                tvError.setText("图片加载失败");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 关闭
     * <p>
     * 安全优雅地销毁退出控制
     * 完美防范跨线程引起的空指针及内存高位常驻问题
     */
    public void dismiss() {
        if (loadStrategy == LoadStrategy.GLIDE) {
            com.bumptech.glide.Glide.with(getContext()).clear(ivImage);
        } else {
            if (loadFuture != null) {
                loadFuture.cancel(true);
            }
        }
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (onCloseListener != null) {
            onCloseListener.onClose();
        }
    }

    /**
     * 设置关闭监听
     *
     * @param onCloseListener 关闭监听
     */
    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    /**
     * 设置缩放比例
     * <p>
     * 支持动画越界回弹平衡
     *
     * @param targetScale 目标缩放比例
     */
    public void setScale(float targetScale) {
        targetScale = Math.max(minScale, Math.min(maxScale, targetScale));
        float curScale = getMatrixScale(matrix);
        if (curScale <= 0f) {
            return;
        }
        float factor = targetScale / curScale;
        PointF center = getImageCenter();
        matrix.postScale(factor, factor, center.x, center.y);
        ivImage.setImageMatrix(matrix);
        springBack();
    }

    public float getCurrentScale() {
        return getMatrixScale(matrix);
    }

    // ======================================================
    //  旋转逻辑（由硬件仿射变换实现的 2.5D 平滑旋转变换层）
    // ======================================================

    private void rotateByMatrix(float degrees) {
        PointF imgCenter = getImageCenter();
        final float[] lastAngle = {0f};
        ValueAnimator animator = ValueAnimator.ofFloat(0f, degrees);
        animator.setDuration(280);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(anim -> {
            float current = (float) anim.getAnimatedValue();
            float delta = current - lastAngle[0]; // 提取差分旋转角度，避免单帧进行全量复合运算
            lastAngle[0] = current;
            matrix.postRotate(delta, imgCenter.x, imgCenter.y);
            ivImage.setImageMatrix(matrix);
        });
        animator.start();
        currentRotation = (currentRotation + degrees) % 360;
        // 旋转动画结束后
        // 重新评估图像在新轴向边界下的边缘状态
        // 执行越界回弹对齐
        ivImage.postDelayed(this::springBack, 290);
    }

    @NonNull
    private PointF getImageCenter() {
        RectF rect = getImageRect(matrix);
        return new PointF(rect.centerX(), rect.centerY());
    }

    // ======================================================
    //  触摸手势高精手势交互引擎 (解耦物理空间点位冲突的核心改良版)
    // ======================================================

    private boolean handleImageTouch(View v, MotionEvent event) {
        if (ivImage.getDrawable() == null) {
            return false;
        }
        boolean handled = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                long now = System.currentTimeMillis();
                // 阻断条件
                // 若满足高能双击间隔窗口
                // 则迅速切断普通拖拽分流
                // 转入双击缩放流程
                if (now - lastTapTime < DOUBLE_TAP_MS) {
                    touchMode = TOUCH_NONE;
                    handleDoubleTapZoom(event.getX(), event.getY());
                    lastTapTime = 0;
                    return true;
                }
                lastTapTime = now;
                // 物理落点数据锁存
                savedMatrix.set(matrix);
                // 全局参考系锚点
                // 代表单次拖动事件最纯净的零点位置 + 绝不动摇
                downTouch.set(event.getX(), event.getY());
                // 差分微元参考点
                // 跟随 ACTION_MOVE 逐帧更新 + 不参与宏观判定
                lastTouch.set(event.getX(), event.getY());
                touchMode = TOUCH_DRAG;
                handled = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 进入多点多指触控
                // 自适应转轨到双指剪切缩放手势状态机中
                if (event.getPointerCount() >= 2) {
                    initDist = spacing(event);
                    if (initDist > 5f) {
                        savedMatrix.set(matrix);
                        // 计算双指在当前屏幕空间的几何核心坐标
                        // 作为缩放定点
                        midPoint(midPoint, event);
                        touchMode = TOUCH_PINCH;
                        handled = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 1. 经典物理微分提取
                // 通过当前触点与上一帧历史旧触点的绝对像素差
                // 计算极短时间跨度下的流式移动步长增量
                float dx = event.getX() - lastTouch.x;
                float dy = event.getY() - lastTouch.y;
                if (touchMode == TOUCH_DRAG) {
                    float curScale = getMatrixScale(matrix);
                    // 2. 解耦空间转换
                    // 采用完全独立的全局参考系（event.getY() - downTouch.y）
                    // 来判定宏观下滑退出行为，
                    // 彻底避免由于 lastTouch 被逐帧高频刷新导致的判定断路故障
                    if (curScale <= baseScale * 1.02f && (event.getY() - downTouch.y) > 0 && Math.abs(event.getY() - downTouch.y) > Math.abs(event.getX() - downTouch.x) * 1.5f) {
                        touchMode = TOUCH_EXIT_DRAG;
                    } else {
                        // 3. 图像物理边界碰撞试探
                        // 通过一个临时的矩阵分量测算如果不施加阻尼
                        // 这一帧位移后将会出现的新边界
                        Matrix testMatrix = new Matrix(matrix);
                        testMatrix.postTranslate(dx, dy);
                        RectF currentRect = getImageRect(testMatrix);
                        // 4. 计算当前拟突变边缘处于真实视口外的超越量
                        float overX = calcOverscroll(currentRect, true);
                        float overY = calcOverscroll(currentRect, false);
                        // 5. 对数级渐进式阻尼驱动模型
                        // 当图片越过边界时
                        // 施加对数衰减系数
                        // 使其在无限越界中逼近阻尼上限值
                        // 消除卡死与生硬感
                        if (overX != 0) {
                            dx = dx * (1.0f - Math.min(0.85f, Math.abs(overX) / (overscrollLimit * 2f)));
                        }
                        if (overY != 0) {
                            dy = dy * (1.0f - Math.min(0.85f, Math.abs(overY) / (overscrollLimit * 2f)));
                        }
                        // 6. 最终平滑步长微元合入主运行仿射变换矩阵
                        matrix.postTranslate(dx, dy);
                        ivImage.setImageMatrix(matrix);
                    }
                    // 7. 高频同步差分动点
                    // 将 lastTouch 刷新为本帧的坐标点
                    // 从而在下一渲染帧到来时作为全新的起点进行微分
                    lastTouch.set(event.getX(), event.getY());
                    handled = true;
                }
                if (touchMode == TOUCH_EXIT_DRAG) {
                    // 下滑退出轨道
                    // 依赖最纯净的 downTouch 宏观坐标进行线性映射
                    // 表现稳定、顺滑
                    float totalDeltaY = event.getY() - downTouch.y;
                    float translationY = Math.max(0, totalDeltaY);
                    // 动力学缩放映射
                    // 随着单指拉下距离的增大
                    // 图片进行非线性等比微缩
                    // 赋予更强的空间离屏感
                    float exitScale = 1.0f - (translationY / getHeight()) * 0.5f;
                    // 黄金分割保护
                    // 限制极限缩小比例 + 维持UI高阶感
                    exitScale = Math.max(0.618f, exitScale);
                    matrix.set(savedMatrix);
                    matrix.postScale(exitScale, exitScale, downTouch.x, downTouch.y);
                    matrix.postTranslate(event.getX() - downTouch.x, translationY);
                    ivImage.setImageMatrix(matrix);
                    // 遮罩背景半透明连续映射
                    // 根据位移线性将背景不透明度向0靠拢
                    float alpha = 1.0f - (translationY / (getHeight() * 0.4f));
                    bgView.setAlpha(Math.max(0f, Math.min(1.0f, alpha)));
                    handled = true;
                } else if (touchMode == TOUCH_PINCH && event.getPointerCount() >= 2) {
                    // 双指捏合状态
                    // 进行平滑的比例变换计算
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        float scale = newDist / initDist;
                        float curScale = getMatrixScale(savedMatrix);
                        float next = curScale * scale;
                        // 预加边界硬阈值卡位限制
                        // 防止双指无限制无限放大或缩小导致视口完全丢失
                        if (next < minScale) scale = minScale / curScale;
                        if (next > maxScale) scale = maxScale / curScale;
                        matrix.set(savedMatrix);
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        ivImage.setImageMatrix(matrix);
                    }
                    handled = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (touchMode == TOUCH_EXIT_DRAG) {
                    // 释放手势时判断是否满足下滑销毁的临界触发比例
                    float moveY = event.getY() - downTouch.y;
                    if (moveY > getHeight() * exitDragThreshold) {
                        dismiss();
                    } else {
                        // 判定未达标
                        // 通过平滑回弹动画拉回原始状态
                        // 背景渐变恢复不透明度
                        animateBgAlpha(bgView.getAlpha(), 1.0f);
                        animateMatrix(matrix, savedMatrix);
                    }
                    handled = true;
                } else if (touchMode == TOUCH_DRAG) {
                    // 关键修复逻辑
                    // 通过全局隔离锚点 downTouch 来全面还原手指在长周期操作中的宏观总跨度
                    // 根除了因差分计算引发的高频重置点击判定失效问题
                    float totalX = event.getX() - downTouch.x;
                    float totalY = event.getY() - downTouch.y;
                    if (Math.abs(totalX) < touchSlop && Math.abs(totalY) < touchSlop) {
                        // 纯正点击
                        // 激活底层无障碍及常规单击体系
                        v.performClick();
                    } else {
                        // 拖拽越界放手
                        // 执行具有质感的临界边界回弹算法
                        springBack();
                    }
                    handled = true;
                } else if (touchMode != TOUCH_NONE) {
                    springBack();
                    handled = true;
                }
                touchMode = TOUCH_NONE;
                break;
            default:
                break;
        }
        return handled;
    }

    /**
     * 高级双击缩放互逆状态切换引擎
     * <p>
     * 无需复杂状态记录
     * 直接基于当前渲染结果进行双射拓扑
     *
     * @param clickX 点击 X 轴坐标
     * @param clickY 点击 Y 轴坐标
     */
    private void handleDoubleTapZoom(float clickX, float clickY) {
        if (ivImage.getDrawable() == null) {
            return;
        }
        float currentScale = getMatrixScale(matrix);
        Matrix targetMatrix = new Matrix();
        // 判定分流
        // 如果图片当前已经是放大状态 + 则双击回归居中等比自适应状态
        // 反之则进行深度缩放
        if (currentScale > baseScale * 1.05f) {
            int bmpW = ivImage.getDrawable().getIntrinsicWidth();
            int bmpH = ivImage.getDrawable().getIntrinsicHeight();
            if (isLongImage) {
                targetMatrix.postScale(baseScale, baseScale);
                float dx = (getWidth() - bmpW * baseScale) / 2f;
                targetMatrix.postTranslate(dx, 0f);
            } else {
                float dx = (getWidth() - bmpW * baseScale) / 2f;
                float dy = (getHeight() - bmpH * baseScale) / 2f;
                targetMatrix.postScale(baseScale, baseScale);
                targetMatrix.postTranslate(dx, dy);
            }
            // 重置旋转角度状态
            currentRotation = 0f;
        } else {
            // 进行局部坐标系聚焦缩放
            float targetScale = baseScale * doubleTapTargetScale;
            float factor = targetScale / currentScale;
            targetMatrix.set(matrix);
            targetMatrix.postScale(factor, factor, clickX, clickY);
            // 越界安全前置对齐
            // 防止双击图片边缘区域导致缩放出来的局部区域闪烁、黑边问题
            RectF targetRect = getImageRect(targetMatrix);
            float overX = calcOverscroll(targetRect, true);
            float overY = calcOverscroll(targetRect, false);
            targetMatrix.postTranslate(-overX, -overY);
        }
        // 执行非突变的高阶矩阵数学插值平滑过渡动画
        animateMatrix(matrix, targetMatrix);
    }

    // ======================================================
    //  过渡物理动画引擎层（基于 Decelerate 插值器的平滑矩阵过渡）
    // ======================================================

    /**
     * 矩阵动画
     * <p>
     * 利用 9 元素仿射变换数组插值
     * 实现两个 Matrix 之间的线性演进平滑过渡
     *
     * @param startMatrix  开始矩阵
     * @param targetMatrix 目标矩阵
     */
    private void animateMatrix(@NonNull Matrix startMatrix, @NonNull Matrix targetMatrix) {
        float[] startVals = new float[9];
        float[] targetVals = new float[9];
        startMatrix.getValues(startVals);
        targetMatrix.getValues(targetVals);
        float[] currentVals = new float[9];
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(zoomAnimDuration);
        // 二阶指数减速插值
        // 呈现优异的丝滑视觉阻尼感
        animator.setInterpolator(new DecelerateInterpolator(1.5f));
        animator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            for (int i = 0; i < 9; i++) {
                currentVals[i] = startVals[i] + (targetVals[i] - startVals[i]) * fraction;
            }
            matrix.setValues(currentVals);
            ivImage.setImageMatrix(matrix);
        });
        animator.start();
    }

    private void animateBgAlpha(float startAlpha, float targetAlpha) {
        ValueAnimator anim = ValueAnimator.ofFloat(startAlpha, targetAlpha);
        anim.setDuration(200);
        anim.addUpdateListener(a -> bgView.setAlpha((float) a.getAnimatedValue()));
        anim.start();
    }

    // ======================================================
    //  矩阵几何解算与临界弹性边界回弹算法层
    // ======================================================

    /**
     * 获取图片资源在经过当前主仿射矩阵变换后映射在屏幕平面上的最新真实物理矩形边界
     *
     * @param matrix 矩阵
     * @return 图片资源在经过当前主仿射矩阵变换后映射在屏幕平面上的最新真实物理矩形边界
     */
    @NonNull
    private RectF getImageRect(Matrix matrix) {
        if (ivImage.getDrawable() == null) {
            return new RectF();
        }
        RectF rect = new RectF(0, 0, ivImage.getDrawable().getIntrinsicWidth(), ivImage.getDrawable().getIntrinsicHeight());
        // 将原始图形尺寸映射给变换后的物理矩阵拓扑空间
        matrix.mapRect(rect);
        return rect;
    }

    /**
     * 高阶物理视口边界碰撞解算核心
     * <p>
     * 精准计算水平 / 垂直方向上的溢出像素位移
     *
     * @param imageRect  当前变换后的图像映射区域真实矩形
     * @param horizontal true 代表水平 X 轴解算
     *                   false 代表垂直 Y 轴解算
     * @return 越过物理视口绝对边缘的像素偏差量
     * 正值代表左 / 上越界
     * 负值代表右 / 下越界
     * 0 代表仍在视口内完美容纳
     */
    private float calcOverscroll(RectF imageRect, boolean horizontal) {
        if (horizontal) {
            float imgW = imageRect.width();
            float viewW = getWidth();
            // 分流处理一
            // 当变换后的图像总宽仍小于等于屏幕视口时
            // 居中对齐作为回弹归宿
            if (imgW <= viewW) {
                return imageRect.left - (viewW - imgW) / 2f;
            } else {
                // 分流处理二
                // 当图像已经大过视口
                // 进入边缘贴合滑动判定约束
                if (imageRect.left > 0) {
                    // 图像左边缘进入视口内部
                    // 判定左侧大量留白空虚
                    // 抛出溢出量
                    return imageRect.left;
                }
                if (imageRect.right < viewW) {
                    // 图像右边缘缩进视口内部
                    // 抛出负溢出量
                    return imageRect.right - viewW;
                }
            }
        } else {
            float imgH = imageRect.height();
            float viewH = getHeight();
            // 分流处理三
            // 垂直方向上的解算
            if (imgH <= viewH) {
                // 细节容错
                // 针对被判定为全景超长图且处于基准自适应状态的情况
                // 顶部允许贴边而非居中
                // 提供极佳的长图阅读体验
                if (isLongImage && getMatrixScale(matrix) <= baseScale * 1.05f) {
                    return imageRect.top;
                }
                return imageRect.top - (viewH - imgH) / 2f;
            } else {
                if (imageRect.top > 0) return imageRect.top;
                if (imageRect.bottom < viewH) return imageRect.bottom - viewH;
            }
        }
        // 稳定状态
        // 无任何边缘越界
        return 0f;
    }

    /**
     * 边界弹性归位回弹核心算法
     * <p>
     * 当检测到边缘溢出量不为 0 时
     * 构建反向线性修正动画平滑消解溢出量
     */
    private void springBack() {
        RectF imgRect = getImageRect(matrix);
        float overX = calcOverscroll(imgRect, true);
        float overY = calcOverscroll(imgRect, false);
        // 判定收敛
        // 若没有任何方向的越界 -> 终止回弹动画 -> 节约主线程渲染帧渲染周期
        if (overX == 0f && overY == 0f) {
            return;
        }
        // 构建反向物理平移分量
        final float fixDx = -overX;
        final float fixDy = -overY;
        final Matrix startMatrix = new Matrix(matrix);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(springDuration);
        animator.setInterpolator(new DecelerateInterpolator(2.0f)); // 物理增强版减速插值，提供更有质感的归位力量反馈
        animator.addUpdateListener(anim -> {
            float fraction = (float) anim.getAnimatedValue();
            matrix.set(startMatrix);
            matrix.postTranslate(fixDx * fraction, fixDy * fraction); // 连续平滑消解像素溢出量
            ivImage.setImageMatrix(matrix);
        });
        animator.start();
    }

    // ======================================================
    //  尺寸居中等比自适应数学解算核心层
    // ======================================================

    /**
     * 根据图片的真实物理点阵像素宽高
     * <p>
     * 结合当前硬件设备的视口物理像素尺寸
     * 计算最初地居中等比对齐矩阵
     *
     * @param bitmapWith   Bitmap 宽
     * @param bitmapHeight Bitmap 高
     */
    private void centerAndFit(int bitmapWith, int bitmapHeight) {
        if (bitmapWith <= 0 || bitmapHeight <= 0 || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        float viewW = getWidth();
        float viewH = getHeight();
        float scaleW = viewW / bitmapWith;
        float scaleH = viewH / bitmapHeight;
        // 宽高比判定是否为全景纵向超长图
        isLongImage = ((float) bitmapHeight / bitmapWith) >= 2.2f;
        matrix.reset();
        if (isLongImage) {
            // 超长长图策略
            // 让图片的宽度恰好等比充满屏幕
            // 顶部贴齐物理视口顶边缘开始浏览
            baseScale = scaleW;
            matrix.postScale(baseScale, baseScale);
            float dx = (viewW - bitmapWith * baseScale) / 2f;
            // y 偏移为 0 固定靠顶
            matrix.postTranslate(dx, 0f);
        } else {
            // 常规图片策略
            // 等比收缩
            // 将整张图完美等比容纳在屏幕正中央
            // 并在两侧或上下留出对称的均等黑边
            baseScale = Math.min(scaleW, scaleH);
            matrix.postScale(baseScale, baseScale);
            float dx = (viewW - bitmapWith * baseScale) / 2f;
            float dy = (viewH - bitmapHeight * baseScale) / 2f;
            matrix.postTranslate(dx, dy);
        }
        ivImage.setImageMatrix(matrix);
        // 初始化累加角度参数
        currentRotation = 0f;
    }

    /**
     * 重置状态
     * <p>
     * 严谨的内部状态恢复清理机
     * 规避重用复用导致的上一次图形缩放历史状态污染
     */
    private void resetState() {
        matrix.reset();
        currentRotation = 0f;
        isLongImage = false;
        baseScale = 1.0f;
        touchMode = TOUCH_NONE;
        bgView.setAlpha(1.0f);
        ivImage.setImageBitmap(null);
        pbLoading.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    /**
     * 图片被加载
     * <p>
     * 数据装载完成核心回调网关
     * 打通跨线程加载与主线程布局就绪时间的步调同步
     *
     * @param bitmap Bitmap
     */
    void onImageLoaded(Bitmap bitmap) {
        pbLoading.setVisibility(View.GONE);
        if (bitmap == null) {
            tvError.setText("图片加载失败");
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        ivImage.setImageBitmap(bitmap);
        // 核心步调同步
        // 确保 ImageView 的真实物理 Measure、Layout 流程已结束
        // 获得真实的 getWidth() 像素值后再拉起矩阵对齐
        // 彻底阻断因零宽高引发的画面挤压、彻底消失的顽疾
        ivImage.post(() -> {
            if (getWidth() > 0 && getHeight() > 0) {
                centerAndFit(bitmap.getWidth(), bitmap.getHeight());
            } else {
                ivImage.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int l, int t, int r, int b, int oldL, int oldT, int oldR, int oldB) {
                        ivImage.removeOnLayoutChangeListener(this);
                        centerAndFit(bitmap.getWidth(), bitmap.getHeight());
                    }
                });
            }
        });
    }

    // ======================================================
    //  高精几何仿射及多点矩阵基础运算工具函数（降维处理）
    // ======================================================

    /**
     * 计算间距
     * <p>
     * 计算双指多点事件中第 0 指与第 1 指之间当前在空间坐标系下的欧几里得绝对直线像素距离
     *
     * @param motionEvent MotionEvent
     * @return 双指多点事件中第 0 指与第 1 指之间当前在空间坐标系下的欧几里得绝对直线像素距离
     */
    private static float spacing(@NonNull MotionEvent motionEvent) {
        float dx = motionEvent.getX(0) - motionEvent.getX(1);
        float dy = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算多点手势中两根手指所在空间线段的绝对几何中点坐标
     *
     * @param pointF      PointF
     * @param motionEvent MotionEvent
     */
    private static void midPoint(@NonNull PointF pointF, @NonNull MotionEvent motionEvent) {
        pointF.set((motionEvent.getX(0) + motionEvent.getX(1)) / 2f, (motionEvent.getY(0) + motionEvent.getY(1)) / 2f);
    }

    /**
     * 获取矩阵缩放比例
     * <p>
     * 逆向解算矩阵矩阵缩放系数
     * 从 Matrix 的 9 元素线性变换数组中提取当前真实的等比综合缩放模长因子
     *
     * @param matrix 矩阵
     * @return 矩阵矩阵缩放系数
     */
    private static float getMatrixScale(@NonNull Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        // 基于勾股定理
        // 综合解算 X / Y 轴切变与主对角缩放轴的欧氏模长
        // 确保旋转 90 / 180 / 270 度后缩放比数据依然绝对准确
        return (float) Math.sqrt(v[Matrix.MSCALE_X] * v[Matrix.MSCALE_X] + v[Matrix.MSKEW_Y] * v[Matrix.MSKEW_Y]);
    }

    // ======================================================
    //  原生后台多线程网络数据下载子线程任务（底层网络安全合规落地方案）
    // ======================================================

    private static void loadUrlInBackground(String urlString, WeakReference<ImageViewerOverlay> weakReference) {
        Bitmap bitmap;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 10 秒连接超时保障
            httpURLConnection.setConnectTimeout(10_000);
            // 15 秒低网速环境读取容错
            httpURLConnection.setReadTimeout(15_000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            // 每一个高耗时检查点前置中断判定
            // 如果外部由于提前退出而销毁了 Future
            // 则在此刻无条件终止向后运行
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            inputStream = httpURLConnection.getInputStream();
            // 构建内存位图
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            bitmap = null;
        } finally {
            // 兜底流资源回收
            // 保障底层 C++ 层的 Native 堆内存不会出现句柄泄漏
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (Exception exception) {
                Timber.e(exception);
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        final Bitmap resultBitmap = bitmap;
        ImageViewerOverlay overlay = weakReference.get();
        if (overlay != null) {
            // 将位图资产数据和 UI 状态变更切回 UI 线程主循环队列中执行
            overlay.handler.post(() -> {
                if (overlay.isAttachedToWindow()) {
                    overlay.onImageLoaded(resultBitmap);
                }
            });
        }
    }

    // ======================================================
    //  无障碍体系优化重写（通过重写核心交互单元，全面打通系统 TalkBack 读屏支持）
    // ======================================================

    public static class AccessibleImageView extends androidx.appcompat.widget.AppCompatImageView {
        public AccessibleImageView(Context context) {
            super(context);
        }

        public AccessibleImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AccessibleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        /**
         * 显式重写并暴露此接口
         * <p>
         * 确保手势引擎中调起的 performClick() 能够同步向下穿透分发
         * 使得视障人群在使用 TalkBack 双击时能够完美唤醒 Android Accessibility 无障碍虚拟焦点和点击反馈逻辑
         *
         * @return 手势引擎中调起的 performClick() 能够同步向下穿透分否
         */
        @Override
        public boolean performClick() {
            return super.performClick();
        }
    }
}