package widget.dialog.bocdialog.message.listener;

import android.view.View;

import widget.dialog.bocdialog.message.BocRoundCornerMessageDialog;

/**
 * Created on 2020-09-03
 *
 * @author zsp
 * @desc 圆角消息对话框点击监听
 */
public interface OnBocRoundCornerMessageDialogClickListener {
    /**
     * 按钮点击
     *
     * @param view                        视图
     * @param bocRoundCornerMessageDialog BOC 圆角消息对话框
     */
    void buttonClick(View view, BocRoundCornerMessageDialog bocRoundCornerMessageDialog);
}