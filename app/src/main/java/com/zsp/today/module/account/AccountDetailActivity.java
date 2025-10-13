package com.zsp.today.module.account;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.kit.AccountDetailActivityKit;
import com.zsp.today.module.account.value.AccountConstant;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import util.intent.IntentVerify;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import util.view.ViewUtils;
import widget.floatingactionbutton.DraggableFloatingActionButton;
import widget.floatingactionbutton.kit.DraggableFloatingActionButtonKit;
import widget.materialcontainertransform.MaterialContainerTransformKit;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;
import widget.view.AccountLineView;
import widget.view.NutritionChartView;

/**
 * @desc: 账目详情页
 * @author: zsp
 * @date: 2021/3/12 5:38 PM
 */
public class AccountDetailActivity extends BasePoolActivity {
    private RelativeLayout accountDetailActivityRl;
    private MaterialToolbar accountDetailActivityMt;
    private RecyclerView accountDetailActivityRv;
    private DraggableFloatingActionButton accountDetailActivityDfab;
    private MaterialCardView accountDetailActivityMcv;
    private LinearLayout accountDetailActivityLl;
    private NutritionChartView accountDetailActivityNcv;
    private PieChart accountDetailActivityPc;
    private AccountLineView accountDetailActivityAav;
    private MaterialButton accountDetailActivityMb;
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
        accountDetailActivityDfab = findViewById(R.id.accountDetailActivityDfab);
        DraggableFloatingActionButtonKit.execute(accountDetailActivityDfab);
        accountDetailActivityMcv = findViewById(R.id.accountDetailActivityMcv);
        accountDetailActivityLl = findViewById(R.id.accountDetailActivityLl);
        accountDetailActivityNcv = findViewById(R.id.accountDetailActivityNcv);
        accountDetailActivityPc = findViewById(R.id.accountDetailActivityPc);
        accountDetailActivityAav = findViewById(R.id.accountDetailActivityAav);
        accountDetailActivityMb = findViewById(R.id.accountDetailActivityMb);
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
        MaterialContainerTransformKit.getInstance().setTransitionName(accountDetailActivityDfab, String.valueOf(R.id.accountDetailActivityDfab));
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        accountDetailActivityMt.setOnMenuItemClickListener(item -> {
            menuItemClickToExecute(item);
            return true;
        });
        // FloatingActionButton
        accountDetailActivityDfab.setOnClickListener(v -> accountDetailActivityKit.everydayAnalysis(AccountDetailActivity.this, accountDetailActivityRl, accountDetailActivityMcv, accountDetailActivityLl, accountDetailActivityNcv, accountDetailActivityPc, accountDetailActivityAav, accountDetailActivityDfab, accountTransferBean.getDate(), false));
        // MaterialButton
        accountDetailActivityMb.setOnClickListener(v -> {
            MaterialContainerTransformKit.getInstance().showStartView(AccountDetailActivity.this, accountDetailActivityRl, accountDetailActivityDfab, accountDetailActivityMcv, false);
            ViewUtils.showView(accountDetailActivityDfab);
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        accountDetailActivityKit.displayAccount(this, accountDetailActivityRv, accountTransferBean.getDate(), statusManager);
    }

    /**
     * 菜单条目点击执行
     *
     * @param menuItem 菜单条目
     */
    private void menuItemClickToExecute(@NotNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.accountDetailActivityMenuAddAccount) {
            // 添加账目
            accountDetailActivityKit.addAccount(AccountDetailActivity.this);
        } else if (itemId == R.id.accountDetailActivityMenuAccountAnalysis) {
            // 账目分析
            accountDetailActivityKit.everydayAnalysis(AccountDetailActivity.this, accountDetailActivityRl, accountDetailActivityMcv, accountDetailActivityLl, accountDetailActivityNcv, accountDetailActivityPc, accountDetailActivityAav, accountDetailActivityDfab, accountTransferBean.getDate(), true);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT)})
    public void accountDetailActivityRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT_CODE) {
            accountDetailActivityKit.displayAccount(this, accountDetailActivityRv, accountTransferBean.getDate(), statusManager);
        }
    }
}