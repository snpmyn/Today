package widget.bocdialog.message.listener;

import android.view.View;

import widget.bocdialog.message.RoundCornerMessageDialog;

/**
 * Created on 2020-09-03
 *
 * @author zsp
 * @desc 圆角消息对话框按钮点击监听
 */
public interface OnRoundCornerMessageDialogButtonClickListener {
    /**
     * 按钮点击
     *
     * @param view                     视图
     * @param roundCornerMessageDialog 圆角消息对话框
     */
    void buttonClick(View view, RoundCornerMessageDialog roundCornerMessageDialog);
}
