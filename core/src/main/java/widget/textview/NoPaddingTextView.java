package widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.zsp.core.R;

/**
 * Created on 2022/3/21
 *
 * @author zsp
 * @desc 无间距 TextView
 */
public class NoPaddingTextView extends AppCompatTextView {
    private final Paint paint = getPaint();
    private final Rect rect = new Rect();
    /**
     * 去字体内边距
     * <p>
     * true 去除、false 不去除
     */
    private Boolean toRemoveTheInBodyMargin = false;

    public NoPaddingTextView(Context context) {
        super(context);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (toRemoveTheInBodyMargin) {
            calculateTextParams();
            setMeasuredDimension(rect.right - rect.left, -rect.top + rect.bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }

    /**
     * 初始化属性
     *
     * @param context 上下文
     * @param attrs   属性
     */
    private void initAttributes(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NoPaddingTextView);
        toRemoveTheInBodyMargin = typedArray.getBoolean(R.styleable.NoPaddingTextView_noPaddingTextViewRemoveDefaultPadding, false);
        typedArray.recycle();
    }

    /**
     * 算文本参数
     *
     * @return 文本参数
     */
    @NonNull
    private String calculateTextParams() {
        String text = getText().toString();
        int textLength = text.length();
        paint.getTextBounds(text, 0, textLength, rect);
        if (textLength == 0) {
            rect.right = rect.left;
        }
        return text;
    }

    /**
     * 绘文本
     *
     * @param canvas 画布
     */
    private void drawText(@NonNull Canvas canvas) {
        String text = calculateTextParams();
        int left = rect.left;
        rect.offset(-rect.left, -rect.top);
        paint.setAntiAlias(true);
        paint.setColor(getCurrentTextColor());
        canvas.drawText(text, (float) (-left), (float) (0), paint);
    }
}