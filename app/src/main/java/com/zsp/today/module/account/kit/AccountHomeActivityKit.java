package com.zsp.today.module.account.kit;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.kit.BackupKit;
import com.zsp.today.module.account.AccountAnalysisActivity;
import com.zsp.today.module.account.AccountHomeActivity;
import com.zsp.today.module.account.AccountSecondActivity;
import com.zsp.today.module.account.adapter.AccountMonthListAdapter;
import com.zsp.today.module.account.bean.AccountMonthListBean;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.value.AccountCondition;
import com.zsp.today.value.AccountConstant;
import com.zsp.today.value.Folder;
import com.zsp.today.widget.excel.ExcelKit;

import widget.status.kit.StatusManagerKit;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.data.StringUtils;
import util.datetime.DateUtils;
import util.intent.IntentJump;
import widget.dialog.bottomsheetdialog.MyBottomSheetDialog;
import widget.toast.ToastKit;
import util.view.ViewUtils;
import widget.dialog.materialalertdialog.MyMaterialAlertDialogBuilder;
import widget.dialog.materialalertdialog.SingleChooseMaterialAlertDialogKit;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;
import widget.status.manager.StatusManager;
import widget.tbs.kit.TbsKit;
import widget.tbs.value.TbsEnum;
import widget.textview.DrawableCenterTextView;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2021/6/13 0013
 *
 * @author zsp
 * @desp 账目主页配套元件
 */
public class AccountHomeActivityKit {
    /**
     * 判断展示账目
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param statusManager     状态管理器
     * @param appointYear       指定年
     */
    public void judgeDisplayAccount(AppCompatActivity appCompatActivity, RecyclerView recyclerView, @NonNull StatusManager statusManager, String appointYear) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null);
        displayAccount(appCompatActivity, recyclerView, AccountMonthKit.getInstance().getAccountMonthListBeanListByMonthRemoveDuplicationWithSort(appointYear, true), statusManager);
    }

    /**
     * 选择年并展示账目
     *
     * @param appCompatActivity            活动
     * @param materialToolbar              MaterialToolbar
     * @param recyclerView                 控件
     * @param statusManager                状态管理器
     * @param extendedFloatingActionButton ExtendedFloatingActionButton
     */
    public void chooseYearAndDisplayAccount(AppCompatActivity appCompatActivity, MaterialToolbar materialToolbar, RecyclerView recyclerView, StatusManager statusManager, ExtendedFloatingActionButton extendedFloatingActionButton) {
        SingleChooseMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getResources().getStringArray(R.array.year), value -> {
            materialToolbar.setTitle(value);
            if (TextUtils.equals(value, DateUtils.getCurrentTimeYear())) {
                ViewUtils.hideView(extendedFloatingActionButton, View.GONE);
            } else {
                AccountHomeActivity.appointYear = value;
                ViewUtils.showView(extendedFloatingActionButton);
            }
            List<AccountMonthListBean> accountMonthListBeanList = AccountMonthKit.getInstance().getAccountMonthListBeanListByMonthRemoveDuplicationWithSort(value, true);
            // 状态判断
            StatusManagerKit.statusJudge(statusManager, false, accountMonthListBeanList);
            // 展示账目
            displayAccount(appCompatActivity, recyclerView, accountMonthListBeanList, statusManager);
        });
    }

    /**
     * 展示账目
     *
     * @param appCompatActivity        活动
     * @param recyclerView             控件
     * @param accountMonthListBeanList 数据集合
     * @param statusManager            状态管理器
     */
    public void displayAccount(AppCompatActivity appCompatActivity, RecyclerView recyclerView, List<AccountMonthListBean> accountMonthListBeanList, StatusManager statusManager) {
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        recyclerViewConfigure.linearVerticalLayout(true, 48, true, true, false);
        // 适配器
        AccountMonthListAdapter accountMonthListAdapter = new AccountMonthListAdapter(appCompatActivity);
        accountMonthListAdapter.setAccountMonthListData(accountMonthListBeanList);
        accountMonthListAdapter.setOnRecyclerViewOnItemClickListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                AccountMonthListBean accountMonthListBean = (AccountMonthListBean) t;
                AccountTransferBean accountTransferBean = new AccountTransferBean(accountMonthListBean.getYear(), accountMonthListBean.getMonth());
                Intent intent = new Intent(appCompatActivity, AccountSecondActivity.class);
                intent.putExtra(AccountConstant.ACCOUNT_HOME_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN, accountTransferBean);
                TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, intent);
            }
        });
        accountMonthListAdapter.setOnRecyclerViewOnItemLongClickListener(new OnRecyclerViewOnItemLongClickListener() {
            @Override
            public <T> void onItemLongClick(View view, int position, T t) {
                new MyMaterialAlertDialogBuilder(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(R.string.wantToDeleteTheAccountOfTheDay).setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    AccountMonthListBean accountMonthListBean = (AccountMonthListBean) t;
                    LitePalKit.getInstance().multiDelete(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_YEAR_AND_MONTH, App.getAppInstance().getPhoneNumber(false, null), accountMonthListBean.getYear(), accountMonthListBean.getMonth());
                    RecyclerViewDisplayController.deleteDynamic(accountMonthListAdapter, position, accountMonthListBeanList);
                    // 状态判断
                    StatusManagerKit.statusJudge(statusManager, false, accountMonthListBeanList);
                    // 备份
                    BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
                }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
            }
        });
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, accountMonthListBeanList);
        // 展示
        RecyclerViewDisplayController.display(recyclerView, accountMonthListAdapter);
    }

    /**
     * BottomSheetDialog
     *
     * @param appCompatActivity 活动
     */
    public void bottomSheetDialog(AppCompatActivity appCompatActivity) {
        // MyBottomSheetDialog
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(appCompatActivity, R.layout.account_home_activity_bottom_sheet_dialog);
        View bottomSheetDialogView = myBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheetDialogView != null;
        // 账目分析
        DrawableCenterTextView accountHomeActivityBottomSheetDialogDctvAccountAnalysis = bottomSheetDialogView.findViewById(R.id.accountHomeActivityBottomSheetDialogDctvAccountAnalysis);
        accountHomeActivityBottomSheetDialogDctvAccountAnalysis.setOnClickListener(v -> {
            myBottomSheetDialog.dismiss();
            IntentJump.getInstance().jump(null, appCompatActivity, false, AccountAnalysisActivity.class);
        });
        // 导出账单
        DrawableCenterTextView accountHomeActivityBottomSheetDialogDctvExportAccount = bottomSheetDialogView.findViewById(R.id.accountHomeActivityBottomSheetDialogDctvExportAccount);
        accountHomeActivityBottomSheetDialogDctvExportAccount.setOnClickListener(v -> {
            myBottomSheetDialog.dismiss();
            exportAccount(appCompatActivity);
        });
        myBottomSheetDialog.show();
    }

    /**
     * 导出账单
     *
     * @param appCompatActivity 活动
     */
    public void exportAccount(AppCompatActivity appCompatActivity) {
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhereAndOrderAndSelect(AccountDataBaseTable.class, new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER, App.getAppInstance().getPhoneNumber(false, null)}, "date", false, "date", "category", "amount");
        if (accountDataBaseTableList.isEmpty()) {
            ToastKit.showShort(appCompatActivity.getString(R.string.noAccountDataAvailable));
            return;
        }
        // 日期
        List<String> dateList = new ArrayList<>();
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            String date = accountDataBaseTable.getDate();
            if (dateList.contains(date)) {
                continue;
            }
            dateList.add(date);
        }
        // 类目
        List<String> categoryList = new ArrayList<>();
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            String category = accountDataBaseTable.getCategory();
            if (categoryList.contains(category)) {
                continue;
            }
            categoryList.add(category);
        }
        String startDate = StringUtils.replace(accountDataBaseTableList.get(accountDataBaseTableList.size() - 1).getDate(), "-", "");
        String endDate = StringUtils.replace(accountDataBaseTableList.get(0).getDate(), "-", "");
        String fileName = String.format(appCompatActivity.getString(R.string.formatAccountFileName), startDate, endDate);
        ExcelKit excelKit = new ExcelKit();
        excelKit.initExcel(Folder.ACCOUNT + fileName, appCompatActivity.getString(R.string.accountTwo), categoryList);
        excelKit.writeToExcel(appCompatActivity, Folder.ACCOUNT + fileName, dateList, categoryList, this::openAccount);
    }

    /**
     * 打开账单
     *
     * @param appCompatActivity 活动
     * @param filePath          文件路径
     */
    public void openAccount(AppCompatActivity appCompatActivity, String filePath) {
        new MyMaterialAlertDialogBuilder(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(String.format(appCompatActivity.getString(R.string.formatAccountExportSuccessful), filePath)).setPositiveButton(R.string.openAccount, (dialog, which) -> {
            dialog.dismiss();
            TbsKit.getInstance().startQbOrMiniQBToLoadUrl(appCompatActivity, filePath, null, s -> {
                if (TextUtils.equals(String.valueOf(TbsEnum.OPEN_FAIL.getType()), s)) {
                    ToastKit.showShort(TbsEnum.OPEN_FAIL.getHint());
                }
            });
        }).setNegativeButton(R.string.iKnow, (dialog, which) -> dialog.dismiss()).show();
    }
}