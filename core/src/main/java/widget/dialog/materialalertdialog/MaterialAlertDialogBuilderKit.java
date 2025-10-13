package widget.dialog.materialalertdialog;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created on 2021/1/8
 *
 * @author zsp
 * @desc 材料对话框构建器配套元件
 */
public class MaterialAlertDialogBuilderKit extends com.google.android.material.dialog.MaterialAlertDialogBuilder {
    public MaterialAlertDialogBuilderKit(@NonNull Context context) {
        super(context);
    }

    public MaterialAlertDialogBuilderKit(@NonNull Context context, int overrideThemeResId) {
        super(context, overrideThemeResId);
    }
}