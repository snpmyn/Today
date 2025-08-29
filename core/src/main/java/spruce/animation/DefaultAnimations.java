package spruce.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import androidx.annotation.NonNull;

import spruce.dynamics.SpringForce;
import spruce.dynamics.SpruceDynamics;
import spruce.dynamics.SpruceSpringAnimation;

/**
 * Convenience methods for retrieving default view animators.
 */
public class DefaultAnimations {
    private static final float GROW_SCALE = 1.5F;
    private static final float SHRINK_SCALE = 0.1F;
    private static final float ORIGINAL_SCALE = 1.0F;
    private static final float FADE_AWAY_TO = 0.0F;
    private static final float FADE_IN_TO = 1.0F;
    private static final float FADE_FROM = 0.0F;
    private static final float START_ROTATION = 0.0F;
    private static final float END_ROTATION = 360.0F;

    @NonNull
    public static Animator growAnimator(View view, long duration) {
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat(View.SCALE_X, GROW_SCALE, ORIGINAL_SCALE), PropertyValuesHolder.ofFloat(View.SCALE_Y, GROW_SCALE, ORIGINAL_SCALE)).setDuration(duration);
    }

    @NonNull
    public static Animator shrinkAnimator(View view, long duration) {
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat(View.SCALE_X, SHRINK_SCALE, ORIGINAL_SCALE), PropertyValuesHolder.ofFloat(View.SCALE_Y, SHRINK_SCALE, ORIGINAL_SCALE)).setDuration(duration);
    }

    @NonNull
    public static Animator fadeAwayAnimator(View view, long duration) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, FADE_AWAY_TO).setDuration(duration);
    }

    @NonNull
    public static Animator fadeInAnimator(View view, long duration) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, FADE_FROM, FADE_IN_TO).setDuration(duration);
    }

    @NonNull
    public static Animator spinAnimator(View view, long duration) {
        return ObjectAnimator.ofFloat(view, View.ROTATION, START_ROTATION, END_ROTATION).setDuration(duration);
    }

    @NonNull
    public static SpruceSpringAnimation dynamicTranslationUpwards(View view) {
        SpruceSpringAnimation tranUp = new SpruceSpringAnimation(view, SpruceDynamics.TRANSLATION_Y).setStartValue(200f);
        tranUp.setSpring(new SpringForce());
        tranUp.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        tranUp.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        tranUp.getSpring().setFinalPosition(FADE_FROM);
        return tranUp;
    }

    @NonNull
    public static SpruceSpringAnimation dynamicFadeIn(View view) {
        SpruceSpringAnimation tranUp = new SpruceSpringAnimation(view, SpruceDynamics.ALPHA).setStartValue(FADE_FROM);
        tranUp.setSpring(new SpringForce());
        tranUp.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        tranUp.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM);
        tranUp.getSpring().setFinalPosition(FADE_IN_TO);
        return tranUp;
    }
}