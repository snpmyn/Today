package widget.carousel;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zsp.core.R;

import util.view.ViewUtils;
import widget.nest.NestScrollConflictHelper;
import widget.webview.WebViewKit;

/**
 * @decs: 轮播视图持有器
 * @author: 郑少鹏
 * @date: 2025/8/10 16:36
 * @version: v 1.0
 */
public class CarouselViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    private final ImageView imageView;
    private final WebView webView;
    private final CarouselListener carouselListener;
    private final NestScrollConflictHelper nestScrollConflictHelper;

    /**
     * constructor
     *
     * @param itemView         条目视图
     * @param carouselListener 轮播监听
     */
    public CarouselViewHolder(View itemView, CarouselListener carouselListener) {
        super(itemView);
        this.textView = itemView.findViewById(R.id.carouselItemTv);
        this.imageView = itemView.findViewById(R.id.carouselItemIv);
        this.webView = itemView.findViewById(R.id.carouselItemWv);
        this.carouselListener = carouselListener;
        this.nestScrollConflictHelper = new NestScrollConflictHelper(itemView);
    }

    /**
     * 绑定
     *
     * @param carouselItem 轮播条目
     * @param orientation  方向
     *                     {@link RecyclerView#HORIZONTAL}
     *                     {@link RecyclerView#VERTICAL}
     */
    @SuppressLint("ClickableViewAccessibility")
    public void bind(@NonNull CarouselItem carouselItem, int orientation) {
        if (carouselItem.getCarouselType() == CarouselType.IMAGE) {
            ViewUtils.hideView(textView, View.GONE);
            ViewUtils.hideView(webView, View.GONE);
            ViewUtils.showView(imageView);
            Glide.with(imageView.getContext()).load(carouselItem.getCarouselResId()).centerCrop().into(imageView);
            // 短点
            itemView.setOnClickListener(v -> carouselListener.onItemClick(carouselItem, getBindingAdapterPosition()));
            // 长点
            itemView.setOnLongClickListener(v -> {
                carouselListener.onItemLongClick(carouselItem, getBindingAdapterPosition());
                return true;
            });
        } else if (carouselItem.getCarouselType() == CarouselType.TEXT) {
            ViewUtils.hideView(imageView, View.GONE);
            ViewUtils.hideView(webView, View.GONE);
            ViewUtils.showView(textView);
            textView.setText(carouselItem.getCarouselDescribe());
            // 短点
            textView.setOnClickListener(v -> carouselListener.onItemClick(carouselItem, getBindingAdapterPosition()));
            // 长点
            textView.setOnLongClickListener(v -> {
                carouselListener.onItemLongClick(carouselItem, getBindingAdapterPosition());
                return true;
            });
        } else if (carouselItem.getCarouselType() == CarouselType.HTML) {
            ViewUtils.hideView(textView, View.GONE);
            ViewUtils.hideView(imageView, View.GONE);
            ViewUtils.showView(webView);
            WebViewKit.Companion.loadUrl(webView, carouselItem.getCarouselHtml());
            // 短点
            webView.setOnClickListener(v -> carouselListener.onItemClick(carouselItem, getBindingAdapterPosition()));
            // 长点
            webView.setOnLongClickListener(v -> {
                carouselListener.onItemLongClick(carouselItem, getBindingAdapterPosition());
                return true;
            });
            // 触摸监听
            webView.setOnTouchListener((v, event) -> {
                // 处理触摸冲突
                nestScrollConflictHelper.handleTouch(v, event, orientation == RecyclerView.VERTICAL);
                // 点击事件（防止丢失）
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    carouselListener.onItemClick(carouselItem, getBindingAdapterPosition());
                }
                return false;
            });
        }
    }
}