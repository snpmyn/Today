package widget.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.zsp.core.R;

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
 * 采用仿射变换矩阵（Matrix）实现的图片预览核心交互组件
 * 完美融合了高频点击防变斜机制、多轮动画抖动对冲算法以及多点触摸阻尼
 */
public class ImageViewerOverlay extends FrameLayout {
    /**
     * 加载策略
     * <p>
     * 提供给上层的架构分流设计
     */
    public enum LoadStrategy {
        NATIVE, GLIDE
    }

    /**
     * 核心资产网络加载策略
     */
    private LoadStrategy loadStrategy = LoadStrategy.NATIVE;
    /**
     * 线程池句柄
     * <p>
     * 固定容量为 4 的并行线程池
     * 用于原生网络流加载
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    /**
     * 异步任务持有句柄
     * <p>
     * 用于在组件销毁或重置时
     * 强行中断未完成的异步网络流
     */
    private Future<?> loadFuture;
    /**
     * 主线程调度器
     * <p>
     * 绑定主线程 Looper
     * 用于 safe 分发异步加载成功的 Bitmap 资产
     */
    private final Handler handler = new Handler(Looper.getMainLooper());
    /**
     * 最小缩放阈值
     */
    private float minScale = 0.5f;
    /**
     * 最大缩放阈值
     */
    private float maxScale = 6.0f;
    /**
     * 双击放大的目标比例倍数
     */
    private float doubleTapTargetScale = 2.5f;
    /**
     * 最大拖拽越界像素
     * <p>
     * 在单指平移时
     * 允许图片超出视窗边界的最大安全物理阻尼距离
     */
    private float overscrollLimit = 120f;
    /**
     * 回弹动画耗时
     * <p>
     * 释放越界拖拽或缩放后
     * 弹性复原动画的持续时间（毫秒）
     */
    private int springDuration = 260;
    /**
     * 按钮缩放动画耗时
     */
    private int zoomAnimDuration = 300;
    /**
     * 下拉退出临界阈值
     * <p>
     * 当单指处于下拉退出状态时
     * 位移超过视窗总高度的比例即触发销毁 (0.0f - 1.0f)
     */
    private float exitDragThreshold = 0.35f;
    /**
     * 空闲
     * <p>
     * 触摸状态
     */
    private static final int TOUCH_NONE = 0;
    /**
     * 单指拖拽平移
     * <p>
     * 触摸状态
     */
    private static final int TOUCH_DRAG = 1;
    /**
     * 双指多点缩放
     * <p>
     * 触摸状态
     */
    private static final int TOUCH_PINCH = 2;
    /**
     * 单指下拉退出
     * <p>
     * 触摸状态
     */
    private static final int TOUCH_EXIT_DRAG = 3;
    /**
     * 全屏容器根布局
     */
    private View imageViewerOverlayFl;
    /**
     * 自定义高级变换图像视窗
     */
    private AccessibleImageView imageViewerOverlayView;
    /**
     * 全局核心变换矩阵
     * <p>
     * 承载当前视图渲染的最终位置、缩放比例及旋转状态
     */
    private final Matrix matrix = new Matrix();
    /**
     * 矩阵状态快照缓存
     * <p>
     * 手势初次按下 (ACTION_DOWN / ACTION_POINTER_DOWN) 时捕获的干净矩阵
     * 作为差分计算的绝对基准
     */
    private final Matrix savedMatrix = new Matrix();
    /**
     * 当前视图的绝对旋转角度值
     * <p>
     * 仅在 0f、90f、180f、270f 间干净流转
     * 不累加动画中间态的浮点数误差
     */
    private float currentRotation = 0f;
    /**
     * 长图鉴别标识
     * <p>
     * 当图片的宽高比超过 2.2 时激活
     * 激活后图片初始化展示将顶部对齐
     * 且允许单轴自适应
     */
    private boolean isLongImage = false;
    /**
     * 全屏自适应基准缩放比
     * <p>
     * 根据图片宽高与宿主视窗宽高适配后的初始缩放系数
     * 其余高级缩放均以此值为 1.0f 基准进行演变
     */
    private float baseScale = 1.0f;
    /**
     * 当前手势交互所处的引擎模式
     */
    private int touchMode = TOUCH_NONE;
    /**
     * 手势落点绝对坐标
     */
    private final PointF downTouch = new PointF();
    /**
     * 上一次触摸触发的局部坐标点
     */
    private final PointF lastTouch = new PointF();
    /**
     * 双指中心坐标点
     */
    private final PointF midPoint = new PointF();
    /**
     * 双指初次落点时的物理跨度物理距离
     */
    private float initDist = 1f;
    /**
     * 系统级触发拖拽动作的滑行死区阈值
     */
    private final float touchSlop;
    /**
     * 上一次单击触发的时间戳
     */
    private long lastTapTime = 0;
    /**
     * 双击判定死区时间间隔上限
     */
    private static final long DOUBLE_TAP_MS = 300;
    /**
     * 销毁事件外置回调监听器
     */
    private OnCloseListener onCloseListener;
    /**
     * 旋转动画专用调度控制柄
     * <p>
     * 用于全局跟踪并管控正在运行的自动旋转变换
     * 连续快速点击时通过强制打断此句柄
     * 消灭两套引擎重叠导致的残影与抖动
     */
    private ValueAnimator rotationAnimator;
    /**
     * 骨架层级加载反馈加载条
     */
    private ProgressBar imageViewerOverlayPb;
    /**
     * 骨架层级错误状态文本提示器
     */
    private TextView imageViewerOverlayTv;

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
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        inflate(context);
    }

    /**
     * 初始化视图并绑定核心控制按钮交互事件
     *
     * @param context 上下文
     */
    private void inflate(Context context) {
        LayoutInflater.from(context).inflate(R.layout.image_viewer_overlay, this, true);
        imageViewerOverlayFl = findViewById(R.id.imageViewerOverlayFl);
        imageViewerOverlayView = findViewById(R.id.imageViewerOverlayView);
        imageViewerOverlayPb = findViewById(R.id.imageViewerOverlayPb);
        imageViewerOverlayTv = findViewById(R.id.imageViewerOverlayTv);
        if (imageViewerOverlayFl == null) {
            imageViewerOverlayFl = this;
        }
        findViewById(R.id.imageViewerOverlayMbClose).setOnClickListener(v -> dismiss());
        findViewById(R.id.imageViewerOverlayMbShrink).setOnClickListener(v -> zoomOutByButton(0.5f));
        findViewById(R.id.imageViewerOverlayMbTurnLeft).setOnClickListener(v -> rotateByMatrix(-90));
        findViewById(R.id.imageViewerOverlayMbTurnRight).setOnClickListener(v -> rotateByMatrix(90));
        findViewById(R.id.imageViewerOverlayMbEnlarge).setOnClickListener(v -> zoomInByButton(0.5f));
        imageViewerOverlayView.setOnTouchListener(this::handleImageTouch);
    }

    public void zoomInByButton(float stepFactor) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return;
        }
        float currentScale = getMatrixScale(matrix);
        float targetScale = Math.min(maxScale, currentScale + (baseScale * stepFactor));
        executeSmoothZoom(targetScale);
    }

    public void zoomOutByButton(float stepFactor) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return;
        }
        float currentScale = getMatrixScale(matrix);
        float targetScale = Math.max(minScale, currentScale - (baseScale * stepFactor));
        executeSmoothZoom(targetScale);
    }

    /**
     * 触发围绕中心点的平滑矩阵缩放
     * <p>
     * 计算缩放形变后
     * 预估图片边界变化情况
     * 如发现缩放导致内容脱离视窗边界
     * 将自动计算平移矩阵将其纠正拉回
     *
     * @param targetScale 绝对目标缩放比系数
     */
    private void executeSmoothZoom(float targetScale) {
        float currentScale = getMatrixScale(matrix);
        if (currentScale <= 0) {
            return;
        }
        // 计算差分比
        float factor = (targetScale / currentScale);
        PointF center = getImageCenter();
        // 建立沙盒矩阵预演变化后的几何包围盒
        Matrix targetMatrix = new Matrix(matrix);
        targetMatrix.postScale(factor, factor, center.x, center.y);
        // 检测由于缩放造成的局部越界
        // 并对沙盒矩阵施加瞬时修正平移
        RectF targetRect = getImageRect(targetMatrix);
        float overX = calcOverscroll(targetRect, true);
        float overY = calcOverscroll(targetRect, false);
        targetMatrix.postTranslate(-overX, -overY);
        // 交付矩阵插值动画器无缝过渡
        animateMatrix(matrix, targetMatrix);
    }

    public void setLoadStrategy(LoadStrategy strategy) {
        this.loadStrategy = strategy;
    }

    public void setExitDragThreshold(float threshold) {
        this.exitDragThreshold = threshold;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public void setDoubleTapTargetScale(float doubleTapTargetScale) {
        this.doubleTapTargetScale = doubleTapTargetScale;
    }

    public void setOverscrollLimit(float overscrollLimit) {
        this.overscrollLimit = overscrollLimit;
    }

    public void setSpringDuration(int springDuration) {
        this.springDuration = springDuration;
    }

    public void setZoomAnimDuration(int zoomAnimDuration) {
        this.zoomAnimDuration = zoomAnimDuration;
    }

    /**
     * 装载并拉起大图浮层展示窗口
     *
     * @param rootView 根视图
     * @param imageUrl 图片链接
     */
    public void show(ViewGroup rootView, String imageUrl) {
        resetState();
        if (getParent() == null) {
            rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if ((null != loadFuture) && !loadFuture.isDone()) {
            loadFuture.cancel(true);
        }
        if (loadStrategy == LoadStrategy.GLIDE) {
            executeGlideLoad(imageUrl);
        } else {
            executeNativeLoad(imageUrl);
        }
    }

    private void executeNativeLoad(String imageUrl) {
        WeakReference<ImageViewerOverlay> weakOverlay = new WeakReference<>(this);
        loadFuture = EXECUTOR_SERVICE.submit(() -> loadUrlInBackground(imageUrl, weakOverlay));
    }

    private void executeGlideLoad(String imageUrl) {
        Glide.with(getContext()).asBitmap().load(imageUrl).into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                onImageLoaded(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageViewerOverlayView.setImageBitmap(null);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                imageViewerOverlayPb.setVisibility(View.GONE);
                imageViewerOverlayTv.setText("图片加载失败");
                imageViewerOverlayTv.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 强行优雅中断并离架销毁当前图片组件
     */
    public void dismiss() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        if (loadStrategy == LoadStrategy.GLIDE) {
            com.bumptech.glide.Glide.with(getContext()).clear(imageViewerOverlayView);
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

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public interface OnCloseListener {
        void onClose();
    }

    /**
     * 设置缩放
     *
     * @param targetScale 目标缩放
     */
    public void setScale(float targetScale) {
        targetScale = Math.max(minScale, Math.min(maxScale, targetScale));
        float curScale = getMatrixScale(matrix);
        if (curScale <= 0f) {
            return;
        }
        float factor = (targetScale / curScale);
        PointF center = getImageCenter();
        matrix.postScale(factor, factor, center.x, center.y);
        imageViewerOverlayView.setImageMatrix(matrix);
        springBack();
    }

    /**
     * 获取当前缩放
     *
     * @return 当前缩放
     */
    public float getCurrentScale() {
        return getMatrixScale(matrix);
    }

    /**
     * 核心旋转控制引擎（防变斜、防抖动）
     * <p>
     * 针对高频快速连续点击进行架构优化
     * 1. 取消正在运行的旧动画，清理 Handler 延迟队列，杜绝两套动画互相踩踏产生的剧烈闪烁抖动。
     * 2. 锁定此时最纯净的矩阵 `startMatrix` 与中心点，拒绝动画演进中产生的中点漂移误差。
     * 3. 核心修复：一阶无形变叠加算法。
     * 在每一帧刷新时，先全量恢复成无污染的 `startMatrix`，再做绝对增量旋转。
     * 从而彻底消灭了多次连续 postRotate 运算带来的 Matrix 切变分量精度累积（Skew 误差），图片永不变斜。
     *
     * @param degrees 相对当前的旋转步进角度值
     *                例如 90f 或 -90f
     */
    private void rotateByMatrix(float degrees) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return;
        }
        // 防抖动
        // 拦截未完成的动画实体
        // 避免并行的两个矩阵刷新流冲突
        if ((rotationAnimator != null) && rotationAnimator.isRunning()) {
            rotationAnimator.cancel();
        }
        // 清理由于旧框架 postDelayed 留下的潜在回弹归位任务死锁
        handler.removeCallbacksAndMessages(null);
        // 防变斜
        // 锁定本次动画周期的绝对纯净基准快照
        // 避免基于“脏矩阵”继续做叠乘
        final Matrix startMatrix = new Matrix(matrix);
        // 瞬间锁定几何包围盒中心
        // 防动画运动中因边界回弹干扰导致中点计算位置跑偏
        final PointF imgCenter = getImageCenter();
        // 维护绝对角度状态机
        // 确保在 0, 90, 180, 270 度范围内精准闭环
        currentRotation = (currentRotation + degrees) % 360;
        if (currentRotation < 0) {
            currentRotation += 360;
        }
        // 构建非滚雪球式一阶矩阵过渡动画
        rotationAnimator = ValueAnimator.ofFloat(0f, degrees);
        rotationAnimator.setDuration(280);
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        rotationAnimator.addUpdateListener(anim -> {
            float curDelta = (float) anim.getAnimatedValue();
            // 防变斜核心
            // 擦除上一帧可能带有微小浮点误差的旧数据
            // 全量还原为 startMatrix 快照
            matrix.set(startMatrix);
            // 施加纯粹的一阶旋转
            // 彻底杜绝切变（Skew_X / Skew_Y）产生的累积倾斜形变
            matrix.postRotate(curDelta, imgCenter.x, imgCenter.y);
            imageViewerOverlayView.setImageMatrix(matrix);
        });
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 自动旋转动画安全着陆后
                // 再稳健启动边界回弹机制
                springBack();
            }
        });
        rotationAnimator.start();
    }

    @NonNull
    private PointF getImageCenter() {
        RectF rect = getImageRect(matrix);
        return new PointF(rect.centerX(), rect.centerY());
    }

    /**
     * 多点触控与手势交互调度内核
     *
     * @param v     View
     * @param event MotionEvent
     * @return 是否消费
     */
    private boolean handleImageTouch(View v, MotionEvent event) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return false;
        }
        // 当自动旋转动画正在运转时
        // 强行劫持触摸反馈
        // 保护矩阵状态机步调一致
        if ((rotationAnimator != null) && rotationAnimator.isRunning()) {
            return true;
        }
        boolean handled = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                long now = System.currentTimeMillis();
                // 判定双击
                if (now - lastTapTime < DOUBLE_TAP_MS) {
                    touchMode = TOUCH_NONE;
                    handleDoubleTapZoom(event.getX(), event.getY());
                    lastTapTime = 0;
                    return true;
                }
                lastTapTime = now;
                savedMatrix.set(matrix);
                downTouch.set(event.getX(), event.getY());
                lastTouch.set(event.getX(), event.getY());
                touchMode = TOUCH_DRAG;
                handled = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 判定双指切入缩放模式
                if (event.getPointerCount() >= 2) {
                    initDist = spacing(event);
                    if (initDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(midPoint, event);
                        touchMode = TOUCH_PINCH;
                        handled = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastTouch.x;
                float dy = event.getY() - lastTouch.y;
                // 核心状态分支一
                // 处理单指平移 / 单指下拉退出判定
                if (touchMode == TOUCH_DRAG) {
                    float curScale = getMatrixScale(matrix);
                    // 满足未放大、纵向手势向下、且手势夹角偏向垂直时
                    // 切入下拉退出渲染分支
                    if (curScale <= baseScale * 1.02f && (event.getY() - downTouch.y) > 0 && Math.abs(event.getY() - downTouch.y) > Math.abs(event.getX() - downTouch.x) * 1.5f) {
                        touchMode = TOUCH_EXIT_DRAG;
                    } else {
                        // 正常的单指平移
                        // 建立物理包围盒阻尼机制
                        Matrix testMatrix = new Matrix(matrix);
                        testMatrix.postTranslate(dx, dy);
                        RectF currentRect = getImageRect(testMatrix);
                        float overX = calcOverscroll(currentRect, true);
                        float overY = calcOverscroll(currentRect, false);
                        // 越界距离越深
                        // 反向抗性越大
                        // 产生橡皮筋粘滞效果
                        if (overX != 0) {
                            dx = dx * (1.0f - Math.min(0.85f, Math.abs(overX) / (overscrollLimit * 2f)));
                        }
                        if (overY != 0) {
                            dy = dy * (1.0f - Math.min(0.85f, Math.abs(overY) / (overscrollLimit * 2f)));
                        }
                        matrix.postTranslate(dx, dy);
                        imageViewerOverlayView.setImageMatrix(matrix);
                    }
                    lastTouch.set(event.getX(), event.getY());
                    handled = true;
                }
                // 核心状态分支二
                // 单指下拉退出执行中
                // 执行同步视窗等比例缩放变小及背景淡化
                if (touchMode == TOUCH_EXIT_DRAG) {
                    float totalDeltaY = event.getY() - downTouch.y;
                    float translationY = Math.max(0, totalDeltaY);
                    float exitScale = 1.0f - (translationY / getHeight()) * 0.5f;
                    // 锁定最小下拉缩放边界
                    exitScale = Math.max(0.618f, exitScale);
                    matrix.set(savedMatrix);
                    matrix.postScale(exitScale, exitScale, downTouch.x, downTouch.y);
                    matrix.postTranslate(event.getX() - downTouch.x, translationY);
                    imageViewerOverlayView.setImageMatrix(matrix);
                    // 改变背景全屏遮罩的 Alpha 透明度
                    float alpha = 1.0f - (translationY / (getHeight() * 0.4f));
                    imageViewerOverlayFl.setAlpha(Math.max(0f, Math.min(1.0f, alpha)));
                    handled = true;
                }
                // 核心状态分支三
                // 双指多点缩放处理
                else if (touchMode == TOUCH_PINCH && event.getPointerCount() >= 2) {
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        float scale = (newDist / initDist);
                        float curScale = getMatrixScale(savedMatrix);
                        float next = (curScale * scale);
                        // 强行约束在安全软边界阈值内
                        if (next < minScale) {
                            scale = minScale / curScale;
                        }
                        if (next > maxScale) {
                            scale = maxScale / curScale;
                        }
                        matrix.set(savedMatrix);
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        imageViewerOverlayView.setImageMatrix(matrix);
                    }
                    handled = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // 触摸释放收尾
                // 单指下拉状态
                if (touchMode == TOUCH_EXIT_DRAG) {
                    float moveY = (event.getY() - downTouch.y);
                    if (moveY > getHeight() * exitDragThreshold) {
                        // 超过阈值 + 直接自卸载销毁
                        dismiss();
                    } else {
                        animateBackgroundAlpha(imageViewerOverlayFl.getAlpha());
                        // 放弃退出 + 回弹重置
                        animateMatrix(matrix, savedMatrix);
                    }
                    handled = true;
                }
                // 触摸释放收尾
                // 单指普通拖拽
                else if (touchMode == TOUCH_DRAG) {
                    float totalX = (event.getX() - downTouch.x);
                    float totalY = (event.getY() - downTouch.y);
                    // 在判定死区内 + 视作原生点击分发
                    if ((Math.abs(totalX) < touchSlop) && (Math.abs(totalY) < touchSlop)) {
                        v.performClick();
                    } else {
                        // 超出死区 + 执行平移碰撞回弹
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
     * 双击行为响应逻辑
     *
     * @param clickX X 轴点击坐标
     * @param clickY Y 轴点击坐标
     */
    private void handleDoubleTapZoom(float clickX, float clickY) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return;
        }
        float currentScale = getMatrixScale(matrix);
        Matrix targetMatrix = new Matrix();
        // 状态判定
        // 如果是已放大状态
        // 双击行为定义为复原回全屏自适应状态
        if (currentScale > baseScale * 1.05f) {
            int bmpW = imageViewerOverlayView.getDrawable().getIntrinsicWidth();
            int bmpH = imageViewerOverlayView.getDrawable().getIntrinsicHeight();
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
            currentRotation = 0f;
        } else {
            // 状态判定
            // 如果是自适应状态
            // 双击以当前点击落点为靶心进行深度放大
            float targetScale = baseScale * doubleTapTargetScale;
            float factor = (targetScale / currentScale);
            targetMatrix.set(matrix);
            targetMatrix.postScale(factor, factor, clickX, clickY);
            // 预估放大后的包围盒形变
            // 提前进行视窗边界对齐修正
            RectF targetRect = getImageRect(targetMatrix);
            float overX = calcOverscroll(targetRect, true);
            float overY = calcOverscroll(targetRect, false);
            targetMatrix.postTranslate(-overX, -overY);
        }
        animateMatrix(matrix, targetMatrix);
    }

    /**
     * 基于矩阵九宫格参数直接进行插值的平滑动画执行器
     *
     * @param startMatrix  起始矩阵
     * @param targetMatrix 目标终点绝对矩阵
     */
    private void animateMatrix(@NonNull Matrix startMatrix, @NonNull Matrix targetMatrix) {
        float[] startVals = new float[9];
        float[] targetVals = new float[9];
        startMatrix.getValues(startVals);
        targetMatrix.getValues(targetVals);
        float[] currentVals = new float[9];
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(zoomAnimDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        valueAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            for (int i = 0; i < 9; i++) {
                currentVals[i] = startVals[i] + (targetVals[i] - startVals[i]) * fraction;
            }
            matrix.setValues(currentVals);
            imageViewerOverlayView.setImageMatrix(matrix);
        });
        valueAnimator.start();
    }

    private void animateBackgroundAlpha(float startAlpha) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startAlpha, (float) 1.0);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(a -> imageViewerOverlayFl.setAlpha((float) a.getAnimatedValue()));
        valueAnimator.start();
    }

    @NonNull
    private RectF getImageRect(Matrix matrix) {
        if (imageViewerOverlayView.getDrawable() == null) {
            return new RectF();
        }
        RectF rect = new RectF(0, 0, imageViewerOverlayView.getDrawable().getIntrinsicWidth(), imageViewerOverlayView.getDrawable().getIntrinsicHeight());
        matrix.mapRect(rect);
        return rect;
    }

    /**
     * 计算图片物理边界在当前视窗规格中的越界值
     *
     * @param imageRect  待校验的图片围盒矩形
     * @param horizontal 轴向选择
     *                   true 为 X 轴
     *                   false 为 Y 轴
     * @return 越界物理像素值
     * 0f 代表未越界
     * 正负值代表脱离视窗的边缘偏离量
     */
    private float calcOverscroll(RectF imageRect, boolean horizontal) {
        if (horizontal) {
            float imgW = imageRect.width();
            float viewW = getWidth();
            // 宽度小于视窗
            // 计算居中对齐所需的微调平移量
            if (imgW <= viewW) {
                return (imageRect.left - (viewW - imgW) / 2f);
            } else {
                if (imageRect.left > 0) {
                    return imageRect.left;
                }
                if (imageRect.right < viewW) {
                    return imageRect.right - viewW;
                }
            }
        } else {
            float imgH = imageRect.height();
            float viewH = getHeight();
            // 高度小于视窗
            if (imgH <= viewH) {
                // 特殊规约：长图在初始或未放大的基础状态下，强制顶部对齐，不触发垂直居中。
                if (isLongImage && getMatrixScale(matrix) <= baseScale * 1.05f) {
                    return imageRect.top;
                }
                return (imageRect.top - (viewH - imgH) / 2f);
            } else {
                if (imageRect.top > 0) {
                    return imageRect.top;
                }
                if (imageRect.bottom < viewH) {
                    return imageRect.bottom - viewH;
                }
            }
        }
        return 0f;
    }

    /**
     * 执行平移归位弹性动画
     * <p>
     * 检测当前矩阵在 X 和 Y 轴上的越界量
     * 如果有任意单轴脱离了完美宿主视窗的范围
     * 自动开启平滑反向弹回
     */
    private void springBack() {
        RectF imgRect = getImageRect(matrix);
        float overX = calcOverscroll(imgRect, true);
        float overY = calcOverscroll(imgRect, false);
        if ((overX == 0f) && (overY == 0f)) {
            return;
        }
        // 取得反向目标修补位移
        final float fixDx = -overX;
        final float fixDy = -overY;
        final Matrix startMatrix = new Matrix(matrix);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(springDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
        valueAnimator.addUpdateListener(anim -> {
            float fraction = (float) anim.getAnimatedValue();
            matrix.set(startMatrix);
            matrix.postTranslate(fixDx * fraction, fixDy * fraction);
            imageViewerOverlayView.setImageMatrix(matrix);
        });
        valueAnimator.start();
    }

    /**
     * 核心排版引擎
     * <p>
     * 自适应测算全屏展示位
     * <p>
     * 对高宽比超过 2.2 的图片标记为“超长图”
     * 使其宽度撑满屏幕、顶部对齐
     * 对其余标准纵横比图片
     * 执行完美等比例全视角居中裁剪自适应
     *
     * @param bitmapWith   源图片的物理原色宽度
     * @param bitmapHeight 源图片的物理原色高度
     */
    private void centerAndFit(int bitmapWith, int bitmapHeight) {
        if ((bitmapWith <= 0) || (bitmapHeight <= 0) || (getWidth() <= 0) || (getHeight() <= 0)) {
            return;
        }
        float viewW = getWidth();
        float viewH = getHeight();
        float scaleW = viewW / bitmapWith;
        float scaleH = viewH / bitmapHeight;
        // 设定长图判定规范
        isLongImage = (((float) bitmapHeight / bitmapWith) >= 2.2f);
        matrix.reset();
        if (isLongImage) {
            // 长图横向撑满
            baseScale = scaleW;
            matrix.postScale(baseScale, baseScale);
            float dx = (viewW - bitmapWith * baseScale) / 2f;
            // 垂直轴向对齐顶部
            matrix.postTranslate(dx, 0f);
        } else {
            // 缩放较小者以完全展示整图
            baseScale = Math.min(scaleW, scaleH);
            matrix.postScale(baseScale, baseScale);
            float dx = (viewW - bitmapWith * baseScale) / 2f;
            float dy = (viewH - bitmapHeight * baseScale) / 2f;
            // 完美居中
            matrix.postTranslate(dx, dy);
        }
        imageViewerOverlayView.setImageMatrix(matrix);
        currentRotation = 0f;
    }

    /**
     * 干净抹除状态机参数
     */
    private void resetState() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        matrix.reset();
        currentRotation = 0f;
        isLongImage = false;
        baseScale = 1.0f;
        touchMode = TOUCH_NONE;
        imageViewerOverlayFl.setAlpha(1.0f);
        imageViewerOverlayView.setImageBitmap(null);
        imageViewerOverlayPb.setVisibility(View.VISIBLE);
        imageViewerOverlayTv.setVisibility(View.GONE);
    }

    /**
     * 接收并映射位图资源到画布
     *
     * @param bitmap Bitmap
     */
    void onImageLoaded(Bitmap bitmap) {
        imageViewerOverlayPb.setVisibility(View.GONE);
        if (bitmap == null) {
            imageViewerOverlayTv.setText(getContext().getString(R.string.loadImageFail));
            imageViewerOverlayTv.setVisibility(View.VISIBLE);
            return;
        }
        // 消灭闪烁核心修复点
        // 判定当前宿主组件是否已经完成了全屏物理尺寸测量
        if ((getWidth() > 0) && (getHeight() > 0)) {
            // 尺寸就绪：必须先计算并注入自适应核心矩阵状态，最后才允许进行 Bitmap 渲染上屏。
            centerAndFit(bitmap.getWidth(), bitmap.getHeight());
            imageViewerOverlayView.setImageBitmap(bitmap);
        } else {
            // 异步兜底
            // 此时宿主可能处于初次挂载未测量阶段
            // 先将视窗隐藏（避免原图拉伸残影）
            imageViewerOverlayView.setVisibility(View.INVISIBLE);
            imageViewerOverlayView.setImageBitmap(bitmap);
            imageViewerOverlayView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int l, int t, int r, int b, int oldL, int oldT, int oldR, int oldB) {
                    imageViewerOverlayView.removeOnLayoutChangeListener(this);
                    // 动态测绘结束后重新执行完美的矩阵排版映射
                    centerAndFit(bitmap.getWidth(), bitmap.getHeight());
                    // 抹除首帧闪烁间隙 + 大方展示
                    imageViewerOverlayView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private static float spacing(@NonNull MotionEvent motionEvent) {
        float dx = (motionEvent.getX(0) - motionEvent.getX(1));
        float dy = (motionEvent.getY(0) - motionEvent.getY(1));
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static void midPoint(@NonNull PointF pointF, @NonNull MotionEvent motionEvent) {
        pointF.set((motionEvent.getX(0) + motionEvent.getX(1)) / 2f, (motionEvent.getY(0) + motionEvent.getY(1)) / 2f);
    }

    /**
     * 矩阵缩放解算公式
     * <p>
     * 由于 Matrix 经过 postRotate 旋转后
     * MSCALE_X 和 MSCALE_Y 不再能代表纯粹的拉伸比例
     * 而是会与旋转角度的正余弦复合混淆
     * 此处提取 X 轴的主缩放分量与 Y 轴的切变分量
     * 通过经典的欧几里得勾股定理
     * 计算出无视旋转干扰的真实绝对复合缩放因子
     *
     * @param matrix 矩阵
     * @return 当前 Matrix 产生的物理图像真实拉伸比例
     */
    private static float getMatrixScale(@NonNull Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        return (float) Math.sqrt(v[Matrix.MSCALE_X] * v[Matrix.MSCALE_X] + v[Matrix.MSKEW_Y] * v[Matrix.MSKEW_Y]);
    }

    private static void loadUrlInBackground(String urlString, WeakReference<ImageViewerOverlay> weakReference) {
        Bitmap bitmap;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10_000);
            httpURLConnection.setReadTimeout(15_000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            bitmap = null;
        } finally {
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
        ImageViewerOverlay imageViewerOverlay = weakReference.get();
        if (imageViewerOverlay != null) {
            imageViewerOverlay.handler.post(() -> {
                if (imageViewerOverlay.isAttachedToWindow()) {
                    imageViewerOverlay.onImageLoaded(resultBitmap);
                }
            });
        }
    }

    /**
     * 专为 TalkBack 读屏优化重写的 ImageView 交互单元
     */
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

        @Override
        public boolean performClick() {
            return super.performClick();
        }
    }
}