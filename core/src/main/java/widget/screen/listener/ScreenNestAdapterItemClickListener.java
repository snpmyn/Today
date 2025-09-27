package widget.screen.listener;

import android.view.View;

/**
 * Created on 2019/9/26.
 *
 * @author 郑少鹏
 * @desc 筛选嵌套适配器条目短点监听
 */
public interface ScreenNestAdapterItemClickListener {
    /**
     * 条目短点
     *
     * @param view           视图
     * @param classification 类别
     * @param condition      条件
     * @param selected       选否
     */
    void onItemClick(View view, String classification, String condition, boolean selected);

    /**
     * 条目互斥短点
     *
     * @param classification 类别
     */
    void onItemMutuallyExclusiveClick(String classification);

    /**
     * 条目展开 / 折叠短点
     *
     * @param classification 类别
     * @param condition      条件
     * @param unfold         展开
     */
    void onItemUnfoldAndFoldClick(String classification, String condition, boolean unfold);
}