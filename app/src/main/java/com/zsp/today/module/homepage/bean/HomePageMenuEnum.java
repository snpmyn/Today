package com.zsp.today.module.homepage.bean;

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
    ACCOUNT(1, R.drawable.ic_home_page_menu_account_cos_24dp, "HomePageMenuAccount@One", "账目", true),
    /**
     * 险情
     */
    DANGEROUS(2, R.drawable.ic_home_page_menu_dangerous_cos_24dp, "HomePageMenuDangerous@One", "险情", true),
    /**
     * 心盒
     */
    HEART_BOX(3, R.drawable.ic_home_page_menu_heart_box_cos_24dp, "HomePageMenuHeartBox@One", "心盒", true),
    /**
     * 归心
     */
    HOME_COME(4, R.drawable.ic_home_page_menu_home_come_cos_24dp, "HomePageMenuHomeCome@One", "归心", true),
    /**
     * 灵方
     */
    LING_FANG(5, R.drawable.ic_home_page_menu_ling_fang_cos_24dp, "HomePageMenuLingFang@One", "灵方", true),
    /**
     * 探测
     */
    DETECT(6, R.drawable.ic_home_page_menu_detect_cos_24dp, "HomePageMenuDetect@One", "探测", true),
    /**
     * 诗词
     */
    POETRY(7, R.drawable.ic_home_page_menu_poetry_cos_24dp, "HomePageMenuPoetry@One", "诗词", true),
    /**
     * 网络
     */
    NETWORK(8, R.drawable.ic_home_page_menu_network_cos_24dp, "HomePageMenuNetwork@One", "网络", true),
    /**
     * 知林
     */
    ZHI_LIN(9, R.drawable.ic_home_page_menu_zhi_lin_cos_24dp, "HomePageMenuZhiLin@One", "知林", true);
    /**
     * 菜单 ID
     */
    private final int menuId;
    /**
     * 菜单图标资源 ID
     */
    private final int menuIconResId;
    /**
     * 菜单图标资源名称
     */
    private final String menuIconResName;
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
     * @param menuId          菜单 ID
     * @param menuIconResId   菜单图标资源 ID
     * @param menuIconResName 菜单图标资源名称
     * @param menuName        菜单名称
     * @param menuShow        菜单显示
     */
    HomePageMenuEnum(int menuId, int menuIconResId, String menuIconResName, String menuName, Boolean menuShow) {
        this.menuId = menuId;
        this.menuIconResId = menuIconResId;
        this.menuIconResName = menuIconResName;
        this.menuName = menuName;
        this.menuShow = menuShow;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getMenuIconResId() {
        return menuIconResId;
    }

    public String getMenuIconResName() {
        return menuIconResName;
    }

    public String getMenuName() {
        return menuName;
    }

    public Boolean getMenuShow() {
        return menuShow;
    }
}