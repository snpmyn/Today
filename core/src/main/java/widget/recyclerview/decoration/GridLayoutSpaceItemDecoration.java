package widget.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 2019/3/22.
 *
 * @author 郑少鹏
 * @desc 表格布局条目间距装饰
 */
public class GridLayoutSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int spanCount;
    private final int spacing;
    /**
     * 最顶部条目是否包含顶部边缘间距
     */
    private final boolean topmostItemIncludeTopEdge;
    /**
     * 最底部条目是否包含底部边缘间距
     */
    private final boolean bottommostItemIncludeBottomEdge;
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
     * @param spanCount                       每行条目数
     * @param spacing                         间距
     * @param topmostItemIncludeTopEdge       最顶部条目是否包含顶部边缘间距
     * @param bottommostItemIncludeBottomEdge 最底部条目是否包含底部边缘间距
     * @param leftmostItemIncludeLeftEdge     最左边条目是否包含左侧边缘间距
     * @param rightmostItemIncludeRightEdge   最右边条目是否包含右侧边缘间距
     */
    public GridLayoutSpaceItemDecoration(int spanCount, int spacing, boolean topmostItemIncludeTopEdge, boolean bottommostItemIncludeBottomEdge, boolean leftmostItemIncludeLeftEdge, boolean rightmostItemIncludeRightEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.topmostItemIncludeTopEdge = topmostItemIncludeTopEdge;
        this.bottommostItemIncludeBottomEdge = bottommostItemIncludeBottomEdge;
        this.leftmostItemIncludeLeftEdge = leftmostItemIncludeLeftEdge;
        this.rightmostItemIncludeRightEdge = rightmostItemIncludeRightEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = (position % spanCount);
        // 1. 最左边条目是否包含左侧边缘间距处理
        if (leftmostItemIncludeLeftEdge) {
            outRect.left = (spacing - column * spacing / spanCount);
        } else {
            outRect.left = (column * spacing / spanCount);
        }
        // 2. 最右边条目是否包含右侧边缘间距处理
        if (rightmostItemIncludeRightEdge) {
            outRect.right = ((column + 1) * spacing / spanCount);
        } else {
            outRect.right = (spacing - (column + 1) * spacing / spanCount);
        }
        // 计算最后一行起始索引
        int totalItemCount = state.getItemCount();
        int lastRowStartIndex = (totalItemCount - 1) / spanCount * spanCount;
        // 3. 最底部条目是否包含底部边缘间距处理
        // 仅最后一行受 includeBottomEdge 控制，其余行设为 0 避免上下间距叠加。
        if (position >= lastRowStartIndex) {
            outRect.bottom = bottommostItemIncludeBottomEdge ? spacing : 0;
        } else {
            outRect.bottom = 0;
        }
        // 4. 最顶部条目是否包含顶部边缘间距处理
        if (position < spanCount) {
            outRect.top = topmostItemIncludeTopEdge ? spacing : 0;
        } else {
            outRect.top = spacing;
        }
    }
}