package com.zsp.today.module.homepage.bean;

import com.zsp.today.BuildConfig;
import com.zsp.today.R;

/**
 * Created on 2022/2/8
 *
 * @author zsp
 * @desc 主页菜单枚举
 */
public enum HomePageMenuEnum {
    /**
     * 账目
     */
    ACCOUNT(1, R.drawable.ic_account_basic_24dp, "账目", true),
    /**
     * 险情
     */
    DANGEROUS(2, R.drawable.ic_dangerous_basic_24dp, "险情", true),
    /**
     * 心盒
     */
    HEART_BOX(3, R.drawable.ic_heart_box_basic_24dp, "心盒", true),
    /**
     * 组件
     */
    WIDGET(4, R.drawable.ic_widget_basic_24dp, "组件", BuildConfig.DEBUG);
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
    HomePageMenuEnum(int menuId, int menuIconResId, String menuName, Boolean menuShow) {
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