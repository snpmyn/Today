package widget.textview.align;

/**
 * Created on 2022/3/21
 *
 * @author zsp
 * @desc 动作菜单回调
 */
public interface ActionMenuCallBack {
    /**
     * 创动作菜单
     *
     * @param actionMenu 动作菜单
     * @return false 留默动作菜单、true 移默动作菜单
     */
    boolean onCreateActionMenu(ActionMenu actionMenu);

    /**
     * 动作菜单条目点击
     *
     * @param item            条目
     * @param selectedContent 已选内容
     */
    void onActionMenuItemClick(String item, String selectedContent);
}