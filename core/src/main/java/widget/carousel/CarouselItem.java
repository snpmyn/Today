package widget.carousel;

/**
 * @decs: 轮播条目
 * @author: 郑少鹏
 * @date: 2025/8/10 16:13
 * @version: v 1.0
 */
public class CarouselItem {
    /**
     * 位图资源 ID
     */
    public int drawableResId;

    /**
     * constructor
     *
     * @param drawableResId 位图资源 ID
     */
    public CarouselItem(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public int getDrawableResId() {
        return this.drawableResId;
    }
}