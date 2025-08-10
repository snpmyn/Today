package widget.carousel;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
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
     * @param debug            调试
     * @param snap             吸附
     */
    @SuppressLint("RestrictedApi")
    public void execute(RecyclerView recyclerView, List<CarouselItem> carouselItemList, boolean debug, boolean snap) {
        // 轮播布局管理器
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager();
        carouselLayoutManager.setDebuggingEnabled(recyclerView, debug);
        // RecyclerView
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(carouselLayoutManager);
        if (debug) {
            recyclerView.setBackgroundResource(R.drawable.coloroutline_stroke_dash_r6);
        }
        if (snap) {
            // 轮播吸附辅助器
            CarouselSnapHelper carouselSnapHelper = new CarouselSnapHelper();
            carouselSnapHelper.attachToRecyclerView(recyclerView);
        }
        // 轮播适配器
        CarouselAdapter carouselAdapter = new CarouselAdapter((carouselItem, position) -> recyclerView.smoothScrollToPosition(position));
        carouselAdapter.submitList(carouselItemList);
        // RecyclerView 关联适配器
        recyclerView.setAdapter(carouselAdapter);
    }
}