package com.zsp.today.module.account;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.kit.AccountHomeActivityKit;
import com.zsp.today.module.account.kit.AccountMonthKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import util.datetime.DateUtils;
import util.intent.IntentJump;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import util.view.ViewUtils;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;

/**
 * @desc: 账目主页
 * @author: zsp
 * @date: 2021/6/13 0013 下午 7:23
 */
public class AccountHomeActivity extends BasePoolActivity implements View.OnClickListener {
    /**
     * 指定年
     */
    public static String appointYear;
    private MaterialToolbar accountHomeActivityMt;
    private RecyclerView accountHomeActivityRv;
    private ExtendedFloatingActionButton accountHomeActivityEfab;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 账目主页配套元件
     */
    private AccountHomeActivityKit accountHomeActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_account_home;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        accountHomeActivityMt = findViewById(R.id.accountHomeActivityMt);
        accountHomeActivityRv = findViewById(R.id.accountHomeActivityRv);
        accountHomeActivityEfab = findViewById(R.id.accountHomeActivityEfab);
        accountHomeActivityMt.setTitle(DateUtils.getCurrentTimeYear());
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 指定年
        appointYear = DateUtils.getCurrentTimeYear();
        // 状态管理器
        statusManager = StatusManagerKit.generate(this, accountHomeActivityRv, new StatusManagerKit.StatusManagerKitListener() {
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
        // 账目主页配套元件
        accountHomeActivityKit = new AccountHomeActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        accountHomeActivityMt.setOnMenuItemClickListener(item -> {
            menuItemClickToExecute(item);
            return true;
        });
        accountHomeActivityEfab.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        accountHomeActivityKit.judgeDisplayAccount(this, accountHomeActivityRv, statusManager, appointYear);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.accountHomeActivityEfab) {
            ViewUtils.hideView(accountHomeActivityEfab, View.GONE);
            appointYear = DateUtils.getCurrentTimeYear();
            accountHomeActivityMt.setTitle(appointYear);
            accountHomeActivityKit.displayAccount(this, accountHomeActivityRv, AccountMonthKit.getInstance().getAccountMonthListBeanListByMonthRemoveDuplicationWithSort(appointYear, true), statusManager);
        }
    }

    /**
     * 菜单条目点击执行
     *
     * @param menuItem 菜单条目
     */
    private void menuItemClickToExecute(@NotNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.accountHomeActivityMenuChooseYear) {
            // 选择年份
            accountHomeActivityKit.chooseYearAndDisplayAccount(this, accountHomeActivityMt, accountHomeActivityRv, statusManager, accountHomeActivityEfab);
        } else if (itemId == R.id.accountHomeActivityMenuAddAccount) {
            // 添加账目
            IntentJump.getInstance().jumpWithAnimation(null, this, false, AddAccountActivity.class, 0, 0);
        } else if (itemId == R.id.accountHomeActivityMenuMoreFunction) {
            // 更多功能
            accountHomeActivityKit.bottomSheetDialog(this);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT)})
    public void accountHomeActivityRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE) {
            accountHomeActivityKit.displayAccount(this, accountHomeActivityRv, AccountMonthKit.getInstance().getAccountMonthListBeanListByMonthRemoveDuplicationWithSort(appointYear, true), statusManager);
        }
    }
}