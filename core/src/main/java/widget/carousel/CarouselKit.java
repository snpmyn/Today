package widget.carousel;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.CarouselStrategy;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.android.material.carousel.MultiBrowseCarouselStrategy;
import com.zsp.core.R;

import java.util.List;

/**
 * Created on 2025/8/10.
 *
 * @author 郑少鹏
 * @desc 轮播配套原件
 */
public class CarouselKit {
    /**
     * 执行
     *
     * @param recyclerView     RecyclerView
     * @param carouselItemList 轮播条目集
     * @param carouselStrategy 轮播策略
     *                         {@link HeroCarouselStrategy}
     *                         {@link FullScreenCarouselStrategy}
     *                         {@link MultiBrowseCarouselStrategy}
     *                         {@link com.google.android.material.carousel.UncontainedCarouselStrategy}
     * @param orientation      方向
     *                         {@link RecyclerView#HORIZONTAL}
     *                         {@link RecyclerView#VERTICAL}
     * @param debug            调试
     * @param alignment        对齐
     *                         {@link CarouselLayoutManager#ALIGNMENT_START}
     *                         {@link CarouselLayoutManager#ALIGNMENT_CENTER}
     * @param snap             吸附
     * @param left             左边距
     * @param top              上边距
     * @param right            右边距
     * @param bottom           下边距
     * @param useInDialog      在对话框中使用
     * @param carouselListener 轮播监听
     */
    @SuppressLint("RestrictedApi")
    public void execute(RecyclerView recyclerView, List<CarouselItem> carouselItemList, CarouselStrategy carouselStrategy, int orientation, boolean debug, int alignment, boolean snap, int left, int top, int right, int bottom, boolean useInDialog, CarouselListener carouselListener) {
        // 轮播布局管理器
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(carouselStrategy, orientation);
        carouselLayoutManager.setDebuggingEnabled(recyclerView, debug);
        carouselLayoutManager.setCarouselAlignment(alignment);
        // 控件
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(carouselLayoutManager);
        recyclerView.setBackgroundResource(debug ? R.drawable.color_outline_stroke_dash_r6 : 0);
        // 轮播吸附辅助器
        CarouselSnapHelper carouselSnapHelper = new CarouselSnapHelper(!snap);
        carouselSnapHelper.attachToRecyclerView(recyclerView);
        // 轮播适配器
        CarouselAdapter carouselAdapter = new CarouselAdapter(left, top, right, bottom, new CarouselListener() {
            @Override
            public void onItemClick(CarouselItem carouselItem, int position) {
                if (useInDialog) {
                    recyclerView.post(() -> {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if ((null != viewHolder) && (null != layoutManager)) {
                            int[] snapDistance = carouselSnapHelper.calculateDistanceToFinalSnap(layoutManager, viewHolder.itemView);
                            if (null != snapDistance) {
                                recyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
                            }
                        }
                    });
                } else {
                    recyclerView.smoothScrollToPosition(position);
                }
                if (null != carouselListener) {
                    carouselListener.onItemClick(carouselItem, position);
                }
            }

            @Override
            public void onItemLongClick(CarouselItem carouselItem, int position) {
                if (null != carouselListener) {
                    carouselListener.onItemLongClick(carouselItem, position);
                }
            }
        });
        carouselAdapter.submitList(carouselItemList);
        // RecyclerView 关联适配器
        recyclerView.setAdapter(carouselAdapter);
    }
}