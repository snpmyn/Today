package widget.dialog.bocdialog.message.listener;

import android.view.View;

import widget.dialog.bocdialog.message.RightAngleMessageDialog;

/**
 * Created on 2020-09-03
 *
 * @author zsp
 * @desc 直角消息对话框按钮点击监听
 */
public interface OnRightAngleMessageDialogButtonClickListener {
    /**
     * 按钮点击
     *
     * @param view                    视图
     * @param rightAngleMessageDialog 直角消息对话框
     */
    void buttonClick(View view, RightAngleMessageDialog rightAngleMessageDialog);
}
