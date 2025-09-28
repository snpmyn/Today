package com.zsp.today.module.account;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.kit.AccountSecondActivityKit;
import com.zsp.today.module.account.value.AccountConstant;

import pool.base.BasePoolActivity;
import util.intent.IntentJump;
import util.intent.IntentVerify;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;
import widget.transition.kit.TransitionKit;

/**
 * @desc: 账目二页
 * @author: zsp
 * @date: 2021/11/4 11:22 上午
 */
public class AccountSecondActivity extends BasePoolActivity {
    private MaterialToolbar accountSecondActivityMt;
    private RecyclerView accountSecondActivityRv;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 账目二页配套元件
     */
    private AccountSecondActivityKit accountSecondActivityKit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TransitionKit.getInstance().endPageSetting(this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_account_second;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        accountSecondActivityMt = findViewById(R.id.accountSecondActivityMt);
        accountSecondActivityRv = findViewById(R.id.accountSecondActivityRv);
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(getIntent(), AccountConstant.ACCOUNT_HOME_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        assert accountTransferBean != null;
        accountSecondActivityMt.setTitle(String.format(getString(R.string.formatMonth), accountTransferBean.getMonth()));
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 状态管理器
        statusManager = StatusManagerKit.generate(this, accountSecondActivityRv, new StatusManagerKit.StatusManagerKitListener() {
            @Override
            public void noNetwork() {

            }

            @Override
            public void connectFail() {

            }

            @Override
            public void loadFail() {

            }
        });
        // 账目二页配套元件
        accountSecondActivityKit = new AccountSecondActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        accountSecondActivityMt.setOnMenuItemClickListener(item -> {
            menuItemClickToExecute();
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        accountSecondActivityKit.displayAccount(this, accountSecondActivityRv, statusManager);
    }

    /**
     * 菜单条目点击执行
     */
    private void menuItemClickToExecute() {
        IntentJump.getInstance().jumpWithAnimation(null, this, false, AddAccountActivity.class, 0, 0);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT)})
    public void accountSecondActivityRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE) {
            accountSecondActivityKit.displayAccount(this, accountSecondActivityRv, statusManager);
        }
    }
}