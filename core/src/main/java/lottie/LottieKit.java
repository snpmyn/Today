package lottie;

import android.animation.Animator;
import android.animation.ValueAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieTask;

/**
 * Created on 2021/9/18
 *
 * @author zsp
 * @desc Lottie 配套元件
 */
public class LottieKit {
    /**
     * 资产用法
     *
     * @param lottieAnimationView LottieAnimationView
     * @param assetName           资产名
     *                            如 "camera.json"
     * @param count               数量
     *                            如 {@link ValueAnimator#INFINITE}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithAsset(@NonNull LottieAnimationView lottieAnimationView, String assetName, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setAnimation(assetName);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
    }

    /**
     * 资产和图片资产用法
     *
     * @param lottieAnimationView LottieAnimationView
     * @param assetName           资产名
     *                            如 "camera.json"
     * @param imageAssetFolder    图片资产目录
     *                            如 "images_splash_two/"
     * @param count               数量
     *                            如 {@link ValueAnimator#INFINITE}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithAssetAndImageAsset(@NonNull LottieAnimationView lottieAnimationView, String assetName, String imageAssetFolder, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setAnimation(assetName);
        lottieAnimationView.setImageAssetsFolder(imageAssetFolder);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
    }

    /**
     * raw 用法
     *
     * @param lottieAnimationView LottieAnimationView
     * @param rawResId            raw 资源 ID
     *                            如 R.raw.hamburger_arrow
     * @param count               数量
     *                            如 {@link ValueAnimator#INFINITE}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithRaw(@NonNull LottieAnimationView lottieAnimationView, int rawResId, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setAnimation(rawResId);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
    }

    /**
     * 来自资产动画任务用法
     *
     * @param appCompatActivity   活动
     * @param lottieAnimationView LottieAnimationView
     * @param assetName           资产名
     *                            如 "emoji_wink.json"
     * @param count               数量
     *                            如 {@link ValueAnimator#INFINITE}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithLottieTaskFromAsset(AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, String assetName, int count, Animator.AnimatorListener animatorListener) {
        LottieTask<LottieComposition> lottieCompositionFromAsset = LottieCompositionFactory.fromAsset(appCompatActivity, assetName);
        lottieCompositionFromAsset.addListener(result -> {
            lottieAnimationView.setComposition(result);
            lottieAnimationView.playAnimation();
            lottieAnimationView.setRepeatCount(count);
        });
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
    }

    /**
     * 来自 raw 资源 ID 动画任务用法
     *
     * @param appCompatActivity   活动
     * @param lottieAnimationView LottieAnimationView
     * @param rawResId            raw 资源 ID
     *                            如 R.raw.jolly_walker
     * @param count               数量
     *                            如 {@link ValueAnimator#INFINITE}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithLottieTaskFromRawResId(AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, int rawResId, int count, Animator.AnimatorListener animatorListener) {
        LottieTask<LottieComposition> lottieCompositionFromRawRes = LottieCompositionFactory.fromRawRes(appCompatActivity, rawResId);
        lottieCompositionFromRawRes.addListener(result -> {
            lottieAnimationView.setComposition(result);
            lottieAnimationView.playAnimation();
            lottieAnimationView.setRepeatCount(count);
        });
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
    }

    /**
     * 取消动画
     *
     * @param lottieAnimationView LottieAnimationView
     */
    public void cancelAnimation(@NonNull LottieAnimationView lottieAnimationView) {
        lottieAnimationView.cancelAnimation();
    }
}
