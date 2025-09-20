package util.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.Button;

/**
 * Created on 2025/9/14.
 *
 * @author 郑少鹏
 * @desc 呼吸动画配套原件
 */
public class BreatheAnimationKit {
    private static AnimatorSet animatorSet;

    /**
     * 开始
     *
     * @param button        按钮
     * @param fadeDuration  透明度动画时长
     * @param scaleDuration 缩放动画时长
     */
    public static void start(final Button button, long fadeDuration, long scaleDuration) {
        // 透明度动画
        // 模仿呼吸效果
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(button, "alpha", 1.0F, 0.5F);
        // 循环播放
        fadeInOut.setRepeatCount(ValueAnimator.INFINITE);
        // 动画反向播放
        fadeInOut.setRepeatMode(ValueAnimator.REVERSE);
        // 使用参数控制透明度动画持续时间
        fadeInOut.setDuration(fadeDuration);
        // 缩放动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0F, 1.1F);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0F, 1.1F);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        // 使用参数控制缩放动画持续时间
        scaleX.setDuration(scaleDuration);
        scaleY.setDuration(scaleDuration);
        // 动画集合
        // 多个动画同步播放
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeInOut, scaleX, scaleY);
        animatorSet.start();
    }

    /**
     * 停止
     *
     * @param button        按钮
     * @param resetDuration 复原动画时长
     */
    public static void stop(final Button button, long resetDuration) {
        // 停止动画并复原
        if (animatorSet != null && animatorSet.isRunning()) {
            // 取消正在播放的动画
            animatorSet.cancel();
        }
        // 恢复按钮的透明度和缩放属性
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 1.0F);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0F);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0F);
        AnimatorSet resetAnimatorSet = new AnimatorSet();
        resetAnimatorSet.playTogether(fadeIn, scaleX, scaleY);
        // 使用参数控制复原动画时长
        resetAnimatorSet.setDuration(resetDuration);
        resetAnimatorSet.start();
    }
}