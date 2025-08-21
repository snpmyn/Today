package com.zsp.today.module.mine.kit;

import android.animation.ValueAnimator;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.today.module.mine.fragment.SplashAnimationDetailFragment;
import com.zsp.today.value.SplashConstant;

import lottie.LottieKit;
import pool.value.PoolConstant;
import util.mmkv.MmkvKit;

/**
 * Created on 2022/5/16
 *
 * @author zsp
 * @desc 闪屏动画详情碎片配套元件
 */
public class SplashAnimationDetailFragmentKit {
    /**
     * 展示闪屏动画
     *
     * @param lottieAnimationView LottieAnimationView
     * @param bundle              Bundle
     */
    public void displaySplashAnimation(LottieAnimationView lottieAnimationView, Bundle bundle) {
        if (null == bundle) {
            return;
        }
        LottieKit lottieKit = new LottieKit();
        lottieKit.useWithAsset(lottieAnimationView, bundle.getString(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME) + ".json", ValueAnimator.INFINITE, null);
    }

    /**
     * 使用闪屏动画
     *
     * @param splashAnimationDetailFragment 闪屏动画详情碎片
     * @param bundle                        Bundle
     */
    public void useSplashAnimation(SplashAnimationDetailFragment splashAnimationDetailFragment, Bundle bundle) {
        if (null == bundle) {
            return;
        }
        if (MmkvKit.defaultMmkv().decodeBool(PoolConstant.SPLASH_$_USE_DEFAULT_ANIMATION)) {
            MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_USE_DEFAULT_ANIMATION, false);
        }
        MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_ANIMATION, bundle.getString(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME));
        splashAnimationDetailFragment.onBackPressedSupport();
    }
}