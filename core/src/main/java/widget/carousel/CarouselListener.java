package widget.carousel;

/**
 * @decs: 轮播监听
 * @author: 郑少鹏
 * @date: 2025/8/10 16:23
 * @version: v 1.0
 */
public interface CarouselListener {
    /**
     * 短点
     *
     * @param carouselItem 轮播条目
     * @param position     位置
     */
    default void onItemClick(CarouselItem carouselItem, int position) {
    }

    /**
     * 长点
     *
     * @param carouselItem 轮播条目
     * @param position     位置
     */
    default void onItemLongClick(CarouselItem carouselItem, int position) {
    }
}