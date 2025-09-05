package widget.spannablestringbuilder;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Created on 2025/9/5.
 *
 * @author 郑少鹏
 * @desc 富文本构建器配套原件
 * <p>
 * ForegroundColorSpan
 * 改变文字前景色（字体颜色）
 * <p>
 * BackgroundColorSpan
 * 改变文字背景色
 * <p>
 * AbsoluteSizeSpan
 * 改变文字绝对大小
 * <p>
 * RelativeSizeSpan
 * 改变文字相对大小（例如，放大1.5倍）
 * <p>
 * StyleSpan
 * 设置文字样式（加粗 Bold、斜体 Italic 等）
 * <p>
 * UnderlineSpan
 * 给文字添加下划线
 * <p>
 * StrikethroughSpan
 * 给文字添加删除线
 * <p>
 * ClickableSpan
 * 使文字可以点击（类似于超链接）
 * <p>
 * ImageSpan
 * 在文字中插入图片
 */
public class SpannableStringBuilderKit extends SpannableStringBuilder {
    /**
     * constructor
     * <p>
     * Create a new SpannableStringBuilder containing a copy of the specified text, including its spans if any.
     *
     * @param text 文本
     */
    public SpannableStringBuilderKit(CharSequence text) {
        super(text);
    }

    /**
     * 设置可点击文本
     *
     * @param color                             颜色
     * @param underlineText                     下划线否
     * @param start                             起始索引
     *                                          <p>
     *                                          指定你要应用样式的文本范围的开始位置
     *                                          <p>
     *                                          索引从 0 开始（即字符串的第一个字符）
     * @param end                               结束索引
     *                                          <p>
     *                                          指定你要应用样式的文本范围的结束位置
     *                                          <p>
     *                                          重要：这个范围是 [start, end)，即包括 start，但不包括 end。例如，setSpan(..., 1, 4, ...) 会应用于索引为 1, 2, 3 的字符。
     * @param flag                              标志位
     *                                          <p>
     *                                          这个参数定义了当文本发生变化时（例如，用户在 EditText 中插入或删除文字），这个 Span 应该如何表现。
     *                                          <p>
     *                                          {@link android.text.Spannable#SPAN_EXCLUSIVE_EXCLUSIVE}}
     *                                          最常用。范围不包括插入到起始和结束位置的新文本。例如，你在 Hello 的 e 和 l之间设置了样式，如果在它们之间输入新字符，新字符不会有这个样式。
     *                                          <p>
     *                                          Spannable.SPAN_INCLUSIVE_EXCLUSIVE
     *                                          {@link android.text.Spannable#SPAN_INCLUSIVE_EXCLUSIVE}}
     *                                          范围包括插入到起始位置的新文本，但不包括插入到结束位置的新文本。
     *                                          <p>
     *                                          Spannable.SPAN_EXCLUSIVE_INCLUSIVE
     *                                          {@link android.text.Spannable#SPAN_EXCLUSIVE_INCLUSIVE}}
     *                                          范围不包括插入到起始位置的新文本，但包括插入到结束位置的新文本。
     *                                          <p>
     *                                          Spannable.SPAN_INCLUSIVE_INCLUSIVE
     *                                          {@link android.text.Spannable#SPAN_INCLUSIVE_INCLUSIVE}}
     *                                          范围包括插入到起始和结束位置的新文本
     * @param spannableStringBuilderKitListener 富文本构建器配套原件监听
     */
    public void setClickableSpan(int color, boolean underlineText, int start, int end, int flag, SpannableStringBuilderKitListener spannableStringBuilderKitListener) {
        super.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (null != spannableStringBuilderKitListener) {
                    spannableStringBuilderKitListener.onClick();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);
                ds.setUnderlineText(underlineText);
            }
        }, start, end, flag);
    }

    /**
     * 配置可点击文本控件
     *
     * @param textView                  控件
     * @param spannableStringBuilderKit 富文本构建器配套原件
     */
    public void configClickableSpanTextView(@NonNull TextView textView, SpannableStringBuilderKit spannableStringBuilderKit) {
        // 设置文本视图使用链接移动方法，使其能够识别和响应文本中的可点击链接。
        // setMovementMethod()：是一个用于设置文本移动 / 交互方法的方法。
        // LinkMovementMethod.getInstance()：获取链接移动方法的实例，专门用于处理超链接点击。
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // 将一个 SpannableStringBuilder 对象设置为 TextView 的文本内容，并明确告知 TextView 这个文本是可被添加样式 (Spannable) 的，以便后续可以动态地更改或添加文本中的样式。
        textView.setText(spannableStringBuilderKit, TextView.BufferType.SPANNABLE);
    }

    /**
     * 富文本构建器配套原件监听
     */
    public interface SpannableStringBuilderKitListener {
        /**
         * 点击
         */
        void onClick();
    }
}