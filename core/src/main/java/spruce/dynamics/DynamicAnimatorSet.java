package spruce.dynamics;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is equivalent to {@link android.view.animation.AnimationSet} for dynamic animations.
 */
public class DynamicAnimatorSet {
    private List<SpruceDynamics<?>> anim = new ArrayList<>();

    /**
     * Sets up this AnimatorSet to play all of the supplied animations at the same time.
     *
     * @param items The animations that will be started simultaneously.
     */
    public void playTogether(List<SpruceDynamics<?>> items) {
        anim = items;
    }

    /**
     * This method is used to cancel the animation.
     */
    public void cancel() {
        for (SpruceDynamics<?> anim : anim) {
            anim.cancel();
        }
    }

    /**
     * This method is used to start all queued dynamic animations.
     */
    public void start() {
        for (SpruceDynamics<?> anim : anim) {
            anim.start();
        }
    }
}