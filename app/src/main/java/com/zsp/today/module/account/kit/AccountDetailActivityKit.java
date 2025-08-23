package com.zsp.today.module.account.kit;

import android.content.Intent;
import android.graphics.drawable.InsetDrawable;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.kit.BackupKit;
import com.zsp.today.module.account.AddAccountActivity;
import com.zsp.today.module.account.adapter.AccountDetailAdapter;
import com.zsp.today.module.account.bean.AccountDetailBean;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.value.AccountCondition;
import com.zsp.today.value.AccountConstant;
import com.zsp.today.value.RxBusConstant;
import com.zsp.today.widget.mpandroidchart.piechart.HalfPieChartKit;

import widget.popupmenu.PopupMenuKit;
import widget.status.kit.StatusManagerKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.data.BigDecimalUtils;
import util.intent.IntentJump;
import util.intent.IntentVerify;
import util.rxbus.RxBus;
import widget.dialog.materialalertdialog.MyMaterialAlertDialogBuilder;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;
import widget.status.manager.StatusManager;

/**
 * Created on 2021/1/4
 *
 * @author zsp
 * @desc 账目详情页配套元件
 */
public class AccountDetailActivityKit {
    /**
     * 添加账目
     *
     * @param appCompatActivity 活动
     */
    public void addAccount(@NonNull AppCompatActivity appCompatActivity) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_SECOND_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        Intent intent = new Intent();
        intent.putExtra(AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN, accountTransferBean);
        IntentJump.getInstance().jumpWithAnimation(intent, appCompatActivity, false, AddAccountActivity.class, 0, 0);
    }

    /**
     * 展示账目
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param date              日期
     * @param statusManager     状态管理器
     */
    public void displayAccount(AppCompatActivity appCompatActivity, RecyclerView recyclerView, String date, StatusManager statusManager) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null);
        // 数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(), date);
        List<AccountDetailBean> accountDetailBeanList = AccountBasicKit.getInstance().transformAccountDataBaseTableToAccountDetailBean(accountDataBaseTableList);
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        recyclerViewConfigure.linearVerticalLayout(true, 48, true, true, false);
        // 适配器
        AccountDetailAdapter accountDetailAdapter = getAccountDetailAdapter(appCompatActivity, statusManager, accountDetailBeanList);
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, accountDetailBeanList);
        // 展示
        RecyclerViewDisplayController.display(recyclerView, accountDetailAdapter);
    }

    /**
     * 获取账目详情适配器
     *
     * @param appCompatActivity     活动
     * @param statusManager         状态管理器
     * @param accountDetailBeanList 账目详情数据集
     * @return 账目详情适配器
     */
    @NonNull
    private AccountDetailAdapter getAccountDetailAdapter(AppCompatActivity appCompatActivity, StatusManager statusManager, List<AccountDetailBean> accountDetailBeanList) {
        AccountDetailAdapter accountDetailAdapter = new AccountDetailAdapter(appCompatActivity);
        accountDetailAdapter.setAccountDetailData(accountDetailBeanList);
        accountDetailAdapter.setOnRecyclerViewOnItemLongClickListener(new OnRecyclerViewOnItemLongClickListener() {
            @Override
            public <T> void onItemLongClick(View view, int position, T t) {
                popupMenu(appCompatActivity, view, R.menu.account_detail_activity_popup_menu, accountDetailAdapter, accountDetailBeanList, position, (AccountDetailBean) t, statusManager);
            }
        });
        return accountDetailAdapter;
    }

    /**
     * 弹出式菜单
     *
     * @param appCompatActivity     活动
     * @param view                  视图
     * @param menuResId             菜单资源 ID
     * @param accountDetailAdapter  账目详情适配器
     * @param accountDetailBeanList 账目详情数据集合
     * @param position              位置
     * @param accountDetailBean     账目详情
     * @param statusManager         状态管理器
     */
    private void popupMenu(AppCompatActivity appCompatActivity, View view, @MenuRes int menuResId, AccountDetailAdapter accountDetailAdapter, List<AccountDetailBean> accountDetailBeanList, int position, AccountDetailBean accountDetailBean, StatusManager statusManager) {
        PopupMenuKit.getInstance().popupMenu(appCompatActivity, view, menuResId, new PopupMenuKit.PopupMenuKitListener() {
            /**
             * 菜单条目
             * @param menuItem 菜单条目
             */
            @Override
            public void menuItem(MenuItem menuItem) {
                if (null != menuItem.getIcon()) {
                    if (menuItem.getItemId() == R.id.accountDetailActivityPopupMenuModify) {
                        menuItem.getIcon().setTint(appCompatActivity.getColor(com.zsp.core.R.color.color_FFBA57));
                    } else if (menuItem.getItemId() == R.id.accountDetailActivityPopupMenuDelete) {
                        menuItem.getIcon().setTint(appCompatActivity.getColor(com.zsp.core.R.color.color_EA6464));
                    }
                    int iconMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, appCompatActivity.getResources().getDisplayMetrics());
                    menuItem.setIcon(new InsetDrawable(menuItem.getIcon(), iconMarginPx, 0, iconMarginPx, 0));
                }
            }

            /**
             * 菜单条目短点
             * @param popupMenu 弹出式菜单
             * @param menuItem  菜单条目
             */
            @Override
            public void onMenuItemClick(PopupMenu popupMenu, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.accountDetailActivityPopupMenuModify) {
                    popupMenu.dismiss();
                    modifyAccount(appCompatActivity, accountDetailBean);
                } else if (menuItem.getItemId() == R.id.accountDetailActivityPopupMenuDelete) {
                    popupMenu.dismiss();
                    deleteAccount(appCompatActivity, accountDetailAdapter, accountDetailBeanList, position, accountDetailBean, statusManager);
                }
            }
        });
    }

    /**
     * 修改账目
     *
     * @param appCompatActivity 活动
     * @param accountDetailBean 账目详情
     */
    private void modifyAccount(AppCompatActivity appCompatActivity, @NonNull AccountDetailBean accountDetailBean) {
        AccountTransferBean accountTransferBean = new AccountTransferBean(accountDetailBean.getDate(), accountDetailBean.getCategory(), accountDetailBean.getAmount());
        Intent intent = new Intent();
        intent.putExtra(AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN, accountTransferBean);
        IntentJump.getInstance().jumpWithAnimation(intent, appCompatActivity, false, AddAccountActivity.class, 0, 0);
    }

    /**
     * 删除账目
     *
     * @param appCompatActivity     活动
     * @param accountDetailAdapter  账目详情适配器
     * @param accountDetailBeanList 账目详情数据集合
     * @param position              位置
     * @param accountDetailBean     账目详情
     * @param statusManager         状态管理器
     */
    private void deleteAccount(AppCompatActivity appCompatActivity, AccountDetailAdapter accountDetailAdapter, List<AccountDetailBean> accountDetailBeanList, int position, AccountDetailBean accountDetailBean, StatusManager statusManager) {
        new MyMaterialAlertDialogBuilder(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(R.string.wantToDeleteThisAccount).setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            LitePalKit.getInstance().multiDelete(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY_AND_AMOUNT, App.getAppInstance().getPhoneNumber(), accountDetailBean.getDate(), accountDetailBean.getCategory(), BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(accountDetailBean.getAmount())));
            RecyclerViewDisplayController.deleteDynamic(accountDetailAdapter, position, accountDetailBeanList);
            // 状态判断
            StatusManagerKit.statusJudge(statusManager, false, accountDetailBeanList);
            RxBus.get().post(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT, RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE);
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
        }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
    }

    /**
     * 每日分析
     *
     * @param appCompatActivity 活动
     * @param pieChart          半圆饼图表
     * @param date              日期
     */
    public void everydayAnalysis(AppCompatActivity appCompatActivity, @NonNull PieChart pieChart, String date) {
        // 边距
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pieChart.getLayoutParams();
        if (layoutParams.topMargin == 0) {
            layoutParams.setMargins(0, 50, 0, -(pieChart.getHeight() / 2));
            pieChart.setLayoutParams(layoutParams);
        }
        // 数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(), date);
        ArrayList<PieEntry> pieEntries = new ArrayList<>(accountDataBaseTableList.size());
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            pieEntries.add(new PieEntry(accountDataBaseTable.getAmount().floatValue() / new BigDecimal(AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList)).floatValue(), accountDataBaseTable.getCategory()));
        }
        // 显示
        HalfPieChartKit halfPieChartKit = new HalfPieChartKit();
        halfPieChartKit.execute(appCompatActivity, pieChart, date, pieEntries, appCompatActivity.getString(R.string.yuan), accountDataBaseTableList.isEmpty(), appCompatActivity.getString(R.string.noAccountDataAvailable), com.zsp.core.R.color.basic);
    }
}