package com.zsp.today.module.function.kit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.application.App;
import com.zsp.today.basic.kit.BackupKit;
import com.zsp.today.module.function.database.FunctionDataBaseTable;
import com.zsp.today.module.function.value.FunctionCondition;
import com.zsp.today.basic.value.RxBusConstant;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.rxbus.RxBus;
import widget.adapttemplate.bean.FunctionBean;
import widget.adapttemplate.kit.FunctionAdapterKit;

/**
 * Created on 2021/12/2
 *
 * @author zsp
 * @desc 功能页配套元件
 */
public class FunctionActivityKit {
    /**
     * 展示
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     */
    public void display(AppCompatActivity appCompatActivity, RecyclerView recyclerView) {
        // 数据
        List<FunctionDataBaseTable> functionDataBaseTableList = LitePalKit.getInstance().queryByWhere(FunctionDataBaseTable.class, FunctionCondition.FUNCTION_PHONE_NUMBER, App.getAppInstance().getPhoneNumber());
        List<FunctionBean> functionBeanList = new ArrayList<>(functionDataBaseTableList.size());
        for (FunctionDataBaseTable functionDataBaseTable : functionDataBaseTableList) {
            functionBeanList.add(new FunctionBean(functionDataBaseTable.getFunctionId(), functionDataBaseTable.getFunctionName(), functionDataBaseTable.getFunctionShow()));
        }
        // 功能适配器配套元件
        FunctionAdapterKit functionAdapterKit = new FunctionAdapterKit();
        functionAdapterKit.display(appCompatActivity, recyclerView, functionBeanList, 3, 48, 192, new FunctionAdapterKit.FunctionAdapterKitInterface() {
            @Override
            public void onItemClick(FunctionBean functionBean) {
                // 更新
                update(appCompatActivity, functionBean);
            }
        });
    }

    /**
     * 更新
     *
     * @param appCompatActivity 活动
     * @param functionBean      功能数据
     */
    private void update(AppCompatActivity appCompatActivity, @NonNull FunctionBean functionBean) {
        // 创建待更新对象
        FunctionDataBaseTable functionDataBaseTableUpdate = new FunctionDataBaseTable();
        functionDataBaseTableUpdate.setFunctionShow(functionBean.isFunctionShow());
        // 获取被更新对象
        List<FunctionDataBaseTable> functionDataBaseTableList = LitePalKit.getInstance().queryByWhere(FunctionDataBaseTable.class, FunctionCondition.FUNCTION_PHONE_NUMBER_AND_FUNCTION_ID, App.getAppInstance().getPhoneNumber(), String.valueOf(functionBean.getFunctionId()));
        FunctionDataBaseTable functionDataBaseTableOld = functionDataBaseTableList.get(0);
        // 单个更新
        if (LitePalKit.getInstance().singleUpdate(functionDataBaseTableUpdate, functionDataBaseTableOld.getBaseObjectId()) != 0) {
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, FunctionDataBaseTable.class, null);
        }
        // 刷新菜单
        RxBus.get().post(RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU, RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU_CODE);
    }
}