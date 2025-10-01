package widget.window;

import android.view.Window;

/**
 * Created on 2025/10/1.
 *
 * @author 郑少鹏
 * @desc 窗口配套原件
 */
public class WindowKit {
    /**
     * 设置背景位图资源透明
     *
     * @param window 窗口
     */
    public static void setBackgroundDrawableResourceTransparent(Window window) {
        if (null != window) {
            // 去掉背景
            // 默认白底圆角
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}