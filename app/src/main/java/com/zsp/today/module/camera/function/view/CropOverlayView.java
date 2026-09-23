package com.zsp.today.module.camera.function.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 2026/9/23.
 *
 * @author 郑少鹏
 * @desc 裁剪覆盖视图
 */
public class CropOverlayView extends View {
    /**
     * 触控角点半径
     */
    private static final float HANDLE_RADIUS = 24f;
    /**
     * 手势响应感应范围
     */
    private static final float TOUCH_TOLERANCE = 40f;
    /**
     * 未触控
     * <p>
     * 触控手柄标识
     */
    private static final int HANDLE_NONE = 0;
    /**
     * 左边框
     * <p>
     * 触控手柄标识
     */
    private static final int HANDLE_LEFT = 1;
    /**
     * 上边框
     * <p>
     * 触控手柄标识
     */
    private static final int HANDLE_TOP = 2;
    /**
     * 右边框
     * <p>
     * 触控手柄标识
     */
    private static final int HANDLE_RIGHT = 3;
    /**
     * 下边框
     * <p>
     * 触控手柄标识
     */
    private static final int HANDLE_BOTTOM = 4;
    /**
     * 遮罩画笔
     */
    private final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 边框画笔
     */
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 手柄画笔
     */
    private final Paint handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 预先分配 Path 对象
     * <p>
     * 避免 onDraw 动态创建
     */
    private final Path maskPath = new Path();
    /**
     * 归一化裁剪矩形
     * <p>
     * [0.0 ~ 1.0]
     */
    private final RectF normalizedCropRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    /**
     * 实际像素矩形
     */
    private final RectF cropRect = new RectF();
    /**
     * 当前激活的触控手柄
     */
    private int activeHandle = HANDLE_NONE;
    /**
     * 上一次触摸点的 X 坐标
     */
    private float lastX;
    /**
     * 上一次触摸点的 Y 坐标
     */
    private float lastY;
    /**
     * 裁剪覆盖回调
     */
    private OnCropOverlayCallback onCropOverlayCallback;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public CropOverlayView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context      上下文
     * @param attributeSet 属性集
     */
    public CropOverlayView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /**
     * constructor
     *
     * @param context               上下文
     * @param attributeSet          属性集
     * @param defaultStyleAttribute 默认样式属性
     */
    public CropOverlayView(Context context, @Nullable AttributeSet attributeSet, int defaultStyleAttribute) {
        super(context, attributeSet, defaultStyleAttribute);
        initPaints();
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        // 半透明黑遮罩
        maskPaint.setColor(Color.parseColor("#80000000"));
        maskPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);

        handlePaint.setColor(Color.WHITE);
        handlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCropRectFromNormalized();
    }

    /**
     * 根据归一化坐标更新实际像素坐标
     */
    private void updateCropRectFromNormalized() {
        int width = getWidth();
        int height = getHeight();
        cropRect.left = normalizedCropRect.left * width;
        cropRect.top = normalizedCropRect.top * height;
        cropRect.right = normalizedCropRect.right * width;
        cropRect.bottom = normalizedCropRect.bottom * height;
    }

    /**
     * 根据实际像素坐标更新归一化坐标并触发回调
     */
    private void updateNormalizedFromCropRect() {
        int width = getWidth();
        int height = getHeight();
        if ((width <= 0) || (height <= 0)) {
            return;
        }
        normalizedCropRect.left = Math.max(0.0f, cropRect.left / width);
        normalizedCropRect.top = Math.max(0.0f, cropRect.top / height);
        normalizedCropRect.right = Math.min(1.0f, cropRect.right / width);
        normalizedCropRect.bottom = Math.min(1.0f, cropRect.bottom / height);
        if (onCropOverlayCallback != null) {
            onCropOverlayCallback.OnCropOverlayChanged(new RectF(normalizedCropRect));
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        // 复用 Path
        // 先重置内部数据结构
        maskPath.reset();
        maskPath.addRect(0, 0, width, height, Path.Direction.CW);
        maskPath.addRect(cropRect, Path.Direction.CCW);
        // 绘制四周半透明遮罩
        // 掏空中间 Crop 区域
        canvas.drawPath(maskPath, maskPaint);
        // 绘制裁剪框白边
        canvas.drawRect(cropRect, borderPaint);
        // 绘制四边中间的拖动手柄触点
        canvas.drawCircle(cropRect.left, cropRect.centerY(), HANDLE_RADIUS, handlePaint);
        canvas.drawCircle(cropRect.right, cropRect.centerY(), HANDLE_RADIUS, handlePaint);
        canvas.drawCircle(cropRect.centerX(), cropRect.top, HANDLE_RADIUS, handlePaint);
        canvas.drawCircle(cropRect.centerX(), cropRect.bottom, HANDLE_RADIUS, handlePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeHandle = getTouchedHandle(x, y);
                lastX = x;
                lastY = y;
                return activeHandle != HANDLE_NONE;
            case MotionEvent.ACTION_MOVE:
                if (activeHandle != HANDLE_NONE) {
                    float dx = (x - lastX);
                    float dy = (y - lastY);
                    moveHandle(dx, dy);
                    lastX = x;
                    lastY = y;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activeHandle = HANDLE_NONE;
                updateNormalizedFromCropRect();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取当前触摸位置所对应的手柄标识
     *
     * @param x 触摸点 X 坐标
     * @param y 触摸点 Y 坐标
     * @return 触摸的手柄标识
     */
    private int getTouchedHandle(float x, float y) {
        if (Math.abs(x - cropRect.left) < TOUCH_TOLERANCE && Math.abs(y - cropRect.centerY()) < TOUCH_TOLERANCE * 2) {
            return HANDLE_LEFT;
        } else if (Math.abs(x - cropRect.right) < TOUCH_TOLERANCE && Math.abs(y - cropRect.centerY()) < TOUCH_TOLERANCE * 2) {
            return HANDLE_RIGHT;
        } else if (Math.abs(y - cropRect.top) < TOUCH_TOLERANCE && Math.abs(x - cropRect.centerX()) < TOUCH_TOLERANCE * 2) {
            return HANDLE_TOP;
        } else if (Math.abs(y - cropRect.bottom) < TOUCH_TOLERANCE && Math.abs(x - cropRect.centerX()) < TOUCH_TOLERANCE * 2) {
            return HANDLE_BOTTOM;
        }
        return HANDLE_NONE;
    }

    /**
     * 移动当前激活的手柄并更新裁剪框位置
     *
     * @param dx X 轴偏移量
     * @param dy Y 轴偏移量
     */
    private void moveHandle(float dx, float dy) {
        // 最小允许框选尺寸
        float minSize = 100f;
        switch (activeHandle) {
            case HANDLE_LEFT:
                cropRect.left = Math.min(Math.max(0, cropRect.left + dx), cropRect.right - minSize);
                break;
            case HANDLE_RIGHT:
                cropRect.right = Math.max(Math.min(getWidth(), cropRect.right + dx), cropRect.left + minSize);
                break;
            case HANDLE_TOP:
                cropRect.top = Math.min(Math.max(0, cropRect.top + dy), cropRect.bottom - minSize);
                break;
            case HANDLE_BOTTOM:
                cropRect.bottom = Math.max(Math.min(getHeight(), cropRect.bottom + dy), cropRect.top + minSize);
                break;
        }
        updateNormalizedFromCropRect();
    }

    /**
     * 获取归一化裁剪矩形
     *
     * @return 归一化裁剪矩形
     */
    @NonNull
    public RectF getNormalizedCropRect() {
        return new RectF(normalizedCropRect);
    }

    /**
     * 设置裁剪覆盖回调
     *
     * @param onCropOverlayCallback 裁剪覆盖回调
     */
    public void setOnCropOverlayCallback(OnCropOverlayCallback onCropOverlayCallback) {
        this.onCropOverlayCallback = onCropOverlayCallback;
    }

    /**
     * 裁剪覆盖回调
     */
    public interface OnCropOverlayCallback {
        /**
         * 裁剪覆盖变化
         *
         * @param normalizedCropRect 归一化裁剪矩形
         *                           [0.0 ~ 1.0]
         */
        void OnCropOverlayChanged(@NonNull RectF normalizedCropRect);
    }
}