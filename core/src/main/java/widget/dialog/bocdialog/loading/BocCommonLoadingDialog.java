package widget.dialog.bocdialog.loading;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zsp.core.R;

import util.density.DensityUtils;
import util.layoutparams.LayoutParamsUtils;
import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;

/**
 * Created on 2020-08-28
 *
 * @author zsp
 * @desc BOC 普通加载对话框
 */
public class BocCommonLoadingDialog extends BaseBocInstanceDialog {
    private TextView bocCommonLoadingDialogTvHint;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocCommonLoadingDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_common_loading;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocCommonLoadingDialogTvHint = view.findViewById(R.id.bocCommonLoadingDialogTvHint);
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
        // 设置视图布局参数
        LayoutParamsUtils.setViewLayoutParams(view, false, 0, true, DensityUtils.dipToPxByFloat(context, 80));
        // 设置窗口管理器布局参数
        LayoutParamsUtils.setWindowManagerLayoutParams(getWindow(), true, DensityUtils.dipToPxByFloat(context, 80), false, 0);
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
        bocCommonLoadingDialogTvHint.setText(hint);
        bocCommonLoadingDialogTvHint.setVisibility(View.VISIBLE);
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
        private final BocCommonLoadingDialog bocCommonLoadingDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocCommonLoadingDialog = new BocCommonLoadingDialog(context, selfThemeResId);
        }

        public Builder setHint(String hint) {
            bocCommonLoadingDialog.setHint(hint);
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            bocCommonLoadingDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public BocCommonLoadingDialog build() {
            return bocCommonLoadingDialog;
        }
    }
}