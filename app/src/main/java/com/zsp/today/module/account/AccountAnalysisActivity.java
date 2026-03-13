package com.zsp.today.module.account;

import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.account.kit.AccountAnalysisActivityKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import widget.menu.MenuItemKit;

/**
 * @desc: 账目分析页
 * @author: zsp
 * @date: 2021/6/13 0013 下午 7:34
 */
public class AccountAnalysisActivity extends BasePoolActivity {
    private MaterialToolbar accountAnalysisActivityMt;
    private TextView accountAnalysisActivityTv;
    private LineChart accountAnalysisActivityLcYearAnalysis;
    private LineChart accountAnalysisActivityLcMonthAnalysis;
    /**
     * 账目分析页配套元件
     */
    private AccountAnalysisActivityKit accountAnalysisActivityKit;

    /**
     * 布局资源 ID
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 layoutResId()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_account_analysis;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        accountAnalysisActivityMt = findViewById(R.id.accountAnalysisActivityMt);
        accountAnalysisActivityTv = findViewById(R.id.accountAnalysisActivityTv);
        accountAnalysisActivityLcYearAnalysis = findViewById(R.id.accountAnalysisActivityLcYearAnalysis);
        accountAnalysisActivityLcMonthAnalysis = findViewById(R.id.accountAnalysisActivityLcMonthAnalysis);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        accountAnalysisActivityKit = new AccountAnalysisActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        accountAnalysisActivityMt.setOnMenuItemClickListener(item -> {
            menuItemClickToExecute(item);
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        accountAnalysisActivityKit.monthAnalysis(this, accountAnalysisActivityTv, accountAnalysisActivityLcMonthAnalysis, accountAnalysisActivityLcYearAnalysis);
    }

    /**
     * 菜单条目点击执行
     *
     * @param menuItem 菜单条目
     */
    private void menuItemClickToExecute(@NotNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.accountAnalysisActivityMenuChooseDate) {
            // 选择日期
            accountAnalysisActivityKit.chooseDateAndShowMonthAnalysis(this, accountAnalysisActivityTv, accountAnalysisActivityLcMonthAnalysis);
        } else if (itemId == R.id.accountAnalysisActivityMenuMonthAnalysis) {
            // 月度分析
            MenuItemKit.getInstance().show(accountAnalysisActivityMt.getMenu().findItem(R.id.accountAnalysisActivityMenuChooseDate));
            accountAnalysisActivityKit.monthAnalysis(this, accountAnalysisActivityTv, accountAnalysisActivityLcMonthAnalysis, accountAnalysisActivityLcYearAnalysis);
        } else if (itemId == R.id.accountAnalysisActivityMenuYearAnalysis) {
            // 年度分析
            MenuItemKit.getInstance().hide(accountAnalysisActivityMt.getMenu().findItem(R.id.accountAnalysisActivityMenuChooseDate));
            accountAnalysisActivityKit.yearAnalysis(this, accountAnalysisActivityTv, accountAnalysisActivityLcYearAnalysis, accountAnalysisActivityLcMonthAnalysis);
        }
    }
}