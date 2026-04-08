package widget.spruce

import android.animation.ObjectAnimator
import androidx.recyclerview.widget.RecyclerView
import spruce.Spruce
import spruce.animation.DefaultAnimations
import spruce.sort.DefaultSort
import spruce.sort.LinearSort

/**
 * Created on 2026/4/8.
 * @author 郑少鹏
 * @desc
 */
class SpruceKit {
    companion object {
        /**
         * 默排序
         *
         * @param recyclerView     控件
         * @param interObjectDelay 实体整型延迟
         * @param duration         时长
         */
        fun defaultSort(recyclerView: RecyclerView, interObjectDelay: Long, duration: Long) {
            Spruce.SpruceBuilder(recyclerView).apply {
                sortWith(DefaultSort(interObjectDelay))
                animateWith(
                    DefaultAnimations.shrinkAnimator(recyclerView, duration),
                    ObjectAnimator.ofFloat(
                        recyclerView, "translationX", -recyclerView.width.toFloat(), 0.0F
                    ).setDuration(duration)
                )
                start()
            }
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
        fun linearSort(
            recyclerView: RecyclerView,
            interObjectDelay: Long,
            reversed: Boolean,
            direction: LinearSort.Direction,
            duration: Long
        ) {
            Spruce.SpruceBuilder(recyclerView).apply {
                sortWith(LinearSort(interObjectDelay, reversed, direction))
                animateWith(DefaultAnimations.fadeInAnimator(recyclerView, duration))
                start()
            }
        }
    }
}