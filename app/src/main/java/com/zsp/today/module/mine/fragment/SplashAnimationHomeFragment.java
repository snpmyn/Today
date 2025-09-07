package com.zsp.today.module.mine.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.mine.kit.SplashAnimationHomeFragmentKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolFragment;
import util.rxbus.RxBus;
import widget.status.listener.BaseStatusListener;
import widget.status.manager.StatusManager;

/**
 * Created on 2022/5/7
 *
 * @author zsp
 * @desc 闪屏动画主页碎片
 */
public class SplashAnimationHomeFragment extends BasePoolFragment {
    private MaterialToolbar splashAnimationHomeFragmentMt;
    private RecyclerView splashAnimationHomeFragmentRv;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 闪屏动画主页碎片配套元件
     */
    private SplashAnimationHomeFragmentKit splashAnimationHomeFragmentKit;

    public static @NotNull SplashAnimationHomeFragment newInstance() {
        Bundle bundle = new Bundle();
        SplashAnimationHomeFragment fragment = new SplashAnimationHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.fragment_splash_animation_home;
    }

    /**
     * 第一 Fragment 否
     *
     * @return 第一 Fragment 否
     */
    @Override
    protected boolean areFirstFragment() {
        return false;
    }

    /**
     * EventBus 注册
     * <p>
     * onDestroyView 反注册。
     */
    @Override
    protected void eventBusRegister() {

    }

    /**
     * 初始控件
     * <p>
     * 此处仅设 Toolbar 标题、返回箭头等轻量 UI 操作。
     *
     * @param view 视图
     */
    @Override
    protected void stepUi(@NonNull View view) {
        splashAnimationHomeFragmentMt = view.findViewById(R.id.splashAnimationHomeFragmentMt);
        splashAnimationHomeFragmentRv = view.findViewById(R.id.splashAnimationHomeFragmentRv);
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
        hideSoftInput();
    }

    /**
     * EventBus 反注册
     * <p>
     * onCreateView 注册。
     */
    @Override
    protected void eventBusUnregister() {

    }

    private void initConfiguration() {
        // 状态管理器
        statusManager = StatusManager.generate(splashAnimationHomeFragmentRv, new BaseStatusListener() {
            @Override
            public void setRetryEvent(View retryView) {

            }
        });
        // 闪屏动画主页碎片配套元件
        splashAnimationHomeFragmentKit = new SplashAnimationHomeFragmentKit();
    }

    private void setListener() {
        splashAnimationHomeFragmentMt.setOnMenuItemClickListener(item -> {
            splashAnimationHomeFragmentKit.useDefaultSplashAnimation(fragmentationSupportActivity, SplashAnimationHomeFragment.this);
            return true;
        });
    }

    private void startLogic() {
        splashAnimationHomeFragmentKit.displaySplashAnimation(fragmentationSupportActivity, splashAnimationHomeFragmentRv, statusManager, this);
    }

    /**
     * 处理回退事件
     * <p>
     * 返 true 消费该事件，不再向上传递。
     * 返 false 向上最终传递至 Fragment 宿主 Activity。此时宿主 Activity 复写 onBackPressedSupport 则执行，没复写不执行。
     * Fragment 宿主 Activity 之基类复写 onKeyUp 时同执行。
     * MainActivity 于该法处理。
     * SplashActivity 与 LoginActivity 于 BaseActivity 之 onKeyUp 处理。
     *
     * @return boolean
     */
    @Override
    public boolean onBackPressedSupport() {
        pop();
        RxBus.get().post(RxBusConstant.MAIN_ACTIVITY_$_BOTTOM_NAVIGATION_VIEW, RxBusConstant.MAIN_ACTIVITY_$_SHOW_BOTTOM_NAVIGATION_VIEW_CODE);
        return true;
    }
}