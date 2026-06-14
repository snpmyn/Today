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
    TAB_LAYOUT(ZhiLinMenuAction.TabLayout.INSTANCE, R.drawable.ic_widget_cos_24dp, "TabLayout", true),
    /**
     * RxJava
     * <p>
     * 响应式异步框架
     */
    RX_JAVA(ZhiLinMenuAction.RxJava.INSTANCE, R.drawable.ic_widget_cos_24dp, "RxJava", true),
    /**
     * COMPOSE
     */
    COMPOSE(ZhiLinMenuAction.Compose.INSTANCE, R.drawable.ic_widget_cos_24dp, "COMPOSE", true),
    /**
     * 面试
     */
    INTERVIEW(ZhiLinMenuAction.Interview.INSTANCE, R.drawable.ic_widget_cos_24dp, "面试", true),
    /**
     * 自定义视图
     */
    CUSTOM_VIEW(ZhiLinMenuAction.CustomView.INSTANCE, R.drawable.ic_widget_cos_24dp, "自定义视图", true),
    /**
     * MVP
     */
    MVP(ZhiLinMenuAction.Mvp.INSTANCE, R.drawable.ic_widget_cos_24dp, "MVP", true),
    /**
     * 历史
     */
    HISTORY(ZhiLinMenuAction.History.INSTANCE, R.drawable.ic_widget_cos_24dp, "历史", true),
    /**
     * 联动
     */
    COLLABORATION(ZhiLinMenuAction.COLLABORATION.INSTANCE, R.drawable.ic_widget_cos_24dp, "联动", true),
    /**
     * 悬浮
     */
    FLOATING(ZhiLinMenuAction.FLOATING.INSTANCE, R.drawable.ic_widget_cos_24dp, "悬浮", true),
    /**
     * 浮层
     */
    OVERLAY(ZhiLinMenuAction.OVERLAY.INSTANCE, R.drawable.ic_widget_cos_24dp, "浮层", true),
    /**
     * 轮播
     */
    CAROUSEL(ZhiLinMenuAction.CAROUSEL.INSTANCE, R.drawable.ic_widget_cos_24dp, "轮播", true);
    /**
     * 菜单 ID
     */
    private final ZhiLinMenuAction zhiLinMenuAction;
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
     * @param zhiLinMenuAction 知林菜单动作
     * @param menuIconResId    菜单图标资源 ID
     * @param menuName         菜单名称
     * @param menuShow         菜单显示
     */
    ZhiLinMenuEnum(ZhiLinMenuAction zhiLinMenuAction, int menuIconResId, String menuName, Boolean menuShow) {
        this.zhiLinMenuAction = zhiLinMenuAction;
        this.menuIconResId = menuIconResId;
        this.menuName = menuName;
        this.menuShow = menuShow;
    }

    public ZhiLinMenuAction getZhiLinMenuAction() {
        return zhiLinMenuAction;
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