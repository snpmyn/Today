package spruce.sort;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * Utility class for distance calculations and view to point conversion
 */
class Utils {
    /**
     * Get the euclidean distance between two points
     *
     * @param firstPoint  PointF object
     * @param secondPoint PointF object
     * @return float value representing the distance in a straight line between two points
     */
    public static float euclideanDistance(@NonNull PointF firstPoint, @NonNull PointF secondPoint) {
        return PointF.length(secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
    }

    /**
     * Get the horizontal, or x distance between two points
     *
     * @param firstPoint  PointF object
     * @param secondPoint PointF object
     * @return float value representing the horizontal (or x) distance between two points
     */
    public static float horizontalDistance(@NonNull PointF firstPoint, @NonNull PointF secondPoint) {
        return Math.abs(secondPoint.x - firstPoint.x);
    }

    /**
     * Get the vertical, or y distance between two points
     *
     * @param firstPoint  PointF object
     * @param secondPoint PointF object
     * @return float value representing the vertical (or y) distance between two points
     */
    public static float verticalDistance(@NonNull PointF firstPoint, @NonNull PointF secondPoint) {
        return Math.abs(secondPoint.y - firstPoint.y);
    }

    /**
     * Convert a view into it's coordinates as a Point
     *
     * @param view Object
     * @return PointF containing the x, y coordinates of the view
     */
    @NonNull
    @Contract("_ -> new")
    public static PointF viewToPoint(@NonNull View view) {
        return new PointF(Math.round(view.getX()), Math.round(view.getY()));
    }
}