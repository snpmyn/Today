package com.zsp.today.module.mine.kit;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.today.R;
import com.zsp.today.module.mine.fragment.SplashAnimationDetailFragment;
import com.zsp.today.module.splash.SplashConstant;

import lottie.kit.LottieKit;
import pool.value.PoolConstant;
import util.mmkv.MmkvKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;

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
        LottieKit.getInstance().useWithAsset(lottieAnimationView, bundle.getString(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME) + ".json", ValueAnimator.INFINITE, null);
    }

    /**
     * 使用闪屏动画
     *
     * @param appCompatActivity             活动
     * @param splashAnimationDetailFragment 闪屏动画详情碎片
     * @param bundle                        Bundle
     */
    public void useSplashAnimation(AppCompatActivity appCompatActivity, SplashAnimationDetailFragment splashAnimationDetailFragment, Bundle bundle) {
        if (null == bundle) {
            return;
        }
        if (MmkvKit.defaultMmkv().decodeBool(PoolConstant.SPLASH_$_USE_DEFAULT_ANIMATION)) {
            MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_USE_DEFAULT_ANIMATION, false);
        }
        MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_ANIMATION, bundle.getString(SplashConstant.SPLASH_ANIMATION_HOME_FRAGMENT_$_RES_NAME));
        splashAnimationDetailFragment.onBackPressedSupport();
        // 提示
        BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogOne(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.setAnimationSuccessful), 0, () -> BocDialogKit.getInstance(appCompatActivity).end(), null);
    }
}