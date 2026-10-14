package widget.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 2019/7/1.
 *
 * @author 郑少鹏
 * @desc 线性布局水平条目间距装饰
 * 头条目左间距
 * 其后都右间距
 */
public class LinearLayoutHorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 间距
     */
    private final int space;
    /**
     * 上下偏移
     */
    private final boolean topAndBottomOffset;
    /**
     * 最左边条目是否包含左侧边缘间距
     */
    private final boolean leftmostItemIncludeLeftEdge;
    /**
     * 最右边条目是否包含右侧边缘间距
     */
    private final boolean rightmostItemIncludeRightEdge;

    /**
     * constructor
     *
     * @param space                         间距
     * @param topAndBottomOffset            上下偏移
     * @param leftmostItemIncludeLeftEdge   最左边条目是否包含左侧边缘间距
     * @param rightmostItemIncludeRightEdge 最右边条目是否包含右侧边缘间距
     */
    public LinearLayoutHorizontalSpaceItemDecoration(int space, boolean topAndBottomOffset, boolean leftmostItemIncludeLeftEdge, boolean rightmostItemIncludeRightEdge) {
        this.space = space;
        this.topAndBottomOffset = topAndBottomOffset;
        this.leftmostItemIncludeLeftEdge = leftmostItemIncludeLeftEdge;
        this.rightmostItemIncludeRightEdge = rightmostItemIncludeRightEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        int itemCount = state.getItemCount();
        // 1. 上下偏移处理
        if (topAndBottomOffset) {
            outRect.top = space;
            outRect.bottom = space;
        }
        // 2. 最左边条目是否包含左侧边缘间距处理
        if (position == 0) {
            // 最左边条目
            outRect.left = leftmostItemIncludeLeftEdge ? space : 0;
        } else {
            outRect.left = 0;
        }
        // 3. 最右边条目是否包含右侧边缘间距处理
        if (position == (itemCount - 1)) {
            // 最右边条目
            outRect.right = rightmostItemIncludeRightEdge ? space : 0;
        } else {
            outRect.right = space;
        }
    }
}