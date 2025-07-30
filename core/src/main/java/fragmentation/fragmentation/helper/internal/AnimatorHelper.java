package fragmentation.fragmentation.helper.internal;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import fragmentation.fragmentation.animation.FragmentAnimator;

/**
 * @decs: AnimatorHelper
 * @author: 郑少鹏
 * @date: 2019/5/20 9:33
 */
public final class AnimatorHelper {
    private final Context context;
    public Animation enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation;
    private Animation noneAnimation, noneAnimationFixed;
    private FragmentAnimator fragmentAnimator;

    public AnimatorHelper(Context context, FragmentAnimator fragmentAnimator) {
        this.context = context;
        notifyChanged(fragmentAnimator);
    }

    public void notifyChanged(FragmentAnimator fragmentAnimator) {
        this.fragmentAnimator = fragmentAnimator;
        initEnterAnimation();
        initExitAnimation();
        initPopEnterAnimation();
        initPopExitAnimation();
    }

    public Animation getNoneAnimation() {
        if (null == noneAnimation) {
            noneAnimation = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        }
        return noneAnimation;
    }

    public Animation getNoneAnimationFixed() {
        if (null == noneAnimationFixed) {
            noneAnimationFixed = new Animation() {
            };
        }
        return noneAnimationFixed;
    }

    @Nullable
    public Animation compatChildFragmentExitAnim(@NotNull Fragment fragment) {
        boolean flag = (((null != fragment.getTag()) && fragment.getTag().startsWith("android:switcher:") && fragment.getUserVisibleHint()) || ((null != fragment.getParentFragment()) && fragment.getParentFragment().isRemoving() && !fragment.isHidden()));
        if (flag) {
            Animation animation = new Animation() {
            };
            animation.setDuration(exitAnimation.getDuration());
            return animation;
        }
        return null;
    }

    private void initEnterAnimation() {
        if (fragmentAnimator.getEnter() == 0) {
            enterAnimation = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            enterAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getEnter());
        }
    }

    private void initExitAnimation() {
        if (fragmentAnimator.getExit() == 0) {
            exitAnimation = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            exitAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getExit());
        }
    }

    private void initPopEnterAnimation() {
        if (fragmentAnimator.getPopEnter() == 0) {
            popEnterAnimation = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            popEnterAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopEnter());
        }
    }

    private void initPopExitAnimation() {
        if (fragmentAnimator.getPopExit() == 0) {
            popExitAnimation = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            popExitAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopExit());
        }
    }
}
