package com.zsp.today.module.mine.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.mine.kit.SplashAnimationDetailFragmentKit;
import com.zsp.today.module.splash.SplashConstant;

import org.jetbrains.annotations.NotNull;

import lottie.kit.LottieKit;
import pool.base.BasePoolFragment;

/**
 * Created on 2022/5/16
 *
 * @author zsp
 * @desc 闪屏动画详情碎片
 */
public class SplashAnimationDetailFragment extends BasePoolFragment implements View.OnClickListener {
    private LottieAnimationView splashAnimationDetailFragmentLav;
    private MaterialButton splashAnimationDetailFragmentMb;
    /**
     * 闪屏动画详情碎片配套元件
     */
    private SplashAnimationDetailFragmentKit splashAnimationDetailFragmentKit;

    public static @NotNull SplashAnimationDetailFragment newInstance(String resName) {
        Bundle bundle = new Bundle();
        bundle.putString(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME, resName);
        SplashAnimationDetailFragment fragment = new SplashAnimationDetailFragment();
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
        return R.layout.fragment_splash_animation_detail;
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
        splashAnimationDetailFragmentLav = view.findViewById(R.id.splashAnimationDetailFragmentLav);
        splashAnimationDetailFragmentMb = view.findViewById(R.id.splashAnimationDetailFragmentMb);
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
        splashAnimationDetailFragmentKit = new SplashAnimationDetailFragmentKit();
    }

    private void setListener() {
        splashAnimationDetailFragmentMb.setOnClickListener(this);
    }

    private void startLogic() {
        splashAnimationDetailFragmentKit.displaySplashAnimation(splashAnimationDetailFragmentLav, getArguments());
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.splashAnimationDetailFragmentMb) {
            splashAnimationDetailFragmentKit.useSplashAnimation(this, getArguments());
        }
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
        LottieKit.getInstance().endAnimation(splashAnimationDetailFragmentLav);
        pop();
        return true;
    }
}