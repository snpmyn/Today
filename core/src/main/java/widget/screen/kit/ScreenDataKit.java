package widget.screen.kit;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import util.list.ListUtils;

/**
 * Created on 2019/6/12.
 *
 * @author 郑少鹏
 * @desc ScreenDataKit
 */
public class ScreenDataKit {
    /**
     * 单选打包
     *
     * @param stringList          数据
     * @param selected            选否
     * @param correspondingValues 对应值
     */
    public void singleSelectPack(@NonNull List<Object> stringList, boolean selected, Object... correspondingValues) {
        if (ListUtils.listIsNotEmpty(stringList)) {
            stringList.clear();
        }
        if (selected) {
            stringList.addAll(Arrays.asList(correspondingValues));
        }
    }

    /**
     * 多选打包
     *
     * @param stringList          数据
     * @param selected            选否
     * @param correspondingValues 对应值
     */
    public void multiSelectPack(List<Object> stringList, boolean selected, @NonNull Object... correspondingValues) {
        for (Object object : correspondingValues) {
            if (selected) {
                if (stringList.contains(object)) {
                    return;
                }
                stringList.add(object);
            } else {
                stringList.remove(object);
            }
        }
    }
}