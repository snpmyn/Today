package widget.floatingactionbutton.kit;

import androidx.annotation.NonNull;

import widget.floatingactionbutton.DraggableFloatingActionButton;

/**
 * Created on 2025/10/4.
 *
 * @author 郑少鹏
 * @desc 可拖动浮动操作按钮配套原件
 */
public class DraggableFloatingActionButtonKit {
    /**
     * 执行
     *
     * @param draggableFloatingActionButton 可拖动浮动操作按钮
     * @param marginX                       左右边缘距离
     * @param marginY                       上下边缘距离
     */
    public static void execute(@NonNull DraggableFloatingActionButton draggableFloatingActionButton, float marginX, float marginY) {
        draggableFloatingActionButton.setEdgeMode(DraggableFloatingActionButton.EdgeMode.NONE);
        draggableFloatingActionButton.setEdgeMarginX(marginX);
        draggableFloatingActionButton.setEdgeMarginY(marginY);
    }
}