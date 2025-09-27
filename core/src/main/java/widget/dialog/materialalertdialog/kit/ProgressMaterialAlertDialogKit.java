package widget.dialog.materialalertdialog.kit;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.zsp.core.R;

/**
 * Created on 2025/9/24.
 *
 * @author 郑少鹏
 * @desc 进度材料对话框配套元件
 */
public class ProgressMaterialAlertDialogKit {
    private AlertDialog alertDialog;
    private LinearProgressIndicator progressMaterialAlertDialogLpi;

    public static ProgressMaterialAlertDialogKit getInstance() {
        return ProgressMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity 活动 关闭
     */
    public void show(@NonNull AppCompatActivity appCompatActivity) {
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_progress_material, null);
        progressMaterialAlertDialogLpi = view.findViewById(R.id.progressMaterialAlertDialogLpi);
        alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity).setView(view).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    /**
     * 更新
     *
     * @param progress 进度
     */
    public void update(int progress) {
        progressMaterialAlertDialogLpi.setProgress(progress);
    }

    /**
     * 关闭
     */
    public void close() {
        if ((null != alertDialog) && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    private static final class InstanceHolder {
        static final ProgressMaterialAlertDialogKit INSTANCE = new ProgressMaterialAlertDialogKit();
    }
}