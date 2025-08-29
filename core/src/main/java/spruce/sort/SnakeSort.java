package spruce.sort;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SnakeSort extends CorneredSort {
    private final long interObjectDelay;
    private final boolean reversed;

    /**
     * Animate child views from side to side (based on the provided corner parameter), alternating left to right and right to left on each row.
     *
     * @param interObjectDelay long delay between objects
     * @param reversed         boolean indicating if the selection is reversed
     * @param corner           {@link Corner Corner} value to start from
     */
    public SnakeSort(long interObjectDelay, boolean reversed, Corner corner) {
        super(interObjectDelay, reversed, corner);
        this.interObjectDelay = interObjectDelay;
        this.reversed = reversed;
    }

    @Override
    public List<SpruceTimedView> getViewListWithTimeOffsets(ViewGroup parent, List<View> children) {
        final PointF comparisonPoint = getDistancePoint(parent, children);
        List<SpruceTimedView> timedViews = new ArrayList<>();
        long currentTimeOffset = 0;
        // Calculate all possible vertical distances from the point of comparison.
        final List<Float> verticalDistances = new ArrayList<>();
        for (View child : children) {
            float d = Utils.verticalDistance(comparisonPoint, Utils.viewToPoint(child));
            if (!verticalDistances.contains(d)) {
                verticalDistances.add(d);
            }
        }
        // Sort these so we can find the row index by the vertical distance.
        Collections.sort(verticalDistances);
        children.sort((left, right) -> {
            double leftHorizontalDistance = Utils.horizontalDistance(comparisonPoint, Utils.viewToPoint(left));
            double leftVerticalDistance = Utils.verticalDistance(comparisonPoint, Utils.viewToPoint(left));
            double rightHorizontalDistance = Utils.horizontalDistance(comparisonPoint, Utils.viewToPoint(right));
            double rightVerticalDistance = Utils.verticalDistance(comparisonPoint, Utils.viewToPoint(right));
            // Difference in vertical distance takes priority.
            if (leftVerticalDistance < rightVerticalDistance) {
                return -1;
            } else if (leftVerticalDistance > rightVerticalDistance) {
                return 1;
            }
            // If the are in the same row, find the row index.
            int row = verticalDistances.indexOf((float) leftVerticalDistance);
            if (leftHorizontalDistance < rightHorizontalDistance) {
                return row % 2 == 0 ? -1 : 1;
            } else {
                return row % 2 == 0 ? 1 : -1;
            }
        });
        if (reversed) {
            Collections.reverse(children);
        }
        for (View view : children) {
            timedViews.add(new SpruceTimedView(view, currentTimeOffset));
            currentTimeOffset += interObjectDelay;
        }
        return timedViews;
    }
}