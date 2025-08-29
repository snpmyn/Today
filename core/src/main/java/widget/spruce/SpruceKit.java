package widget.spruce;

import android.animation.ObjectAnimator;

import androidx.recyclerview.widget.RecyclerView;

import spruce.Spruce;
import spruce.animation.DefaultAnimations;
import spruce.sort.DefaultSort;
import spruce.sort.LinearSort;

/**
 * Created on 2019/6/26.
 *
 * @author 郑少鹏
 * @desc Spruce 配套元件
 */
public class SpruceKit {
    /**
     * 默排序
     *
     * @param recyclerView     控件
     * @param interObjectDelay 实体整型延迟
     * @param duration         时长
     */
    public void defaultSort(RecyclerView recyclerView, long interObjectDelay, long duration) {
        new Spruce.SpruceBuilder(recyclerView).sortWith(new DefaultSort(interObjectDelay)).animateWith(DefaultAnimations.shrinkAnimator(recyclerView, duration), ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0.0F).setDuration(duration)).start();
    }

    /**
     * 线性排序
     *
     * @param recyclerView     控件
     * @param interObjectDelay 实体整型延迟
     * @param reversed         反转
     * @param direction        方向
     * @param duration         时长
     */
    public void linearSort(RecyclerView recyclerView, long interObjectDelay, boolean reversed, LinearSort.Direction direction, long duration) {
        new Spruce.SpruceBuilder(recyclerView).sortWith(new LinearSort(interObjectDelay, reversed, direction)).animateWith(DefaultAnimations.fadeInAnimator(recyclerView, duration)).start();
    }
}