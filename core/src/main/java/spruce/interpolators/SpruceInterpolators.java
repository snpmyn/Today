package spruce.interpolators;

import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

public class SpruceInterpolators {
    /**
     * Ease Interpolator
     */
    public static final Interpolator EASE = new PathInterpolator(0.250F, 0.100F, 0.250F, 1.000F);
    /**
     * Ease-In Interpolator
     */
    public static final Interpolator EASE_IN = new PathInterpolator(0.420F, 0.000F, 1.000F, 1.000F);
    /**
     * Ease-In Interpolator
     */
    public static final Interpolator EASE_OUT = new PathInterpolator(0.000F, 0.000F, 0.580F, 1.000F);
    /**
     * Ease-In-Out Interpolator
     */
    public static final Interpolator EASE_IN_OUT = new PathInterpolator(0.420F, 0.000F, 0.580F, 1.000F);
    /**
     * Ease-In-Quad Interpolator
     */
    public static final Interpolator EASE_IN_QUAD = new PathInterpolator(0.550F, 0.085F, 0.680F, 0.530F);
    /**
     * Ease-In-Cubic Interpolator
     */
    public static final Interpolator EASE_IN_CUBIC = new PathInterpolator(0.550F, 0.055F, 0.675F, 0.190F);
    /**
     * Ease-In-Quart Interpolator
     */
    public static final Interpolator EASE_IN_QUART = new PathInterpolator(0.895F, 0.030F, 0.685F, 0.220F);
    /**
     * Ease-In-Quint Interpolator
     */
    public static final Interpolator EASE_IN_QUINT = new PathInterpolator(0.755F, 0.050F, 0.855F, 0.060F);
    /**
     * Ease-In-Sine Interpolator
     */
    public static final Interpolator EASE_IN_SINE = new PathInterpolator(0.470F, 0.000F, 0.745F, 0.715F);
    /**
     * Ease-In-Exop Interpolator
     */
    public static final Interpolator EASE_IN_EXPO = new PathInterpolator(0.950F, 0.050F, 0.795F, 0.035F);
    /**
     * Ease-In-Circ Interpolator
     */
    public static final Interpolator EASE_IN_CIRC = new PathInterpolator(0.600F, 0.040F, 0.980F, 0.335F);
    /**
     * Ease-In-Back Interpolator
     */
    public static final Interpolator EASE_IN_BACK = new PathInterpolator(0.600F, -0.280F, 0.735F, 0.045F);
    /**
     * Ease-Out-Quad Interpolator
     */
    public static final Interpolator EASE_OUT_QUAD = new PathInterpolator(0.250F, 0.460F, 0.450F, 0.940F);
    /**
     * Ease-Out-Cubic Interpolator
     */
    public static final Interpolator EASE_OUT_CUBIC = new PathInterpolator(0.215F, 0.610F, 0.355F, 1.000F);
    /**
     * Ease-Out-Quart Interpolator
     */
    public static final Interpolator EASE_OUT_QUART = new PathInterpolator(0.165F, 0.840F, 0.440F, 1.000F);
    /**
     * Ease-Out-Quint Interpolator
     */
    public static final Interpolator EASE_OUT_QUINT = new PathInterpolator(0.230F, 1.000F, 0.320F, 1.000F);
    /**
     * Ease-Out-Sine Interpolator
     */
    public static final Interpolator EASE_OUT_SINE = new PathInterpolator(0.390F, 0.575F, 0.565F, 1.000F);
    /**
     * Ease-Out-Exop Interpolator
     */
    public static final Interpolator EASE_OUT_EXPO = new PathInterpolator(0.190F, 1.000F, 0.220F, 1.000F);
    /**
     * Ease-Out-Circ Interpolator
     */
    public static final Interpolator EASE_OUT_CIRC = new PathInterpolator(0.075F, 0.820F, 0.165F, 1.000F);
    /**
     * Ease-Out-Back Interpolator
     */
    public static final Interpolator EASE_OUT_BACK = new PathInterpolator(0.175F, 0.885F, 0.320F, 1.275F);
    /**
     * Ease-In-Out-Quad Interpolator
     */
    public static final Interpolator EASE_IN_OUT_QUAD = new PathInterpolator(0.455F, 0.030F, 0.515F, 0.955F);
    /**
     * Ease-In-Out-Cubic Interpolator
     */
    public static final Interpolator EASE_IN_OUT_CUBIC = new PathInterpolator(0.645F, 0.045F, 0.355F, 1.000F);
    /**
     * Ease-In-Out-Quart Interpolator
     */
    public static final Interpolator EASE_IN_OUT_QUART = new PathInterpolator(0.770F, 0.000F, 0.175F, 1.000F);
    /**
     * Ease-In-Out-Quint Interpolator
     */
    public static final Interpolator EASE_IN_OUT_QUINT = new PathInterpolator(0.770F, 0.000F, 0.175F, 1.000F);
    /**
     * Ease-In-Out-Sine Interpolator
     */
    public static final Interpolator EASE_IN_OUT_SINE = new PathInterpolator(0.445F, 0.050F, 0.550F, 0.950F);
    /**
     * Ease-In-Out-Expo Interpolator
     */
    public static final Interpolator EASE_IN_OUT_EXPO = new PathInterpolator(1.000F, 0.000F, 0.000F, 1.000F);
    /**
     * Ease-In-Out-Circ Interpolator
     */
    public static final Interpolator EASE_IN_OUT_CIRC = new PathInterpolator(0.785F, 0.135F, 0.150F, 0.860F);
    /**
     * Ease-In-Out-Back Interpolator
     */
    public static final Interpolator EASE_IN_OUT_BACK = new PathInterpolator(0.680F, -0.550F, 0.265F, 1.550F);
}
