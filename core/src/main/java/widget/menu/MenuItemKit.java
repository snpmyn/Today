package widget.menu;

import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021/4/21
 *
 * @author zsp
 * @desc MenuItem 配套元件
 */
public class MenuItemKit {
    public static MenuItemKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param menuItem 菜单条目
     */
    public void show(@NotNull MenuItem menuItem) {
        menuItem.setVisible(true);
        menuItem.setEnabled(true);
    }

    /**
     * 隐藏
     *
     * @param menuItem 菜单条目
     */
    public void hide(@NotNull MenuItem menuItem) {
        menuItem.setVisible(false);
        menuItem.setEnabled(false);
    }

    private static final class InstanceHolder {
        static final MenuItemKit INSTANCE = new MenuItemKit();
    }
}