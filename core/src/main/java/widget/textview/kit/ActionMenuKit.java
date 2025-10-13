package widget.textview.kit;

import androidx.annotation.NonNull;

import java.util.List;

import widget.textview.menu.ActionMenu;

/**
 * Created on 2025/10/8.
 *
 * @author 郑少鹏
 * @desc 动作菜单配套原件
 */
public class ActionMenuKit {
    /**
     * 创动作菜单
     *
     * @param actionMenu              动作菜单
     * @param actionMenuItemTitleList 动作菜单条目标题集
     */
    public void createActionMenu(@NonNull ActionMenu actionMenu, List<String> actionMenuItemTitleList) {
        actionMenu.setActionMenuItemTextColor(0xffffffff);
        actionMenu.setActionMenuBackgroundColor(0xff666666);
        actionMenu.addActionMenuItem(actionMenuItemTitleList);
    }
}