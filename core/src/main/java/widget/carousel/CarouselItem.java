package widget.carousel;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * @decs: 轮播条目
 * @author: 郑少鹏
 * @date: 2025/8/10 16:13
 * @version: v 1.0
 */
public class CarouselItem {
    /**
     * 显示图像
     */
    public boolean showImage;
    /**
     * 轮播资源 ID
     */
    public int carouselResId;
    /**
     * 轮播标题
     */
    public String carouselTitle;
    /**
     * 轮播描述
     */
    public String carouselDescribe;

    /**
     * constructor
     *
     * @param showImage        显示图像
     * @param carouselResId    轮播资源 ID
     * @param carouselTitle    轮播标题
     * @param carouselDescribe 轮播描述
     */
    public CarouselItem(boolean showImage, int carouselResId, String carouselTitle, String carouselDescribe) {
        this.showImage = showImage;
        this.carouselResId = carouselResId;
        this.carouselTitle = carouselTitle;
        this.carouselDescribe = carouselDescribe;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public int getCarouselResId() {
        return carouselResId;
    }

    public String getCarouselTitle() {
        return carouselTitle;
    }

    public String getCarouselDescribe() {
        return carouselDescribe;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CarouselItem)) {
            return false;
        }
        CarouselItem carouselItem = (CarouselItem) obj;
        return ((showImage == carouselItem.showImage) && (carouselResId == carouselItem.carouselResId) && Objects.equals(carouselTitle, carouselItem.carouselTitle) && Objects.equals(carouselDescribe, carouselItem.carouselDescribe));
    }

    @Override
    public int hashCode() {
        return Objects.hash(showImage, carouselResId, carouselTitle, carouselDescribe);
    }
}