package spruce.sort;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InlineSort extends CorneredSort {
    private final long interObjectDelay;
    private final boolean reversed;

    /**
     * Animate child views from side to side (based on the provided corner parameter).
     *
     * @param interObjectDelay long delay between objects
     * @param reversed         boolean indicating if the selection is reversed
     * @param corner           {@link Corner Corner} value to start from
     */
    public InlineSort(long interObjectDelay, boolean reversed, Corner corner) {
        super(interObjectDelay, reversed, corner);
        this.interObjectDelay = interObjectDelay;
        this.reversed = reversed;
    }

    @Override
    public List<SpruceTimedView> getViewListWithTimeOffsets(ViewGroup parent, List<View> children) {
        List<SpruceTimedView> timedViews = new ArrayList<>();
        long currentTimeOffset = 0;
        if (reversed) {
            Collections.reverse(children);
        }
        for (View view : children) {
            timedViews.add(new SpruceTimedView(view, currentTimeOffset));
            currentTimeOffset += interObjectDelay;
        }
        return timedViews;
    }

    @Override
    public void sortChildren(ViewGroup parent, List<View> children) {
        final PointF comparisonPoint = getDistancePoint(parent, children);
        children.sort((o1, o2) -> {
            double leftHorizontalDistance = Utils.horizontalDistance(comparisonPoint, Utils.viewToPoint(o1));
            double leftVerticalDistance = Utils.verticalDistance(comparisonPoint, Utils.viewToPoint(o1));
            double rightHorizontalDistance = Utils.horizontalDistance(comparisonPoint, Utils.viewToPoint(o2));
            double rightVerticalDistance = Utils.verticalDistance(comparisonPoint, Utils.viewToPoint(o2));
            if (leftVerticalDistance < rightVerticalDistance || leftVerticalDistance == rightVerticalDistance && leftHorizontalDistance < rightHorizontalDistance) {
                return -1;
            }
            return 1;
        });
    }
}