package com.zsp.today.module.homepage;

import android.os.Bundle;
import android.view.View;

import com.zsp.today.R;
import com.zsp.today.module.homepage.fragment.HomePageChildFragment;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolFragment;

/**
 * Created on 2020/12/14
 *
 * @author zsp
 * @desc 首页碎片
 */
public class HomePageFragment extends BasePoolFragment {
    public static @NotNull HomePageFragment newInstance() {
        Bundle bundle = new Bundle();
        HomePageFragment fragment = new HomePageFragment();
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
        return R.layout.fragment_home_page;
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
    protected void stepUi(View view) {

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
        if (null == findChildFragment(HomePageChildFragment.class)) {
            loadRootFragment(R.id.homePageFragmentFl, HomePageChildFragment.newInstance());
        }
    }

    /**
     * 开始加载
     * <p>
     * 此处设 Listener、各 Adapter、请求数据等。
     * onDestroyView 释放。
     */
    @Override
    protected void startLoadOnEnterAnimationEnd() {

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

    }
}
