package com.zsp.today.module.zhilin.bean;

import com.zsp.today.R;

/**
 * Created on 2025/8/19.
 *
 * @author 郑少鹏
 * @desc 知林菜单枚举
 */
public enum ZhiLinMenuEnum {
    /**
     * TabLayout
     * <p>
     * 标签布局
     */
    TAB_LAYOUT(1, R.drawable.ic_widget_cos_24dp, "TabLayout", true),
    /**
     * CardView
     * <p>
     * 卡片视图
     */
    CARD_VIEW(2, R.drawable.ic_widget_cos_24dp, "CardView", true),
    /**
     * SearchView
     * <p>
     * 搜索视图
     */
    SEARCH_VIEW(3, R.drawable.ic_widget_cos_24dp, "SearchView", true),
    /**
     * 安卓
     */
    ANDROID(4, R.drawable.ic_widget_cos_24dp, "安卓", true),
    /**
     * 面试
     */
    INTERVIEW(5, R.drawable.ic_widget_cos_24dp, "面试", true);
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
     * 菜单显示
     */
    private final Boolean menuShow;

    /**
     * constructor
     *
     * @param menuId        菜单 ID
     * @param menuIconResId 菜单图标资源 ID
     * @param menuName      菜单名称
     * @param menuShow      菜单显示
     */
    ZhiLinMenuEnum(int menuId, int menuIconResId, String menuName, Boolean menuShow) {
        this.menuId = menuId;
        this.menuIconResId = menuIconResId;
        this.menuName = menuName;
        this.menuShow = menuShow;
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

    public Boolean getMenuShow() {
        return menuShow;
    }
}