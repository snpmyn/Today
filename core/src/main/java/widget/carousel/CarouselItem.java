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
     * 位图描述
     */
    public String drawableDescribe;

    /**
     * constructor
     *
     * @param drawableResId    位图资源 ID
     * @param drawableDescribe 位图描述
     */
    public CarouselItem(int drawableResId, String drawableDescribe) {
        this.drawableResId = drawableResId;
        this.drawableDescribe = drawableDescribe;
    }

    public int getDrawableResId() {
        return this.drawableResId;
    }

    public String getDrawableDescribe() {
        return drawableDescribe;
    }
}