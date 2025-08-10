package widget.dialog.customdialog;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

/**
 * @decs: CustomDialog
 * @author: 郑少鹏
 * @date: 2018/4/4 13:43
 */
public class CustomDialog extends BaseDialog {
    private BaseViewConvertListener baseViewConvertListener;

    @NonNull
    @Contract(" -> new")
    public static CustomDialog init() {
        return new CustomDialog();
    }

    @Override
    public int intLayoutId() {
        return layoutId;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        if (null != baseViewConvertListener) {
            baseViewConvertListener.convertView(holder, dialog);
        }
    }

    public CustomDialog setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public CustomDialog setBaseViewConvertListener(BaseViewConvertListener baseViewConvertListener) {
        this.baseViewConvertListener = baseViewConvertListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            baseViewConvertListener = savedInstanceState.getParcelable("listener");
        }
    }

    /**
     * 保存接口
     *
     * @param outState outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("listener", baseViewConvertListener);
    }
}