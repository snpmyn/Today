package widget.appbarlayout.kit

import android.view.View
import com.google.android.material.appbar.AppBarLayout

/**
 * Created on 2026/4/8.
 * @author 郑少鹏
 * @desc AppBarLayoutKit
 *
 * 暂无引用
 */
class AppBarLayoutKit {
    companion object {
        /**
         * 滑标志
         * <p>
         * CollapsingToolbarLayout 自身 app:layout_scrollFlags 含 scroll 时下设 layout_behavior 布局无法垂直居中
         * 不设 scroll 上无法滑而吸顶
         * 动设即可
         *
         * @param view  视图
         * @param flags 标志
         */
        fun setScrollFlags(view: View, flags: Int) {
            val layoutParams = view.layoutParams as AppBarLayout.LayoutParams
            layoutParams.setScrollFlags(flags)
            view.layoutParams = layoutParams
        }
    }
}