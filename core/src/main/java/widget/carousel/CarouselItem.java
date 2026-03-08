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
     * 轮播类型
     */
    public CarouselType carouselType;
    /**
     * 轮播资源 ID
     */
    public int carouselResId;
    /**
     * 轮播 HTML
     */
    public String carouselHtml;
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
     * @param carouselType     轮播类型
     * @param carouselResId    轮播资源 ID
     * @param carouselTitle    轮播标题
     * @param carouselDescribe 轮播描述
     */
    public CarouselItem(CarouselType carouselType, int carouselResId, String carouselTitle, String carouselDescribe) {
        this.carouselType = carouselType;
        this.carouselResId = carouselResId;
        this.carouselTitle = carouselTitle;
        this.carouselDescribe = carouselDescribe;
    }

    /**
     * constructor
     *
     * @param carouselType     轮播类型
     * @param carouselHtml     轮播 HTML
     * @param carouselTitle    轮播标题
     * @param carouselDescribe 轮播描述
     */
    public CarouselItem(CarouselType carouselType, String carouselHtml, String carouselTitle, String carouselDescribe) {
        this.carouselType = carouselType;
        this.carouselHtml = carouselHtml;
        this.carouselTitle = carouselTitle;
        this.carouselDescribe = carouselDescribe;
    }

    public CarouselType getCarouselType() {
        return carouselType;
    }

    public int getCarouselResId() {
        return carouselResId;
    }

    public String getCarouselHtml() {
        return carouselHtml;
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
        return ((carouselType == carouselItem.carouselType) && (carouselResId == carouselItem.carouselResId) && Objects.equals(carouselHtml, carouselItem.carouselHtml) && Objects.equals(carouselTitle, carouselItem.carouselTitle) && Objects.equals(carouselDescribe, carouselItem.carouselDescribe));
    }

    @Override
    public int hashCode() {
        return Objects.hash(carouselType, carouselResId, carouselHtml, carouselTitle, carouselDescribe);
    }
}