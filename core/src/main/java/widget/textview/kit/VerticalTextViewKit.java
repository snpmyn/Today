package widget.textview.kit;

import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;

import widget.textview.VerticalTextView;

/**
 * Created on 2025/10/8.
 *
 * @author 郑少鹏
 * @desc 垂直 TextView 配套原件
 */
public class VerticalTextViewKit {
    /**
     * 设垂直对齐从左到右否
     *
     * @param verticalTextView     VerticalTextView
     * @param fromLeftToRight      从左到右否
     * @param horizontalScrollView HorizontalScrollView
     * @param direction            方向
     */
    public void setVerticalAlignAreFromLeftToRight(@NonNull VerticalTextView verticalTextView, Boolean fromLeftToRight, @NonNull HorizontalScrollView horizontalScrollView, int direction) {
        verticalTextView.setAreLeftToRight(fromLeftToRight).requestLayout();
        horizontalScrollView.fullScroll(direction);
    }

    /**
     * 设垂直对齐需下划线否
     *
     * @param verticalTextView VerticalTextView
     * @param areUnderLineText 需下划线否
     */
    public void setVerticalAlignAreUnderLineText(@NonNull VerticalTextView verticalTextView, Boolean areUnderLineText) {
        verticalTextView.setAreUnderLineText(areUnderLineText).requestLayout();
    }
}