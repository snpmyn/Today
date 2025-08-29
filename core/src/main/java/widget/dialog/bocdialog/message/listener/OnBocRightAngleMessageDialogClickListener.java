package widget.dialog.bocdialog.message.listener;

import android.view.View;

import widget.dialog.bocdialog.message.BocRightAngleMessageDialog;

/**
 * Created on 2020-09-03
 *
 * @author zsp
 * @desc BOC 直角消息对话框点击监听
 */
public interface OnBocRightAngleMessageDialogClickListener {
    /**
     * 按钮点击
     *
     * @param view                       视图
     * @param bocRightAngleMessageDialog BOC 直角消息对话框
     */
    void buttonClick(View view, BocRightAngleMessageDialog bocRightAngleMessageDialog);
}