package com.zsp.today.module.account;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.kit.AccountDetailActivityKit;
import com.zsp.today.module.account.value.AccountConstant;

import pool.base.BasePoolActivity;
import util.intent.IntentVerify;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import util.view.ViewUtils;
import widget.materialcontainertransform.MaterialContainerTransformKit;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;

/**
 * @desc: 账目详情页
 * @author: zsp
 * @date: 2021/3/12 5:38 PM
 */
public class AccountDetailActivity extends BasePoolActivity {
    private RelativeLayout accountDetailActivityRl;
    private MaterialToolbar accountDetailActivityMt;
    private RecyclerView accountDetailActivityRv;
    private FloatingActionButton accountDetailActivityFab;
    private MaterialCardView accountDetailActivityMcv;
    private PieChart accountDetailActivityPc;
    private Button accountDetailActivityButton;
    /**
     * 账目传输
     */
    private AccountTransferBean accountTransferBean;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 账目详情页配套元件
     */
    private AccountDetailActivityKit accountDetailActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_account_detail;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        accountDetailActivityRl = findViewById(R.id.accountDetailActivityRl);
        accountDetailActivityMt = findViewById(R.id.accountDetailActivityMt);
        accountDetailActivityRv = findViewById(R.id.accountDetailActivityRv);
        accountDetailActivityFab = findViewById(R.id.accountDetailActivityFab);
        accountDetailActivityMcv = findViewById(R.id.accountDetailActivityMcv);
        accountDetailActivityPc = findViewById(R.id.accountDetailActivityPc);
        accountDetailActivityButton = findViewById(R.id.accountDetailActivityButton);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 账目传输
        accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(getIntent(), AccountConstant.ACCOUNT_SECOND_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        // 状态管理器
        statusManager = StatusManagerKit.generate(this, accountDetailActivityRv, new StatusManagerKit.StatusManagerKitListener() {
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
        // 账目详情页配套元件
        accountDetailActivityKit = new AccountDetailActivityKit();
        // 设转换名
        MaterialContainerTransformKit.getInstance().setTransitionName(accountDetailActivityFab, String.valueOf(R.id.accountDetailActivityFab));
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        accountDetailActivityMt.setOnMenuItemClickListener(item -> {
            accountDetailActivityKit.addAccount(AccountDetailActivity.this);
            return true;
        });
        // FloatingActionButton
        accountDetailActivityFab.setOnClickListener(v -> {
            ViewUtils.hideView(accountDetailActivityFab, View.GONE);
            MaterialContainerTransformKit.getInstance().showEndView(AccountDetailActivity.this, accountDetailActivityRl, accountDetailActivityFab, accountDetailActivityMcv, false);
            accountDetailActivityKit.everydayAnalysis(AccountDetailActivity.this, accountDetailActivityPc, accountTransferBean.getDate());
        });
        // ImageButton
        accountDetailActivityButton.setOnClickListener(v -> {
            MaterialContainerTransformKit.getInstance().showStartView(AccountDetailActivity.this, accountDetailActivityRl, accountDetailActivityFab, accountDetailActivityMcv, false);
            ViewUtils.showView(accountDetailActivityFab);
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        accountDetailActivityKit.displayAccount(this, accountDetailActivityRv, accountTransferBean.getDate(), statusManager);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT)})
    public void accountDetailActivityRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT_CODE) {
            accountDetailActivityKit.displayAccount(this, accountDetailActivityRv, accountTransferBean.getDate(), statusManager);
        }
    }
}