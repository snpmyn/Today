package pool.module.splash.kit;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.core.R;

import lottie.LottieKit;
import pool.value.PoolConstant;
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
    public void execute(AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, SplashActivityKit splashActivityKit) {
        String assetName = MmkvKit.defaultMmkv().decodeString(PoolConstant.SPLASH_$_ANIMATION);
        if (MmkvKit.defaultMmkv().decodeBool(PoolConstant.SPLASH_$_USE_DEFAULT_ANIMATION) || TextUtils.isEmpty(assetName)) {
            assetName = "lottie_animation_splash_default";
        }
        LottieKit lottieKit = new LottieKit();
        lottieKit.useWithAsset(lottieAnimationView, assetName + ".json", ValueAnimator.INFINITE, null);
        new Handler(appCompatActivity.getMainLooper()).postDelayed(() -> {
            LottieKit lottieKitInner = new LottieKit();
            lottieKitInner.cancelAnimation(lottieAnimationView);
            splashActivityKit.execute(appCompatActivity);
        }, Long.parseLong(appCompatActivity.getString(R.string.PoolSplashDuration)));
    }
}