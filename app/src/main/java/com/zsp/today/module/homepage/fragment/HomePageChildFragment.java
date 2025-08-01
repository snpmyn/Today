package com.zsp.today.module.homepage.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.base.BaseFragment;
import com.zsp.today.module.function.FunctionActivity;
import com.zsp.today.module.homepage.kit.HomePageChildFragmentKit;
import com.zsp.today.value.RxBusConstant;

import org.jetbrains.annotations.NotNull;

import util.intent.IntentJump;
import util.rxbus.RxBus;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import widget.appbarlayout.listener.BaseAppBarLayoutStateChangeListener;
import widget.kotlin.banner.view.BannerView;
/**
 * Created on 2020/12/17
 * @author zsp
 * @desc 首页子碎片
 */
public class HomePageChildFragment extends BaseFragment {
    private AppBarLayout homePageChildFragmentAbl;
    private CollapsingToolbarLayout homePageChildFragmentCtl;
    private MaterialToolbar homePageChildFragmentMt;
    private BannerView homePageChildFragmentBv;
    private RecyclerView homePageChildFragmentRv;
    /**
     * 首页子碎片配套元件
     */
    private HomePageChildFragmentKit homePageChildFragmentKit;
    public static @NotNull HomePageChildFragment newInstance() {
        Bundle bundle = new Bundle();
        HomePageChildFragment fragment = new HomePageChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    /**
     * 布局资源 ID
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.fragment_home_page_child;
    }
    /**
     * 第一 Fragment 否
     * @return 第一 Fragment 否
     */
    @Override
    protected boolean areFirstFragment() {
        return true;
    }
    /**
     * EventBus 注册
     * <p>
     * onDestroyView 反注册。
     */
    @Override
    protected void eventBusRegister() {
        RxBus.get().register(this);
    }
    /**
     * 初始控件
     * <p>
     * 此处仅设 Toolbar 标题、返回箭头等轻量 UI 操作。
     * @param view 视图
     */
    @Override
    protected void stepUi(@NonNull View view) {
        homePageChildFragmentAbl = view.findViewById(R.id.homePageChildFragmentAbl);
        homePageChildFragmentCtl = view.findViewById(R.id.homePageChildFragmentCtl);
        homePageChildFragmentMt = view.findViewById(R.id.homePageChildFragmentMt);
        homePageChildFragmentBv = view.findViewById(R.id.homePageChildFragmentBv);
        homePageChildFragmentRv = view.findViewById(R.id.homePageChildFragmentRv);
    }
    /**
     * Fragment 对用户可见时调
     */
    @Override
    protected void visibleToUser() {

    }
    /**
     * 开始加载
     * <p>
     * 某些场景需懒加载，如 FragmentAdapter 懒加载、同级 Fragment 切换懒加载。
     * 库自 0.8 提供 onLazyInitView(Bundle saveInstanceState) 使用。
     */
    @Override
    protected void startLoadOnLazyInitView() {

    }
    /**
     * 开始加载
     * <p>
     * 此处设 Listener、各 Adapter、请求数据等。
     * onDestroyView 释放。
     */
    @Override
    protected void startLoadOnEnterAnimationEnd() {
        initConfiguration();
        setListener();
        startLogic();
    }
    /**
     * Fragment 对用户不可见时调
     */
    @Override
    protected void invisibleToUser() {

    }
    /**
     * EventBus 反注册
     * <p>
     * onCreateView 注册。
     */
    @Override
    protected void eventBusUnregister() {
        RxBus.get().unregister(this);
    }
    private void initConfiguration() {
        homePageChildFragmentKit = new HomePageChildFragmentKit();
    }
    private void setListener() {
        homePageChildFragmentMt.setOnMenuItemClickListener(item -> {
            IntentJump.getInstance().jump(null, fragmentationSupportActivity, false, FunctionActivity.class);
            return true;
        });
        homePageChildFragmentAbl.addOnOffsetChangedListener(new BaseAppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    // 折叠
                    homePageChildFragmentKit.showTitle(HomePageChildFragment.this, homePageChildFragmentCtl);
                    homePageChildFragmentCtl.setCollapsedTitleTextColor(ContextCompat.getColor(fragmentationSupportActivity, com.zsp.core.R.color.white));
                } else {
                    // 中间
                    homePageChildFragmentCtl.setTitle("");
                }
            }
        });
    }
    private void startLogic() {
        homePageChildFragmentKit.banner(homePageChildFragmentBv);
        homePageChildFragmentKit.preStore();
        homePageChildFragmentKit.display(fragmentationSupportActivity, homePageChildFragmentRv);
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU)})
    public void homePageChildFragmentRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU_CODE) {
            homePageChildFragmentKit.display(fragmentationSupportActivity, homePageChildFragmentRv);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        homePageChildFragmentBv.startAutoScroll();
    }
    @Override
    public void onStop() {
        super.onStop();
        homePageChildFragmentBv.stopAutoScroll();
    }
}
