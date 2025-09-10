package widget.dialog.bocdialog.loading;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsp.core.R;

import util.density.DensityUtils;
import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.loading.listener.OnClickToCloseListener;
import widget.dialog.bocdialog.loading.listener.OnDialogCloseListener;

/**
 * Created on 2020-09-01
 *
 * @author zsp
 * @desc BOC 可取消加载对话框
 */
public class BocCanCancelLoadingDialog extends BaseBocInstanceDialog {
    private ImageView bocCanCancelLoadingDialogIvLoading;
    private TextView bocCanCancelLoadingDialogTvHint;
    private ImageView bocCanCancelLoadingDialogIvClose;
    private Animation animation;
    private OnClickToCloseListener onClickToCloseListener;
    private OnDialogCloseListener onDialogCloseListener;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocCanCancelLoadingDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_can_cancel_loading;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocCanCancelLoadingDialogIvLoading = view.findViewById(R.id.bocCanCancelLoadingDialogIvLoading);
        bocCanCancelLoadingDialogTvHint = view.findViewById(R.id.bocCanCancelLoadingDialogTvHint);
        bocCanCancelLoadingDialogIvClose = view.findViewById(R.id.bocCanCancelLoadingDialogIvClose);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        bocCanCancelLoadingDialogIvClose.setOnClickListener(view -> {
            if (null != onClickToCloseListener) {
                onClickToCloseListener.clickToClose();
                onClickToCloseListener = null;
            }
            cancel();
        });
    }

    /**
     * 初始数据
     */
    @Override
    protected void initData() {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, 146);
        layoutParams.height = DensityUtils.dipToPxByFloat(context, 66);
        view.setLayoutParams(layoutParams);
        // 动画
        animation = AnimationUtils.loadAnimation(context, R.anim.boc_dialog_can_cancel_loading);
    }

    @Override
    public void show() {
        super.show();
        bocCanCancelLoadingDialogIvLoading.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bocCanCancelLoadingDialogIvLoading.clearAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != onDialogCloseListener) {
            onDialogCloseListener.dialogClose();
            onDialogCloseListener = null;
        }
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
     *
     * @param hint 提示
     */
    private void setHint(String hint) {
        if (TextUtils.isEmpty(hint)) {
            return;
        }
        bocCanCancelLoadingDialogTvHint.setText(hint);
    }

    /**
     * 设置点击关闭监听
     *
     * @param onClickToCloseListener 点击关闭监听
     */
    private void setOnClickToCloseListener(OnClickToCloseListener onClickToCloseListener) {
        this.onClickToCloseListener = onClickToCloseListener;
    }

    /**
     * 设置对话框关闭监听
     *
     * @param onDialogCloseListener 对话框关闭监听
     */
    private void setOnDialogCloseListener(OnDialogCloseListener onDialogCloseListener) {
        this.onDialogCloseListener = onDialogCloseListener;
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
        private final BocCanCancelLoadingDialog bocCanCancelLoadingDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocCanCancelLoadingDialog = new BocCanCancelLoadingDialog(context, selfThemeResId);
        }

        public Builder setHint(String hint) {
            bocCanCancelLoadingDialog.setHint(hint);
            return this;
        }

        public Builder setOnClickToCloseListener(OnClickToCloseListener onClickToCloseListener) {
            bocCanCancelLoadingDialog.setOnClickToCloseListener(onClickToCloseListener);
            return this;
        }

        public Builder setOnDialogCloseListener(OnDialogCloseListener onDialogCloseListener) {
            bocCanCancelLoadingDialog.setOnDialogCloseListener(onDialogCloseListener);
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            bocCanCancelLoadingDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public BocCanCancelLoadingDialog build() {
            return bocCanCancelLoadingDialog;
        }
    }
}