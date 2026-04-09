package lottie.kit

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory

/**
 * Created on 2026/4/9.
 * @author 郑少鹏
 * @desc Lottie 配套元件
 * <p>
 * Lottie 对 APK 大小影响非常小
 * 未压缩时为 287kb
 */
object LottieKit {
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
    @JvmStatic
    fun useWithAsset(
        lottieAnimationView: LottieAnimationView,
        assetName: String,
        count: Int,
        animatorListener: Animator.AnimatorListener? = null
    ) {
        lottieAnimationView.setAnimation(assetName)
        lottieAnimationView.setRepeatCount(count)
        animatorListener?.let { lottieAnimationView.addAnimatorListener(it) }
        lottieAnimationView.playAnimation()
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
    @JvmStatic
    fun useWithAssetAndImageAsset(
        lottieAnimationView: LottieAnimationView,
        assetName: String,
        imageAssetFolder: String,
        count: Int,
        animatorListener: Animator.AnimatorListener? = null
    ) {
        lottieAnimationView.imageAssetsFolder = imageAssetFolder
        lottieAnimationView.setAnimation(assetName)
        lottieAnimationView.setRepeatCount(count)
        animatorListener?.let { lottieAnimationView.addAnimatorListener(it) }
        lottieAnimationView.playAnimation()
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
    @JvmStatic
    fun useWithRaw(
        lottieAnimationView: LottieAnimationView,
        rawResId: Int,
        count: Int,
        animatorListener: Animator.AnimatorListener? = null
    ) {
        lottieAnimationView.setAnimation(rawResId)
        lottieAnimationView.setRepeatCount(count)
        animatorListener?.let { lottieAnimationView.addAnimatorListener(it) }
        lottieAnimationView.playAnimation()
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
    @JvmStatic
    fun useWithLottieTaskFromAsset(
        appCompatActivity: AppCompatActivity,
        lottieAnimationView: LottieAnimationView,
        assetName: String,
        count: Int,
        animatorListener: Animator.AnimatorListener? = null
    ) {
        val lottieCompositionFromAsset =
            LottieCompositionFactory.fromAsset(appCompatActivity, assetName)
        lottieCompositionFromAsset.addListener { p0 ->
            p0?.let {
                lottieAnimationView.setComposition(it)
            }
            lottieAnimationView.setRepeatCount(count)
            animatorListener?.let {
                lottieAnimationView.addAnimatorListener(it)
            }
            lottieAnimationView.playAnimation()
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
    @JvmStatic
    fun useWithLottieTaskFromRawResId(
        appCompatActivity: AppCompatActivity,
        lottieAnimationView: LottieAnimationView,
        rawResId: Int,
        count: Int,
        animatorListener: Animator.AnimatorListener? = null
    ) {
        val lottieCompositionFromRawRes =
            LottieCompositionFactory.fromRawRes(appCompatActivity, rawResId)
        lottieCompositionFromRawRes.addListener { p0 ->
            p0?.let {
                lottieAnimationView.setComposition(it)
            }
            lottieAnimationView.setRepeatCount(count)
            animatorListener?.let {
                lottieAnimationView.addAnimatorListener(it)
            }
            lottieAnimationView.playAnimation()
        }
    }

    /**
     * 结束动画
     *
     * @param lottieAnimationView LottieAnimationView
     */
    @JvmStatic
    fun endAnimation(lottieAnimationView: LottieAnimationView) {
        lottieAnimationView.cancelAnimation()
        lottieAnimationView.clearAnimation()
    }
}