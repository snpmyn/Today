package com.zsp.today.module.mine;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.mine.kit.SplashAnimationDetailActivityKit;
import com.zsp.today.module.splash.SplashConstant;

import lottie.kit.LottieKit;
import pool.base.BasePoolActivity;

/**
 * Created on 2025/12/21.
 *
 * @author 郑少鹏
 * @desc 闪屏动画详情页
 */
public class SplashAnimationDetailActivity extends BasePoolActivity implements View.OnClickListener {
    private LottieAnimationView splashAnimationDetailActivityLav;
    private MaterialButton splashAnimationDetailActivityMb;
    /**
     * 闪屏动画详情页配套元件
     */
    private SplashAnimationDetailActivityKit splashAnimationDetailActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_splash_animation_detail;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        splashAnimationDetailActivityLav = findViewById(R.id.splashAnimationDetailActivityLav);
        splashAnimationDetailActivityMb = findViewById(R.id.splashAnimationDetailActivityMb);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        splashAnimationDetailActivityKit = new SplashAnimationDetailActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        splashAnimationDetailActivityMb.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        splashAnimationDetailActivityKit.displaySplashAnimation(splashAnimationDetailActivityLav, getIntent().getBundleExtra(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.splashAnimationDetailActivityMb) {
            splashAnimationDetailActivityKit.useSplashAnimation(this, getIntent().getBundleExtra(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LottieKit.getInstance().endAnimation(splashAnimationDetailActivityLav);
    }
}