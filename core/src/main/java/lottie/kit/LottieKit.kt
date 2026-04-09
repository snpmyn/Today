package lottie.kit;

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
 * <p>
 * Lottie 对 APK 大小影响非常小
 * 未压缩时为 287kb
 *
 * @author zsp
 * @desc Lottie 配套元件
 */
public class LottieKit {
    public static LottieKit getInstance() {
        return LottieKit.InstanceHolder.INSTANCE;
    }

    /**
     * 资产用法
     *
     * @param lottieAnimationView LottieAnimationView
     * @param assetName           资产名
     *                            如 "camera.json"
     * @param count               数量
     *                            {@link ValueAnimator#RESTART} 重复两次
     *                            两次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#REVERSE} 重复三次
     *                            三次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#INFINITE} 无限重复
     *                            不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithAsset(@NonNull LottieAnimationView lottieAnimationView, String assetName, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setAnimation(assetName);
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
        lottieAnimationView.playAnimation();
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
     *                            {@link ValueAnimator#RESTART} 重复两次
     *                            两次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#REVERSE} 重复三次
     *                            三次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#INFINITE} 无限重复
     *                            不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithAssetAndImageAsset(@NonNull LottieAnimationView lottieAnimationView, String assetName, String imageAssetFolder, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setImageAssetsFolder(imageAssetFolder);
        lottieAnimationView.setAnimation(assetName);
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
        lottieAnimationView.playAnimation();
    }

    /**
     * raw 用法
     *
     * @param lottieAnimationView LottieAnimationView
     * @param rawResId            raw 资源 ID
     *                            如 R.raw.hamburger_arrow
     * @param count               数量
     *                            {@link ValueAnimator#RESTART} 重复两次
     *                            两次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#REVERSE} 重复三次
     *                            三次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#INFINITE} 无限重复
     *                            不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithRaw(@NonNull LottieAnimationView lottieAnimationView, int rawResId, int count, Animator.AnimatorListener animatorListener) {
        lottieAnimationView.setAnimation(rawResId);
        lottieAnimationView.setRepeatCount(count);
        if (null != animatorListener) {
            lottieAnimationView.addAnimatorListener(animatorListener);
        }
        lottieAnimationView.playAnimation();
    }

    /**
     * 来自资产动画任务用法
     *
     * @param appCompatActivity   活动
     * @param lottieAnimationView LottieAnimationView
     * @param assetName           资产名
     *                            如 "emoji_wink.json"
     * @param count               数量
     *                            {@link ValueAnimator#RESTART} 重复两次
     *                            两次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#REVERSE} 重复三次
     *                            三次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#INFINITE} 无限重复
     *                            不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithLottieTaskFromAsset(AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, String assetName, int count, Animator.AnimatorListener animatorListener) {
        LottieTask<LottieComposition> lottieCompositionFromAsset = LottieCompositionFactory.fromAsset(appCompatActivity, assetName);
        lottieCompositionFromAsset.addListener(result -> {
            lottieAnimationView.setComposition(result);
            lottieAnimationView.setRepeatCount(count);
            if (null != animatorListener) {
                lottieAnimationView.addAnimatorListener(animatorListener);
            }
            lottieAnimationView.playAnimation();
        });
    }

    /**
     * 来自 raw 资源 ID 动画任务用法
     *
     * @param appCompatActivity   活动
     * @param lottieAnimationView LottieAnimationView
     * @param rawResId            raw 资源 ID
     *                            如 R.raw.jolly_walker
     * @param count               数量
     *                            {@link ValueAnimator#RESTART} 重复两次
     *                            两次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#REVERSE} 重复三次
     *                            三次重复结束执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     *                            <p>
     *                            {@link ValueAnimator#INFINITE} 无限重复
     *                            不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}
     *                            每次重复执行 {@link android.animation.Animator.AnimatorListener#onAnimationRepeat(Animator)}
     * @param animatorListener    An animation listener receives notifications from an animation.
     *                            Notifications indicate animation related events, such as the end or the repetition of the animation.
     */
    public void useWithLottieTaskFromRawResId(AppCompatActivity appCompatActivity, LottieAnimationView lottieAnimationView, int rawResId, int count, Animator.AnimatorListener animatorListener) {
        LottieTask<LottieComposition> lottieCompositionFromRawRes = LottieCompositionFactory.fromRawRes(appCompatActivity, rawResId);
        lottieCompositionFromRawRes.addListener(result -> {
            lottieAnimationView.setComposition(result);
            lottieAnimationView.setRepeatCount(count);
            if (null != animatorListener) {
                lottieAnimationView.addAnimatorListener(animatorListener);
            }
            lottieAnimationView.playAnimation();
        });
    }

    /**
     * 结束动画
     *
     * @param lottieAnimationView LottieAnimationView
     */
    public void endAnimation(@NonNull LottieAnimationView lottieAnimationView) {
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.clearAnimation();
    }

    private static final class InstanceHolder {
        static final LottieKit INSTANCE = new LottieKit();
    }
}