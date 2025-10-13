package widget.textview.kit;

import androidx.annotation.NonNull;

import widget.textview.LeftAndRightAlignTextView;

/**
 * Created on 2025/10/8.
 *
 * @author 郑少鹏
 * @desc 左右对齐 TextView 配套原件
 */
public class LeftAndRightAlignTextViewKit {
    /**
     * 设水平对齐两端对齐否
     *
     * @param leftAndRightAlignTextView LeftAndRightAlignTextView
     * @param areTextJustify            两端对齐否
     */
    public void setHorizontalAlignAreTextJustify(@NonNull LeftAndRightAlignTextView leftAndRightAlignTextView, Boolean areTextJustify) {
        leftAndRightAlignTextView.setAreTextJustify(areTextJustify);
        leftAndRightAlignTextView.postInvalidate();
    }

    /**
     * 设水平对齐文本
     *
     * @param leftAndRightAlignTextView LeftAndRightAlignTextView
     * @param text                      文本
     */
    public void setHorizontalAlignText(@NonNull LeftAndRightAlignTextView leftAndRightAlignTextView, String text) {
        leftAndRightAlignTextView.setText(text);
        leftAndRightAlignTextView.postInvalidate();
    }
}