package widget.dialog.bottomsheetdialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * Created on 2025/8/13.
 *
 * @author 郑少鹏
 * @desc MyBottomSheetDialog
 */
public class MyBottomSheetDialog extends BottomSheetDialog {
    /**
     * constructor
     *
     * @param context     上下文
     * @param layoutResID 布局资源 ID
     */
    public MyBottomSheetDialog(@NonNull Context context, int layoutResID) {
        super(context);
        setContentView(layoutResID);
        setDismissWithAnimation(true);
    }
}