package widget.dialog.materialalertdialog;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created on 2021/1/8
 *
 * @author zsp
 * @desc MyMaterialAlertDialogBuilder
 */
public class MyMaterialAlertDialogBuilder extends com.google.android.material.dialog.MaterialAlertDialogBuilder {
    public MyMaterialAlertDialogBuilder(@NonNull Context context) {
        super(context);
    }

    public MyMaterialAlertDialogBuilder(@NonNull Context context, int overrideThemeResId) {
        super(context, overrideThemeResId);
    }
}