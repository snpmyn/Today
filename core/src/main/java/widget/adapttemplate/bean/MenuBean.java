package widget.adapttemplate.bean;

/**
 * Created on 2021/3/29
 *
 * @author zsp
 * @desc 菜单
 */
public class MenuBean {
    /**
     * 菜单 ID
     */
    private final int menuId;
    /**
     * 菜单图标资源 ID
     */
    private final int menuIconResId;
    /**
     * 菜单名称
     */
    private final String menuName;

    /**
     * constructor
     *
     * @param menuId        菜单 ID
     * @param menuIconResId 菜单图标资源 ID
     * @param menuName      菜单名称
     */
    public MenuBean(int menuId, int menuIconResId, String menuName) {
        this.menuId = menuId;
        this.menuIconResId = menuIconResId;
        this.menuName = menuName;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getMenuIconResId() {
        return menuIconResId;
    }

    public String getMenuName() {
        return menuName;
    }
}
