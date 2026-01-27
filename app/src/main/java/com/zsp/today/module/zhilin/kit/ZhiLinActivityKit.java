package com.zsp.today.module.zhilin.kit;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.zhilin.android.AndroidActivity;
import com.zsp.today.module.zhilin.bean.ZhiLinMenuEnum;
import com.zsp.today.module.zhilin.interview.InterviewActivity;
import com.zsp.today.module.zhilin.tablayout.TabLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;
import widget.toast.ToastKit;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2025/9/28.
 *
 * @author 郑少鹏
 * @desc 知林页配套原件
 */
public class ZhiLinActivityKit {
    /**
     * 展示
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param spanCount         跨距数
     * @param space             间距
     * @param totalMargin       总外边距
     * @param statusManager     状态管理器
     */
    public void display(AppCompatActivity appCompatActivity, RecyclerView recyclerView, int spanCount, int space, int totalMargin, StatusManager statusManager) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null);
        // 获取组件菜单图标资源 ID 集
        ZhiLinMenuEnum[] zhiLinMenuEnums = ZhiLinMenuEnum.values();
        // 获取菜单集
        List<MenuBean> menuBeanList = new ArrayList<>(zhiLinMenuEnums.length);
        for (ZhiLinMenuEnum zhiLinMenuEnum : zhiLinMenuEnums) {
            if (zhiLinMenuEnum.getMenuShow()) {
                menuBeanList.add(new MenuBean(zhiLinMenuEnum.getMenuId(), zhiLinMenuEnum.getMenuIconResId(), zhiLinMenuEnum.getMenuName()));
            }
        }
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, menuBeanList);
        // 菜单适配器配套元件
        MenuAdapterKit menuAdapterKit = new MenuAdapterKit();
        menuAdapterKit.display(appCompatActivity, recyclerView, menuBeanList, spanCount, space, totalMargin, false, (view, menuBean) -> distribute(appCompatActivity, view, menuBean.getMenuId()));
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param view              视图
     * @param menuId            菜单 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, View view, int menuId) {
        switch (menuId) {
            // 标签布局
            case 1:
                Intent fromThisToTabLayoutActivityIntent = new Intent(appCompatActivity, TabLayoutActivity.class);
                TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, fromThisToTabLayoutActivityIntent, false);
                break;
            // 卡片视图
            case 2:
                // 搜索视图
            case 3:
                ToastKit.showShort(appCompatActivity.getString(R.string.waitForImplementation));
                break;
            // 安卓
            case 4:
                Intent fromThisToAndroidActivityIntent = new Intent(appCompatActivity, AndroidActivity.class);
                TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, fromThisToAndroidActivityIntent, false);
                break;
            case 5:
                Intent fromThisToInterviewActivityIntent = new Intent(appCompatActivity, InterviewActivity.class);
                TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, fromThisToInterviewActivityIntent, false);
                break;
            default:
                break;
        }
    }
}