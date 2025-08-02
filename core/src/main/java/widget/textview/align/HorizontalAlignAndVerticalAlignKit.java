package widget.textview.align;

import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created on 2022/3/25
 *
 * @author zsp
 * @desc 水平对齐和垂直对齐配套元件
 */
public class HorizontalAlignAndVerticalAlignKit {
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

    /**
     * 创动作菜单
     *
     * @param actionMenu              动作菜单
     * @param actionMenuItemTitleList 动作菜单条目标题集
     */
    public void createActionMenu(@NonNull ActionMenu actionMenu, List<String> actionMenuItemTitleList) {
        actionMenu.setActionMenuItemTextColor(0xffffffff);
        actionMenu.setActionMenuBackgroundColor(0xff666666);
        actionMenu.addActionMenuItem(actionMenuItemTitleList);
    }
}
