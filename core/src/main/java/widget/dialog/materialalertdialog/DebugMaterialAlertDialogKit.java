package widget.dialog.materialalertdialog;

import android.content.Context;

import com.zsp.core.R;

import util.handler.HandlerKit;

/**
 * Created on 2025/9/3.
 *
 * @author 郑少鹏
 * @desc 调试材料对话框配套元件
 */
public class DebugMaterialAlertDialogKit {
    public static DebugMaterialAlertDialogKit getInstance() {
        return DebugMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param context 上下文
     * @param message 消息
     * @param debug   调试否
     */
    public void show(Context context, String message, boolean debug) {
        if (debug) {
            // 为避免调用方处于异步任务导致更新 UI 失败，故用 Handler 操作。
            new HandlerKit().post(() -> new MaterialAlertDialogBuilderKit(context, R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).setTitle(R.string.debug).setMessage(message).setPositiveButton(R.string.ensure, (dialog, which) -> dialog.dismiss()).setCancelable(false).show());
        }
    }

    private static final class InstanceHolder {
        static final DebugMaterialAlertDialogKit INSTANCE = new DebugMaterialAlertDialogKit();
    }
}