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
     * 是否包含顶部边缘间距
     */
    private final boolean includeTopEdge;
    /**
     * 是否包含底部边缘间距
     */
    private final boolean includeBottomEdge;
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
     * @param spanCount                     每行条目数
     * @param spacing                       间距
     * @param includeTopEdge                是否包含顶部边缘间距
     * @param includeBottomEdge             是否包含底部边缘间距
     * @param leftmostItemIncludeLeftEdge   最左边条目是否包含左侧边缘间距
     * @param rightmostItemIncludeRightEdge 最右边条目是否包含右侧边缘间距
     */
    public GridLayoutSpaceItemDecoration(int spanCount, int spacing, boolean includeTopEdge, boolean includeBottomEdge, boolean leftmostItemIncludeLeftEdge, boolean rightmostItemIncludeRightEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeTopEdge = includeTopEdge;
        this.includeBottomEdge = includeBottomEdge;
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
        // 3. 是否包含底部边缘间距
        if (includeBottomEdge) {
            outRect.bottom = spacing;
        }
        // 4. 是否包含顶部边缘间距
        if (position < spanCount) {
            outRect.top = includeTopEdge ? spacing : 0;
        } else {
            outRect.top = spacing;
        }
    }
}