package widget.dialog.bocdialog.base;

import android.content.Context;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import util.log.LogUtils;

/**
 * Created on 2020-08-21
 *
 * @author zsp
 * @desc BOC 单例对话框基类
 * <p>
 * 继承 {@link BaseBocDialog} 实现对话框，关闭时调 {@link #dismiss()}。
 * 继承 {@link BaseBocInstanceDialog} 实现对话框，关闭时调 {@link #handle(Class)}。
 */
public abstract class BaseBocInstanceDialog extends BaseBocDialog {
    private static final List<BaseBocInstanceDialog> CURRENT_BASE_BOC_INSTANCE_DIALOGS = new ArrayList<>();

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    protected BaseBocInstanceDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
        handle(this.getClass());
        setCurrentDialog();
    }

    /**
     * 获取当前对话框
     *
     * @param c 类
     * @return 当前对话框
     */
    private static @Nullable BaseBocInstanceDialog getCurrentDialog(Class<?> c) {
        for (BaseBocInstanceDialog baseInstanceDialog : CURRENT_BASE_BOC_INSTANCE_DIALOGS) {
            if (baseInstanceDialog.getClass() == c) {
                return baseInstanceDialog;
            }
        }
        return null;
    }

    /**
     * 设置当前对话框
     */
    private void setCurrentDialog() {
        BaseBocInstanceDialog baseInstanceDialog;
        for (int i = 0; i < CURRENT_BASE_BOC_INSTANCE_DIALOGS.size(); i++) {
            baseInstanceDialog = CURRENT_BASE_BOC_INSTANCE_DIALOGS.get(i);
            if (baseInstanceDialog.getClass() == this.getClass()) {
                CURRENT_BASE_BOC_INSTANCE_DIALOGS.remove(baseInstanceDialog);
                i--;
            }
        }
        CURRENT_BASE_BOC_INSTANCE_DIALOGS.add(this);
    }

    /**
     * 处理
     *
     * @param c 类
     */
    public void handle(Class<?> c) {
        try {
            BaseBocInstanceDialog baseInstanceDialog = getCurrentDialog(c);
            if (null != baseInstanceDialog) {
                if (baseInstanceDialog.isShowing()) {
                    baseInstanceDialog.dismiss();
                }
                CURRENT_BASE_BOC_INSTANCE_DIALOGS.remove(baseInstanceDialog);
            }
        } catch (Exception e) {
            LogUtils.exception(e);
        }
    }
}