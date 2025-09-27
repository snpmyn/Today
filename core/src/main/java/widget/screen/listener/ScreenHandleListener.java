package widget.screen.listener;

import android.view.View;

/**
 * Created on 2019/9/26.
 *
 * @author 郑少鹏
 * @desc 筛选操作监听
 */
public interface ScreenHandleListener {
    /**
     * 点
     *
     * @param view           视图
     * @param classification 类别
     * @param condition      条件
     * @param selected       选否
     */
    void click(View view, String classification, String condition, boolean selected);

    /**
     * 重置
     */
    void reset();

    /**
     * 确定
     */
    void ensure();
}