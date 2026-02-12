package com.zsp.today.module.zhilin.customview.path;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created on 2020/7/6.
 *
 * @author zsp
 * @desc 路径
 * <p>
 * {@link Path} 画直线。须指定 {@link Paint} 的 style 为 STROKE，否则 Paint 默用 FILL，画不出直线。
 * {@link Path} 画方形。画填充和描边两种方形需分别指定样式。
 */
public class MyPath extends View {
    private Paint paint;
    private Path path;

    public MyPath(Context context) {
        super(context);
    }

    public MyPath(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
    }

    public MyPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // 画笔
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), com.zsp.core.R.color.color_FFEE6002));
        paint.setStrokeWidth(24.0F);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        // 路径
        path.moveTo(Integer.valueOf(getWidth() / 2 - 240).floatValue(), Integer.valueOf(getHeight() / 2 - 120).floatValue());
        path.lineTo(Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue());
        path.moveTo(Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue());
        path.lineTo(Integer.valueOf(getWidth() / 2 + 240).floatValue(), Integer.valueOf(getHeight() / 2 + 160).floatValue());
        canvas.drawPath(path, paint);
    }
}