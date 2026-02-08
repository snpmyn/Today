package com.zsp.today.module.zhilin.customview.customview.line;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created on 2020/7/3.
 *
 * @author zsp
 * @desc 多线
 */
public class MyLines extends View {
    private Paint paint;

    public MyLines(Context context) {
        super(context);
    }

    public MyLines(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public MyLines(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), com.zsp.core.R.color.color_FFEE6002));
        paint.setStrokeWidth(24.0F);
        paint.setStrokeCap(Paint.Cap.ROUND);
        @SuppressLint("DrawAllocation") float[] floats = new float[]{Integer.valueOf(getWidth() / 2 - 240).floatValue(), Integer.valueOf(getHeight() / 2 - 120).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2 + 240).floatValue(), Integer.valueOf(getHeight() / 2 + 120).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2 + 240).floatValue(), Integer.valueOf(getHeight() / 2 - 120).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2 - 240).floatValue(), Integer.valueOf(getHeight() / 2 + 120).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2 - 240).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2).floatValue(),
                Integer.valueOf(getWidth() / 2).floatValue(), Integer.valueOf(getHeight() / 2 + 240).floatValue()};
        canvas.drawLines(floats, paint);
    }
}