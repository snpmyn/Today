package widget.materialtoolbar;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Created on 2025/8/23.
 *
 * @author 郑少鹏
 * @desc 材料工具栏配套原件
 */
public class MaterialToolbarKit {
    public static MaterialToolbarKit getInstance() {
        return MaterialToolbarKit.InstanceHolder.INSTANCE;
    }

    /**
     * 设置菜单溢出图标色调颜色
     *
     * @param materialToolbar 材料工具栏
     * @param tintColor       色调颜色
     */
    public void setMenuOverflowIconTintColor(@NonNull MaterialToolbar materialToolbar, int tintColor) {
        Drawable drawable = materialToolbar.getOverflowIcon();
        if (null != drawable) {
            drawable.setTint(tintColor);
            materialToolbar.setOverflowIcon(drawable);
        }
    }

    private static final class InstanceHolder {
        static final MaterialToolbarKit INSTANCE = new MaterialToolbarKit();
    }
}