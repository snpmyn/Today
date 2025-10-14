package widget.carousel;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.zsp.core.R;

import util.density.DensityUtils;

/**
 * @decs: 轮播适配器
 * @author: 郑少鹏
 * @date: 2025/8/10 16:42
 * @version: v 1.0
 */
public class CarouselAdapter extends ListAdapter<CarouselItem, CarouselViewHolder> {
    /**
     * 左边距
     */
    private final int left;
    /**
     * 上边距
     */
    private final int top;
    /**
     * 右边距
     */
    private final int right;
    /**
     * 下边距
     */
    private final int bottom;
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
     * @param left             左边距
     * @param top              上边距
     * @param right            右边距
     * @param bottom           下边距
     * @param carouselListener 轮播监听
     */
    public CarouselAdapter(int left, int top, int right, int bottom, CarouselListener carouselListener) {
        super(carouselItemCallback);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.carouselListener = carouselListener;
        this.carouselItemLayoutResId = R.layout.carousel_item;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        return new CarouselViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(carouselItemLayoutResId, viewGroup, false), carouselListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder carouselViewHolder, int pos) {
        carouselViewHolder.bind(getItem(pos));
        // 适配横 / 竖向
        int itemCount = getItemCount();
        int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
        if (pos == 0) {
            // 头条
            // 右 / 下边距
            marginRight = right;
            marginBottom = bottom;
        } else if (pos == (itemCount - 1)) {
            // 末条
            // 左 / 上边距
            marginLeft = left;
            marginTop = top;
        } else {
            // 中条
            // 四边距
            marginLeft = left;
            marginTop = top;
            marginRight = right;
            marginBottom = bottom;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(DensityUtils.dipToPxByInt(marginLeft), DensityUtils.dipToPxByInt(marginTop), DensityUtils.dipToPxByInt(marginRight), DensityUtils.dipToPxByInt(marginBottom));
        carouselViewHolder.itemView.setLayoutParams(layoutParams);
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