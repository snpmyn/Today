package com.zsp.today.module.homepage;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.function.FunctionActivity;
import com.zsp.today.module.homepage.kit.HomePageFragmentKit;
import com.zsp.today.module.setting.kit.SharedPreferencesKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolFragment;
import util.intent.IntentJump;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import util.typeface.TypefaceUtils;
import widget.appbarlayout.listener.BaseAppBarLayoutStateChangeListener;
import widget.kotlin.banner.view.BannerView;
import widget.materialtoolbar.MaterialToolbarKit;
import widget.quote.WeeklyQuoteKit;

/**
 * Created on 2020/12/17
 *
 * @author zsp
 * @desc 首页碎片
 */
public class HomePageFragment extends BasePoolFragment {
    private AppBarLayout homePageFragmentAbl;
    private CollapsingToolbarLayout homePageFragmentCtl;
    private MaterialToolbar homePageFragmentMt;
    private BannerView homePageFragmentBv;
    private RecyclerView homePageFragmentRvBanner;
    private RecyclerView homePageFragmentRv;
    /**
     * 首页碎片配套元件
     */
    private HomePageFragmentKit homePageFragmentKit;

    public static @NotNull HomePageFragment newInstance() {
        Bundle bundle = new Bundle();
        HomePageFragment fragment = new HomePageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取布局 ID
     *
     * @return 布局 ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_page;
    }

    /**
     * 初始视图
     *
     * @param view 视图
     */
    @Override
    protected void initView(@NonNull View view) {
        homePageFragmentAbl = view.findViewById(R.id.homePageFragmentAbl);
        homePageFragmentCtl = view.findViewById(R.id.homePageFragmentCtl);
        homePageFragmentMt = view.findViewById(R.id.homePageFragmentMt);
        homePageFragmentBv = view.findViewById(R.id.homePageFragmentBv);
        homePageFragmentRvBanner = view.findViewById(R.id.homePageFragmentRvBanner);
        homePageFragmentRv = view.findViewById(R.id.homePageFragmentRv);
    }

    /**
     * 头次可见
     * <p>
     * 只调一次
     * <p>
     * 网络请求
     * 耗时任务
     * 初始数据
     */
    @Override
    protected void onFragmentFirstVisible() {
        initConfiguration();
        setListener();
        startLogic();
    }

    private void initConfiguration() {
        MaterialToolbarKit.getInstance().setMenuItemColor(baseLazyFragmentAppCompatActivity, homePageFragmentMt, com.zsp.core.R.color.color_06FDDB);
        homePageFragmentKit = new HomePageFragmentKit();
        homePageFragmentKit.initTitle(homePageFragmentCtl, homePageFragmentMt, WeeklyQuoteKit.getQuote(SharedPreferencesKit.getInstance().homePageQuote()));
    }

    private void setListener() {
        homePageFragmentMt.setOnMenuItemClickListener(item -> {
            IntentJump.getInstance().jump(null, baseLazyFragmentAppCompatActivity, false, FunctionActivity.class);
            return true;
        });
        homePageFragmentAbl.addOnOffsetChangedListener(new BaseAppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    // 折叠
                    homePageFragmentCtl.setTitleEnabled(true);
                    homePageFragmentCtl.setCollapsedTitleTextSize(56.0F);
                    homePageFragmentCtl.setCollapsedTitleTypeface(TypefaceUtils.sansBold(baseLazyFragmentAppCompatActivity));
                    homePageFragmentCtl.setTitle(WeeklyQuoteKit.getQuote(SharedPreferencesKit.getInstance().homePageQuote()));
                } else if (state == State.EXPANDED) {
                    // 展开
                    homePageFragmentKit.initTitle(homePageFragmentCtl, homePageFragmentMt, WeeklyQuoteKit.getQuote(SharedPreferencesKit.getInstance().homePageQuote()));
                } else {
                    // 中间
                    homePageFragmentCtl.setTitle("");
                }
            }
        });
    }

    private void startLogic() {
        homePageFragmentKit.banner(homePageFragmentBv);
        homePageFragmentKit.carousel(baseLazyFragmentAppCompatActivity, homePageFragmentRvBanner);
        homePageFragmentKit.functionDataBaseTable(baseLazyFragmentAppCompatActivity, homePageFragmentRv);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.HOME_PAGE_FRAGMENT_$_REFRESH_MENU)})
    public void homePageFragmentRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.HOME_PAGE_FRAGMENT_$_REFRESH_MENU_CODE) {
            homePageFragmentKit.displayFunctionDataBaseTable(baseLazyFragmentAppCompatActivity, homePageFragmentRv);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        homePageFragmentBv.startAutoScroll();
    }

    @Override
    public void onStop() {
        super.onStop();
        homePageFragmentBv.stopAutoScroll();
    }
}