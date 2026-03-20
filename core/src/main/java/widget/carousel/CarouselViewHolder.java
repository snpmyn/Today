package widget.carousel;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zsp.core.R;

import util.view.ViewUtils;
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
    }

    /**
     * 绑定
     *
     * @param carouselItem 轮播条目
     */
    public void bind(@NonNull CarouselItem carouselItem) {
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
            WebViewKit.loadUrl(webView, carouselItem.getCarouselHtml());
            // 短点
            webView.setOnClickListener(v -> carouselListener.onItemClick(carouselItem, getBindingAdapterPosition()));
            // 长点
            webView.setOnLongClickListener(v -> {
                carouselListener.onItemLongClick(carouselItem, getBindingAdapterPosition());
                return true;
            });
        }
    }
}