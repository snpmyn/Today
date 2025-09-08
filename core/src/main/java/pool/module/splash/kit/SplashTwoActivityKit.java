package pool.module.splash.kit;

import android.animation.ValueAnimator;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.core.R;

import lottie.kit.LottieKit;
import pool.value.PoolConstant;
import util.handler.HandlerKit;
import util.mmkv.MmkvKit;

/**
 * Created on 2022/5/6
 *
 * @author zsp
 * @desc 闪屏二页配套元件
 */
public class SplashTwoActivityKit {
    /**
     * 执行
     *
     * @param appCompatActivity   活动
     * @param lottieAnimationView 控件
     * @param splashActivityKit   闪屏页配套元件
     */
    public void execute(@NonNull AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, SplashActivityKit splashActivityKit) {
        LottieKit.getInstance().useWithAsset(lottieAnimationView, getAnimationAssetName() + ".json", ValueAnimator.INFINITE, null);
        HandlerKit.getInstance().postDelayed(() -> {
            LottieKit.getInstance().endAnimation(lottieAnimationView);
            splashActivityKit.execute(appCompatActivity);
            saveAnimationAssetName();
        }, Long.parseLong(appCompatActivity.getString(R.string.PoolSplashDuration)));
    }

    /**
     * 获取动画资源名
     *
     * @return 动画资源名
     */
    private String getAnimationAssetName() {
        String animationAssetName = MmkvKit.defaultMmkv().decodeString(PoolConstant.SPLASH_$_ANIMATION);
        if (TextUtils.isEmpty(animationAssetName)) {
            animationAssetName = "lottie_animation_splash_default";
        }
        return animationAssetName;
    }

    /**
     * 保存动画资源名
     */
    private void saveAnimationAssetName() {
        if (TextUtils.isEmpty(MmkvKit.defaultMmkv().decodeString(PoolConstant.SPLASH_$_ANIMATION))) {
            MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_ANIMATION, "lottie_animation_splash_default");
        }
    }
}