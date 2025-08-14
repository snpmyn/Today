package com.zsp.today;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.View;

import com.zsp.today.kit.MainActivityKit;
import com.zsp.today.module.homepage.HomePageFragment;
import com.zsp.today.module.mine.MineFragment;
import com.zsp.today.value.RxBusConstant;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import pool.base.BasePoolFragment;
import timber.log.Timber;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import util.view.ViewUtils;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2025/7/30 11:18
 * @version: v 1.0
 */
public class MainActivity extends BasePoolActivity implements BasePoolFragment.OnBackToFirstListener {
    private BottomNavigationView mainActivityBnv;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private final BasePoolFragment[] supportFragments = new BasePoolFragment[2];
    /**
     * Fragment
     */
    private int prePosition;
    /**
     * 主页配套元件
     */
    private MainActivityKit mainActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        mainActivityBnv = findViewById(R.id.mainActivityBnv);
        mainActivityBnv.setItemIconTintList(null);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        mainActivityKit = new MainActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        mainActivityBnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuHomePage) {
                showHideFragmentExecute(0, prePosition);
            } else if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuMine) {
                showHideFragmentExecute(1, prePosition);
            }
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // Fragment 显示
        fragmentShow();
        // 执行
        mainActivityKit.execute(this);
    }

    /**
     * Fragment 显示
     */
    private void fragmentShow() {
        BasePoolFragment firstFragment = findFragment(HomePageFragment.class);
        if (null == firstFragment) {
            supportFragments[FIRST] = HomePageFragment.newInstance();
            supportFragments[SECOND] = MineFragment.newInstance();
            loadMultipleRootFragment(R.id.mainActivityFl, FIRST, supportFragments[FIRST], supportFragments[SECOND]);
        } else {
            // 此处库已做 Fragment 恢复（无需额外处理，无重叠问题）
            // 此处需拿到 mFragments 引用
            supportFragments[FIRST] = firstFragment;
            supportFragments[SECOND] = findFragment(MineFragment.class);
        }
    }

    private void clickShow(int position, int prePosition) {
        Timber.d("show %s hide %s ", position, prePosition);
    }

    public void showHideFragmentExecute(int position, int prePosition) {
        clickShow(position, prePosition);
        showHideFragment(supportFragments[position], supportFragments[prePosition]);
        this.prePosition = position;
    }

    /**
     * 回第一 Fragment
     */
    @Override
    public void onBackToFirstFragment() {
        mainActivityBnv.getMenu().findItem(com.zsp.core.R.id.bottomNavigationViewMenuHomePage).setChecked(true);
        showHideFragmentExecute(0, prePosition);
        prePosition = 0;
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.MAIN_ACTIVITY_$_BOTTOM_NAVIGATION_VIEW)})
    public void mainActivityBottomNavigationView(@NotNull Integer integer) {
        switch (integer) {
            // 隐底导航视图
            case RxBusConstant.MAIN_ACTIVITY_$_HIDE_BOTTOM_NAVIGATION_VIEW_CODE:
                ViewUtils.hideView(mainActivityBnv, View.GONE);
                break;
            // 显底导航视图
            case RxBusConstant.MAIN_ACTIVITY_$_SHOW_BOTTOM_NAVIGATION_VIEW_CODE:
                ViewUtils.showView(mainActivityBnv);
                break;
            default:
                break;
        }
    }
}