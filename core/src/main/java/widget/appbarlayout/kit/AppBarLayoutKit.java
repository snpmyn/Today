package widget.appbarlayout.kit;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created on 2019/6/14.
 *
 * @author 郑少鹏
 * @desc AppBarLayoutKit
 */
public class AppBarLayoutKit {
    /**
     * 滑标志
     * <p>
     * CollapsingToolbarLayout 自身 app:layout_scrollFlags 含 scroll 时下设 layout_behavior 布局无法垂直居中。
     * 不设 scroll 上无法滑而吸顶。
     * 动设即可。
     *
     * @param view  视图
     * @param flags 标志
     */
    public static void setScrollFlags(@NonNull View view, int flags) {
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setScrollFlags(flags);
        view.setLayoutParams(layoutParams);
    }
}
