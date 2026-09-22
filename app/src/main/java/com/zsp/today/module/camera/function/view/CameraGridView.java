package com.zsp.today.module.camera.function.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.today.R;

/**
 * Created on 2026/9/20.
 *
 * @author 郑少鹏
 * @desc 相机网格视图
 */
@SuppressWarnings("unused")
public class CameraGridView extends View {
    /**
     * 线条画笔
     */
    private Paint linePaint;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public CameraGridView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context      上下文
     * @param attributeSet 属性集
     */
    public CameraGridView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /**
     * constructor
     *
     * @param context               上下文
     * @param attributeSet          属性集
     * @param defaultStyleAttribute 模式样式属性
     */
    public CameraGridView(Context context, @Nullable AttributeSet attributeSet, int defaultStyleAttribute) {
        super(context, attributeSet, defaultStyleAttribute);
        init(context, attributeSet);
    }

    /**
     * 初始化
     *
     * @param context      上下文
     * @param attributeSet 属性集
     */
    private void init(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        // 线条颜色
        int lineColor = Color.WHITE;
        // 线条宽度
        float defaultLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics());
        float lineWidth = defaultLineWidth;
        // 加载属性
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CameraGridView);
            lineColor = typedArray.getColor(R.styleable.CameraGridView_lineColor, lineColor);
            lineWidth = typedArray.getDimension(R.styleable.CameraGridView_lineWidth, defaultLineWidth);
            typedArray.recycle();
        }
        // 初始画笔
        linePaint = new Paint();
        // 线条颜色
        linePaint.setColor(lineColor);
        // 线条宽度
        linePaint.setStrokeWidth(lineWidth);
        // 线条样式
        linePaint.setStyle(Paint.Style.STROKE);
        // 消除锯齿
        linePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        if ((width <= 0) || (height <= 0)) {
            return;
        }
        // 1. 计算两条纵线位置
        // 画面水平三等分，即 1/3 和 2/3 处。
        float verticalLine1X = width / 3f;
        float verticalLine2X = (width / 3f) * 2f;
        // 2. 计算两条横线位置
        // 画面垂直三等分，即 1/3 和 2/3 处。
        float horizontalLine1Y = height / 3f;
        float horizontalLine2Y = (height / 3f) * 2f;
        // 绘制两条纵线
        canvas.drawLine(verticalLine1X, 0f, verticalLine1X, height, linePaint);
        canvas.drawLine(verticalLine2X, 0f, verticalLine2X, height, linePaint);
        // 绘制两条横线
        canvas.drawLine(0f, horizontalLine1Y, width, horizontalLine1Y, linePaint);
        canvas.drawLine(0f, horizontalLine2Y, width, horizontalLine2Y, linePaint);
    }

    /**
     * 设置线条颜色
     *
     * @param lineColor 线条颜色
     */
    public void setLineColor(@ColorInt int lineColor) {
        if (linePaint != null) {
            linePaint.setColor(lineColor);
            invalidate();
        }
    }

    /**
     * 设置线条宽度
     *
     * @param lineWidth 线条宽度
     *                  单位 PX
     */
    public void setLineWidth(float lineWidth) {
        if (linePaint != null) {
            linePaint.setStrokeWidth(lineWidth);
            invalidate();
        }
    }

    /**
     * 设置线条宽度
     *
     * @param lineWidthDp 线条宽度
     *                    单位 DP
     */
    public void setLineWidthDp(float lineWidthDp) {
        float strokeWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidthDp, getResources().getDisplayMetrics());
        setLineWidth(strokeWidthPx);
    }
}