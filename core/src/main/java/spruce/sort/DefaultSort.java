package spruce.sort;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultSort extends SortFunction {
    private final long interObjectDelay;

    /**
     * Default sort; handles views with a provided offset delay
     *
     * @param interObjectDelay (long) delay between object animations
     */
    public DefaultSort(long interObjectDelay) {
        this.interObjectDelay = interObjectDelay;
    }

    @Override
    public List<SpruceTimedView> getViewListWithTimeOffsets(ViewGroup parent, @NonNull List<View> children) {
        List<SpruceTimedView> childTimedViews = new ArrayList<>();
        long currentTimeOffset = 0L;
        for (View childView : children) {
            childTimedViews.add(new SpruceTimedView(childView, currentTimeOffset));
            currentTimeOffset += interObjectDelay;
        }
        return childTimedViews;
    }

    @Override
    public void sortChildren(ViewGroup parent, List<View> children) {
        // Do nothing, as the original order is maintained
    }
}