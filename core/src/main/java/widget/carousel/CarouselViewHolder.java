package widget.carousel;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zsp.core.R;

/**
 * @decs: 轮播视图持有器
 * @author: 郑少鹏
 * @date: 2025/8/10 16:36
 * @version: v 1.0
 */
public class CarouselViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final CarouselListener carouselListener;

    /**
     * constructor
     *
     * @param itemView         条目视图
     * @param carouselListener 轮播监听
     */
    public CarouselViewHolder(View itemView, CarouselListener carouselListener) {
        super(itemView);
        this.imageView = itemView.findViewById(R.id.carouselItemIv);
        this.carouselListener = carouselListener;
    }

    /**
     * 绑定
     *
     * @param carouselItem 轮播条目
     */
    public void bind(@NonNull CarouselItem carouselItem) {
        Glide.with(imageView.getContext()).load(carouselItem.getDrawableResId()).centerCrop().into(imageView);
        // 短点
        itemView.setOnClickListener(v -> carouselListener.onItemClick(carouselItem, getBindingAdapterPosition()));
        // 长点
        itemView.setOnLongClickListener(v -> {
            carouselListener.onItemLongClick(carouselItem, getBindingAdapterPosition());
            return true;
        });
    }
}