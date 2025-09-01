package widget.location.listener;

/**
 * Created on 2025/9/1.
 *
 * @author 郑少鹏
 * @desc 定位配套原件监听
 */
public interface LocationKitListener {
    /**
     * 定位变化
     *
     * @param locationInfo 定位信息
     */
    void onLocationChanged(String locationInfo);
}