package widget.dialog.materialalertdialog;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 2025/10/4.
 *
 * @author 郑少鹏
 * @desc 两次确认材料对话框配套元件
 */
public class DoubleConfirmationMaterialAlertDialogKit {
    public static DoubleConfirmationMaterialAlertDialogKit getInstance() {
        return DoubleConfirmationMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity                                活动
     * @param firstTitle                                       初次标题
     * @param firstMessage                                     初次内容
     * @param firstPositiveButtonText                          初次积极按钮文本
     * @param firstNegativeButtonText                          初次消极按钮文本
     * @param secondTitle                                      二次标题
     * @param secondMessage                                    二次内容
     * @param secondPositiveButtonText                         二次积极按钮文本
     * @param secondNegativeButtonText                         二次消极按钮文本
     * @param doubleConfirmationMaterialAlertDialogKitListener 两次确认材料对话框配套元件监听
     */
    public void show(AppCompatActivity appCompatActivity, String firstTitle, String firstMessage, String firstPositiveButtonText, String firstNegativeButtonText, String secondTitle, String secondMessage, String secondPositiveButtonText, String secondNegativeButtonText, DoubleConfirmationMaterialAlertDialogKitListener doubleConfirmationMaterialAlertDialogKitListener) {
        new MaterialAlertDialogBuilderKit(appCompatActivity).setTitle(firstTitle).setMessage(firstMessage).setPositiveButton(firstPositiveButtonText, (dialog, which) -> {
            dialog.dismiss();
            new MaterialAlertDialogBuilderKit(appCompatActivity).setTitle(secondTitle).setMessage(secondMessage).setPositiveButton(secondPositiveButtonText, (dialog2, which2) -> {
                dialog2.dismiss();
                if (null != doubleConfirmationMaterialAlertDialogKitListener) {
                    doubleConfirmationMaterialAlertDialogKitListener.execute(dialog2);
                }
            }).setNegativeButton(secondNegativeButtonText, (dialog1, which1) -> dialog1.dismiss()).setCancelable(false).show();
        }).setNegativeButton(firstNegativeButtonText, (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
    }

    /**
     * 两次确认材料对话框配套元件监听
     */
    public interface DoubleConfirmationMaterialAlertDialogKitListener {
        /**
         * 执行
         *
         * @param dialog DialogInterface
         */
        void execute(DialogInterface dialog);
    }

    private static final class InstanceHolder {
        static final DoubleConfirmationMaterialAlertDialogKit INSTANCE = new DoubleConfirmationMaterialAlertDialogKit();
    }
}