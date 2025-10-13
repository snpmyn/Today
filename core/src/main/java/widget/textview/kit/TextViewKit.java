package widget.textview.kit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019/1/8.
 *
 * @author 郑少鹏
 * @desc TextView 配套元件
 */
public class TextViewKit {
    /**
     * 创造 TextView
     *
     * @param context   上下文
     * @param text      文本
     * @param textSize  文本尺寸
     * @param textColor 文本颜色
     * @param bold      粗体否
     * @return TextView
     */
    @NonNull
    public static TextView createTextView(Context context, String text, int textSize, int textColor, boolean bold) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        if (bold) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    /**
     * 设置位图
     *
     * @param context       上下文
     * @param textView      TextView
     * @param drawableResId 位图资源 ID
     * @param position      位置（1 左、2 上、3 右、4 下）
     * @param padding       间距
     */
    public static void setDrawable(Context context, TextView textView, int drawableResId, int position, int padding) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            switch (position) {
                case 1:
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    break;
                case 2:
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case 3:
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    break;
                case 4:
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                    break;
                default:
                    break;
            }
            textView.setCompoundDrawablePadding(padding);
        }
    }

    /**
     * 设置位图颜色
     *
     * @param textView TextView
     * @param color    颜色
     */
    public static void setDrawableColor(@NotNull TextView textView, int color) {
        Drawable[] drawables = textView.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (null != drawable) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /**
     * 获 TextView 宽
     *
     * @param context  上下文
     * @param textView TextView
     * @return TextView 宽
     */
    public static int getTextViewWidth(@NotNull Context context, @NotNull TextView textView) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.widthPixels - textView.getPaddingLeft() - textView.getPaddingRight());
    }
}