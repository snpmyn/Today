package widget.menu

import android.view.MenuItem

/**
 * Created on 2026/4/8.
 * @author 郑少鹏
 * @desc MenuItem 配套元件
 */
class MenuItemKit {
    companion object {
        /**
         * 显示
         *
         * @param menuItem 菜单条目
         */
        fun show(menuItem: MenuItem) {
            menuItem.apply {
                isVisible = true
                isEnabled = true
            }
        }

        /**
         * 隐藏
         *
         * @param menuItem 菜单条目
         */
        fun hide(menuItem: MenuItem) {
            menuItem.apply {
                isVisible = false
                isEnabled = false
            }
        }
    }
}