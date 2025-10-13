package widget.carousel;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.CarouselStrategy;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.android.material.carousel.MultiBrowseCarouselStrategy;
import com.zsp.core.R;

import java.util.List;

import widget.dialog.materialalertdialog.PictureInfoMaterialAlertDialogKit;

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
     * @param appCompatActivity RecyclerView
     * @param recyclerView      RecyclerView
     * @param carouselItemList  轮播条目集
     * @param carouselStrategy  轮播策略
     *                          {@link HeroCarouselStrategy}
     *                          {@link FullScreenCarouselStrategy}
     *                          {@link MultiBrowseCarouselStrategy}
     *                          {@link com.google.android.material.carousel.UncontainedCarouselStrategy}
     * @param debug             调试
     * @param alignment         对齐
     *                          {@link CarouselLayoutManager#ALIGNMENT_START}
     *                          {@link CarouselLayoutManager#ALIGNMENT_CENTER}
     * @param snap              吸附
     */
    @SuppressLint("RestrictedApi")
    public void execute(AppCompatActivity appCompatActivity, RecyclerView recyclerView, List<CarouselItem> carouselItemList, CarouselStrategy carouselStrategy, boolean debug, int alignment, boolean snap) {
        // 轮播布局管理器
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(carouselStrategy);
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
        CarouselAdapter carouselAdapter = new CarouselAdapter(new CarouselListener() {
            @Override
            public void onItemClick(CarouselItem carouselItem, int position) {
                recyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onItemLongClick(CarouselItem carouselItem, int position) {
                PictureInfoMaterialAlertDialogKit.getInstance().show(appCompatActivity, carouselItem);
            }
        });
        carouselAdapter.submitList(carouselItemList);
        // RecyclerView 关联适配器
        recyclerView.setAdapter(carouselAdapter);
    }
}