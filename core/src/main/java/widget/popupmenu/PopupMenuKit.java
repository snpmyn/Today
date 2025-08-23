package widget.popupmenu;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.MenuRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

/**
 * Created on 2025/8/23.
 *
 * @author 郑少鹏
 * @desc 弹出式菜单配套原件
 */
public class PopupMenuKit {
    public static PopupMenuKit getInstance() {
        return PopupMenuKit.InstanceHolder.INSTANCE;
    }

    /**
     * 弹出式菜单
     *
     * @param appCompatActivity    活动
     * @param view                 视图
     * @param menuResId            菜单资源 ID
     * @param popupMenuKitListener 弹出式菜单配套原件监听
     */
    @SuppressLint("RestrictedApi")
    public void popupMenu(AppCompatActivity appCompatActivity, View view, @MenuRes int menuResId, PopupMenuKitListener popupMenuKitListener) {
        PopupMenu popupMenu = new PopupMenu(appCompatActivity, view);
        // Inflating the Popup using xml file.
        popupMenu.getMenuInflater().inflate(menuResId, popupMenu.getMenu());
        // There is no public API to make icons show on menus.
        // IF you need the icons to show this works however it's discouraged to rely on library only APIs since they might disappear in future versions.
        if (popupMenu.getMenu() instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) popupMenu.getMenu();
            // noinspection RestrictedApi
            menuBuilder.setOptionalIconsVisible(true);
            // noinspection RestrictedApi
            for (MenuItem menuItem : menuBuilder.getVisibleItems()) {
                popupMenuKitListener.menuItem(menuItem);
            }
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            popupMenuKitListener.onMenuItemClick(popupMenu, item);
            return true;
        });
        popupMenu.show();
    }

    /**
     * 弹出式菜单配套原件监听
     */
    public interface PopupMenuKitListener {
        /**
         * 菜单条目
         *
         * @param menuItem 菜单条目
         */
        void menuItem(MenuItem menuItem);

        /**
         * 菜单条目短点
         *
         * @param popupMenu 弹出式菜单
         * @param menuItem  菜单条目
         */
        void onMenuItemClick(PopupMenu popupMenu, MenuItem menuItem);
    }

    private static final class InstanceHolder {
        static final PopupMenuKit INSTANCE = new PopupMenuKit();
    }
}