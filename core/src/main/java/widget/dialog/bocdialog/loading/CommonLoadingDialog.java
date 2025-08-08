package widget.dialog.bocdialog.loading;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zsp.core.R;

import util.density.DensityUtils;
import widget.dialog.bocdialog.base.BaseInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;

/**
 * Created on 2020-08-28
 *
 * @author zsp
 * @desc 普通加载对话框
 */
public class CommonLoadingDialog extends BaseInstanceDialog {
    private TextView commonLoadingDialogTvHint;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private CommonLoadingDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_common_loading;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        commonLoadingDialogTvHint = view.findViewById(R.id.commonLoadingDialogTvHint);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 初始数据
     */
    @Override
    protected void initData() {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, 80);
        layoutParams.height = DensityUtils.dipToPxByFloat(context, 80);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != onBackPressedListener) {
            onBackPressedListener.backPressed();
            onBackPressedListener = null;
        }
    }

    /**
     * 设置提示
     * <p>
     * 默认隐藏，设置提示时显示。
     *
     * @param hint 提示
     */
    private void setHint(String hint) {
        if (TextUtils.isEmpty(hint)) {
            return;
        }
        commonLoadingDialogTvHint.setText(hint);
        commonLoadingDialogTvHint.setVisibility(View.VISIBLE);
    }

    /**
     * 设置回退按压监听
     *
     * @param onBackPressedListener 回退按压监听
     */
    private void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public static class Builder {
        private final CommonLoadingDialog commonLoadingDialog;

        public Builder(Context context, int selfThemeResId) {
            this.commonLoadingDialog = new CommonLoadingDialog(context, selfThemeResId);
        }

        public Builder setHint(String hint) {
            commonLoadingDialog.setHint(hint);
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            commonLoadingDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public CommonLoadingDialog build() {
            return commonLoadingDialog;
        }
    }
}
