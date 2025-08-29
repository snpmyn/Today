package widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created on 2018/10/25.
 *
 * @author 郑少鹏
 * @desc 上下图居中（xml 设 android:gravity="center_horizontal"）背景自白
 * 左右图居中（xml 设 android:gravity="center_vertical"）背景自白
 */
public class DrawableCenterTextView extends AppCompatTextView {
    private final Rect rect;

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获 TextView 之 Drawable 对象，返数组长应 4（对应左上右下）
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawable = drawables[0];
        if (null != drawable) {
            // 左 Drawable 非空，测需绘文本宽
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawable.getIntrinsicWidth();
            // 总宽（文本宽 + drawablePadding + drawableWidth）
            float bodyWidth = (textWidth + drawablePadding + drawableWidth);
            // 移画布绘 X 轴
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        } else if (null != (drawable = drawables[1])) {
            // 上 Drawable 非空则获文本高
            getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), rect);
            float textHeight = rect.height();
            int drawablePadding = getCompoundDrawablePadding();
            int drawableHeight = drawable.getIntrinsicHeight();
            // 总高（文本高 + drawablePadding + drawableHeight）
            float bodyHeight = (textHeight + drawablePadding + drawableHeight);
            // 移画布绘 Y 轴
            canvas.translate(0, (getHeight() - bodyHeight) / 2);
        }
        super.onDraw(canvas);
    }
}