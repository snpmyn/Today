package com.zsp.today.module.zhilin.customview.customview.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created on 2020/7/6.
 *
 * @author zsp
 * @desc 弧
 */
public class MyArc extends View {
    private Paint paint;

    public MyArc(Context context) {
        super(context);
    }

    public MyArc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public MyArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        canvas.drawArc(Integer.valueOf(getWidth() / 2 - 240).floatValue(), Integer.valueOf(getHeight() / 2 - 120).floatValue(),
                Integer.valueOf(getWidth() / 2 + 240).floatValue(), Integer.valueOf(getHeight() / 2 + 120).floatValue(),
                45.0F, 45.0F, true, paint);
    }
}