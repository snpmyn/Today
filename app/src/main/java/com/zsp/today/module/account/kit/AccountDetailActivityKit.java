package com.zsp.today.module.account.kit;

import android.content.Intent;
import android.graphics.drawable.InsetDrawable;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.card.MaterialCardView;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.backup.BackupKit;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.AddAccountActivity;
import com.zsp.today.module.account.adapter.AccountDetailAdapter;
import com.zsp.today.module.account.bean.AccountDetailBean;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.module.account.value.AccountCondition;
import com.zsp.today.module.account.value.AccountConstant;
import com.zsp.today.widget.mpandroidchart.piechart.HalfPieChartKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.data.BigDecimalUtils;
import util.data.DoubleUtils;
import util.intent.IntentJump;
import util.intent.IntentVerify;
import util.list.ListUtils;
import util.rxbus.RxBus;
import util.theme.ThemeUtils;
import util.view.ViewUtils;
import widget.dialog.materialalertdialog.DoubleConfirmationMaterialAlertDialogKit;
import widget.floatingactionbutton.DraggableFloatingActionButton;
import widget.materialcontainertransform.MaterialContainerTransformKit;
import widget.popupmenu.PopupMenuKit;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;
import widget.view.AccountLineView;
import widget.view.NutritionChartView;
import widget.view.kit.AccountLineViewKit;
import widget.view.kit.NutritionChartViewKit;

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
        recyclerViewConfigure.linearVerticalLayout(true, 12, true, true, false);
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
                        menuItem.getIcon().setTint(appCompatActivity.getColor(com.zsp.core.R.color.color_FFEE6002));
                    } else if (menuItem.getItemId() == R.id.accountDetailActivityPopupMenuDelete) {
                        menuItem.getIcon().setTint(appCompatActivity.getColor(com.zsp.core.R.color.color_FFF44336));
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
        DoubleConfirmationMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(com.zsp.core.R.string.hint), appCompatActivity.getString(R.string.wantToDeleteAccount), appCompatActivity.getString(R.string.yes), appCompatActivity.getString(R.string.wait), appCompatActivity.getString(R.string.hintAgain), appCompatActivity.getString(R.string.deletionCannotBeRestored), appCompatActivity.getString(com.zsp.core.R.string.delete), appCompatActivity.getString(R.string.wait), dialog -> {
            dialog.dismiss();
            LitePalKit.getInstance().multiDelete(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY_AND_AMOUNT, App.getAppInstance().getPhoneNumber(), accountDetailBean.getDate(), accountDetailBean.getCategory(), BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(accountDetailBean.getAmount())));
            RecyclerViewDisplayController.deleteDynamic(accountDetailAdapter, position, accountDetailBeanList);
            // 状态判断
            StatusManagerKit.statusJudge(statusManager, false, accountDetailBeanList);
            RxBus.get().post(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT, RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE);
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
        });
    }

    /**
     * 每日分析
     *
     * @param appCompatActivity             活动
     * @param relativeLayout                RelativeLayout
     * @param materialCardView              材料卡片视图
     * @param linearLayout                  LinearLayout
     * @param nutritionChartView            营养图表视图
     * @param pieChart                      半圆饼图表
     * @param accountLineView               账目线段视图
     * @param draggableFloatingActionButton 可拖动浮动操作按钮
     * @param date                          日期
     * @param clickMenu                     点击菜单否
     */
    public void everydayAnalysis(AppCompatActivity appCompatActivity, RelativeLayout relativeLayout, MaterialCardView materialCardView, LinearLayout linearLayout, NutritionChartView nutritionChartView, PieChart pieChart, AccountLineView accountLineView, DraggableFloatingActionButton draggableFloatingActionButton, String date, boolean clickMenu) {
        ViewUtils.hideView(draggableFloatingActionButton, View.GONE);
        MaterialContainerTransformKit.getInstance().showEndView(appCompatActivity, relativeLayout, draggableFloatingActionButton, materialCardView, false);
        // 数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(), date);
        if (clickMenu) {
            ViewUtils.showView(accountLineView);
            ViewUtils.hideView(linearLayout, View.GONE);
            // 账目线段视图
            List<AccountLineView.AccountItem> accountItemList = new ArrayList<>(accountDataBaseTableList.size());
            for (AccountDataBaseTable accountDataBaseTable : AccountBasicKit.getInstance().sortByAmount(accountDataBaseTableList, false)) {
                accountItemList.add(new AccountLineView.AccountItem(accountDataBaseTable.getCategory(), DoubleUtils.doubleToFloat(accountDataBaseTable.getAmount()), 0));
            }
            AccountLineViewKit.execute(accountLineView, accountItemList);
        } else {
            ViewUtils.showView(linearLayout);
            ViewUtils.hideView(accountLineView, View.GONE);
            // 半圆饼图表
            ArrayList<PieEntry> pieEntries = new ArrayList<>(accountDataBaseTableList.size());
            for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
                pieEntries.add(new PieEntry(accountDataBaseTable.getAmount().floatValue() / new BigDecimal(AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList)).floatValue(), accountDataBaseTable.getCategory()));
            }
            HalfPieChartKit halfPieChartKit = new HalfPieChartKit();
            halfPieChartKit.execute(appCompatActivity, pieChart, date, pieEntries, appCompatActivity.getString(R.string.yuan), ListUtils.listIsEmpty(accountDataBaseTableList), appCompatActivity.getString(R.string.noAccountDataAvailable), ThemeUtils.getColorPrimaryColorFromAttrResIdWithTypedArray(appCompatActivity));
            // 营养图表视图
            List<NutritionChartView.NutritionPart> nutritionPartList = new ArrayList<>(accountDataBaseTableList.size());
            for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
                nutritionPartList.add(new NutritionChartView.NutritionPart(DoubleUtils.doubleToFloat(accountDataBaseTable.getAmount()), 0, accountDataBaseTable.getCategory()));
            }
            NutritionChartViewKit.execute(appCompatActivity, nutritionChartView, nutritionPartList, appCompatActivity.getString(R.string.yuan), appCompatActivity.getString(R.string.todaySpent));
        }
    }
}