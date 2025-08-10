package widget.carousel;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.zsp.core.R;

/**
 * @decs: 轮播适配器
 * @author: 郑少鹏
 * @date: 2025/8/10 16:42
 * @version: v 1.0
 */
public class CarouselAdapter extends ListAdapter<CarouselItem, CarouselViewHolder> {
    /**
     * 轮播条目布局资源 ID
     */
    private final int carouselItemLayoutResId;
    /**
     * 轮播监听
     */
    private final CarouselListener carouselListener;
    /**
     * 轮播条目回调
     */
    private static final DiffUtil.ItemCallback<CarouselItem> carouselItemCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull CarouselItem oldItem, @NonNull CarouselItem newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed.
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CarouselItem oldItem, @NonNull CarouselItem newItem) {
            return false;
        }
    };

    /**
     * constructor
     *
     * @param carouselListener 轮播监听
     */
    public CarouselAdapter(CarouselListener carouselListener) {
        this(carouselListener, R.layout.carousel_item);
    }

    /**
     * constructor
     *
     * @param carouselListener        轮播监听
     * @param carouselItemLayoutResId 轮播条目布局资源 ID
     */
    public CarouselAdapter(CarouselListener carouselListener, int carouselItemLayoutResId) {
        super(carouselItemCallback);
        this.carouselListener = carouselListener;
        this.carouselItemLayoutResId = carouselItemLayoutResId;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        return new CarouselViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(carouselItemLayoutResId, viewGroup, false), carouselListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder carouselViewHolder, int pos) {
        carouselViewHolder.bind(getItem(pos));
        carouselViewHolder.itemView.setOnHoverListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    v.setAlpha(0.5F);
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    v.setAlpha(1.0F);
                    break;
                // fall out
                default:
            }
            return false;
        });
    }
}