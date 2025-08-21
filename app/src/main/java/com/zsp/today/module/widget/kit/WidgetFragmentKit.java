package com.zsp.today.module.widget.kit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.widget.bean.WidgetMenuEnum;
import com.zsp.today.module.widget.tablayout.TabLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import util.intent.IntentJump;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;
import widget.toast.ToastKit;

/**
 * Created on 2025/8/19.
 *
 * @author 郑少鹏
 * @desc 组件碎片配套原件
 */
public class WidgetFragmentKit {
    /**
     * 展示
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param spanCount         跨距数
     * @param spacing           间距
     * @param totalMargin       总外边距
     * @param statusManager     状态管理器
     */
    public void display(AppCompatActivity appCompatActivity, RecyclerView recyclerView, int spanCount, int spacing, int totalMargin, StatusManager statusManager) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null);
        // 获取组件菜单图标资源 ID 集
        WidgetMenuEnum[] widgetMenuEnums = WidgetMenuEnum.values();
        // 获取菜单集
        List<MenuBean> menuBeanList = new ArrayList<>(widgetMenuEnums.length);
        for (WidgetMenuEnum widgetMenuEnum : widgetMenuEnums) {
            if (widgetMenuEnum.getMenuShow()) {
                menuBeanList.add(new MenuBean(widgetMenuEnum.getMenuId(), widgetMenuEnum.getMenuIconResId(), widgetMenuEnum.getMenuName()));
            }
        }
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, menuBeanList);
        // 菜单适配器配套元件
        MenuAdapterKit menuAdapterKit = new MenuAdapterKit();
        menuAdapterKit.display(appCompatActivity, recyclerView, menuBeanList, spanCount, spacing, totalMargin, false, (view, menuBean) -> distribute(appCompatActivity, menuBean.getMenuId()));
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param menuId            菜单 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, int menuId) {
        switch (menuId) {
            // 标签布局
            case 1:
                IntentJump.getInstance().jump(null, appCompatActivity, false, TabLayoutActivity.class);
                break;
            // 卡片视图
            case 2:
                // 搜索视图
            case 3:
                ToastKit.showShort(appCompatActivity.getString(R.string.waitForImplementation));
                break;
            default:
                break;
        }
    }
}