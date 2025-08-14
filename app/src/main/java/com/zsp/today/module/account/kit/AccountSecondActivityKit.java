package com.zsp.today.module.account.kit;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.kit.BackupKit;
import com.zsp.today.module.account.AccountDetailActivity;
import com.zsp.today.module.account.adapter.AccountDateListAdapter;
import com.zsp.today.module.account.bean.AccountDateListBean;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.value.AccountCondition;
import com.zsp.today.value.AccountConstant;
import com.zsp.today.value.RxBusConstant;
import com.zsp.today.widget.status.StatusManagerKit;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.intent.IntentJump;
import util.intent.IntentVerify;
import util.rxbus.RxBus;
import widget.dialog.materialalertdialog.MyMaterialAlertDialogBuilder;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;
import widget.status.manager.StatusManager;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目二页配套元件
 */
public class AccountSecondActivityKit {
    /**
     * 展示账目
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param statusManager     状态管理器
     */
    public void displayAccount(@NonNull AppCompatActivity appCompatActivity, RecyclerView recyclerView, StatusManager statusManager) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_HOME_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        List<AccountDateListBean> accountDateListBeanList;
        if (null == accountTransferBean) {
            accountDateListBeanList = new ArrayList<>();
        } else {
            accountDateListBeanList = AccountDateKit.getInstance().getAccountDateListBeanListByDateRemoveDuplicationWithSort(accountTransferBean.getYear(), accountTransferBean.getMonth(), false);
        }
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerViewConfigure.gridLayout(3, 48, true, true, false);
        }
        // 适配器
        AccountDateListAdapter accountDateListAdapter = new AccountDateListAdapter(appCompatActivity, 3, 192);
        accountDateListAdapter.setAccountDateListData(accountDateListBeanList);
        accountDateListAdapter.setOnRecyclerViewOnItemClickListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                AccountDateListBean accountDateListBean = (AccountDateListBean) t;
                AccountTransferBean accountTransferBean = new AccountTransferBean(accountDateListBean.getDate());
                Intent intent = new Intent();
                intent.putExtra(AccountConstant.ACCOUNT_SECOND_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN, accountTransferBean);
                IntentJump.getInstance().jump(intent, appCompatActivity, false, AccountDetailActivity.class);
            }
        });
        accountDateListAdapter.setOnRecyclerViewOnItemLongClickListener(new OnRecyclerViewOnItemLongClickListener() {
            @Override
            public <T> void onItemLongClick(View view, int position, T t) {
                new MyMaterialAlertDialogBuilder(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(R.string.wantToDeleteTheAccountOfTheDay).setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    AccountDateListBean accountDateListBean = (AccountDateListBean) t;
                    LitePalKit.getInstance().multiDelete(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(false, null), accountDateListBean.getDate());
                    RecyclerViewDisplayController.deleteDynamic(accountDateListAdapter, position, accountDateListBeanList);
                    // 状态判断
                    StatusManagerKit.statusJudge(statusManager, accountDateListBeanList);
                    RxBus.get().post(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT, RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE);
                    // 备份
                    BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
                }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
            }
        });
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, accountDateListBeanList);
        // 展示
        RecyclerViewDisplayController.display(recyclerView, accountDateListAdapter);
    }
}