package com.zsp.today;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;

import android.view.View;

import androidx.core.app.ActivityCompat;

import com.zsp.today.base.BaseActivity;
import com.zsp.today.base.BaseFragment;
import com.zsp.today.kit.MainActivityKit;
import com.zsp.today.module.homepage.HomePageFragment;
import com.zsp.today.module.mine.MineFragment;
import com.zsp.today.value.RxBusConstant;

import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;
import util.rxbus.RxBus;
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
public class MainActivity extends BaseActivity implements BaseFragment.OnBackToFirstListener {
    private BottomNavigationView mainActivityBnv;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private final BaseFragment[] supportFragments = new BaseFragment[2];
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
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     * @param layoutResId        布局资源 ID
     */
    @Override
    protected void initContentView(Bundle savedInstanceState, int layoutResId) {
        super.initContentView(savedInstanceState, layoutResId);
        RxBus.get().register(this);
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
        mainActivityBnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuHomePage) {
                    showHideFragmentExecute(0, prePosition);
                } else if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuMine) {
                    showHideFragmentExecute(1, prePosition);
                }
                return true;
            }
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
        BaseFragment firstFragment = findFragment(HomePageFragment.class);
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

    /**
     * onBackPressedSupport
     * <p>
     * 回调时机为 Activity 回退栈内 Fragment 数小等 1 时默 finish Activity。
     * 尽量复写该法而非 onBackPress() 保 SupportFragment 内 onBackPressedSupport() 回退事件正常执行。
     */
    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}