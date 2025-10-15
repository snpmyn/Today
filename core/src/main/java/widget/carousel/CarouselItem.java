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
     * 位图资源名
     */
    public String drawableResName;
    /**
     * 位图描述
     */
    public String drawableDescribe;

    /**
     * constructor
     *
     * @param drawableResId    位图资源 ID
     * @param drawableResName  位图资源名
     * @param drawableDescribe 位图描述
     */
    public CarouselItem(int drawableResId, String drawableResName, String drawableDescribe) {
        this.drawableResId = drawableResId;
        this.drawableResName = drawableResName;
        this.drawableDescribe = drawableDescribe;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public String getDrawableResName() {
        return drawableResName;
    }

    public String getDrawableDescribe() {
        return drawableDescribe;
    }
}