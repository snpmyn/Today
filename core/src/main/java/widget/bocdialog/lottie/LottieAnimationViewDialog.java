package widget.bocdialog.lottie;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.core.R;

import lottie.LottieKit;
import util.data.StringUtils;
import util.density.DensityUtils;
import util.view.ViewUtils;
import widget.bocdialog.base.BaseInstanceDialog;
import widget.bocdialog.loading.listener.OnBackPressedListener;
import widget.bocdialog.lottie.bean.DialogLottieAnimationEnum;

/**
 * Created on 2022/4/6
 *
 * @author zsp
 * @desc LottieAnimationView 对话框
 */
public class LottieAnimationViewDialog extends BaseInstanceDialog {
    private LottieAnimationView lottieAnimationViewDialogLav;
    private TextView lottieAnimationViewDialogTv;
    private boolean loading;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private LottieAnimationViewDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_lottie_animation_view;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        lottieAnimationViewDialogLav = view.findViewById(R.id.lottieAnimationViewDialogLav);
        lottieAnimationViewDialogTv = view.findViewById(R.id.lottieAnimationViewDialogTv);
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
     * 设置动画
     *
     * @param dialogLottieAnimationEnum 对话框 Lottie 动画枚举
     */
    private void setAnimation(@NonNull DialogLottieAnimationEnum dialogLottieAnimationEnum) {
        // 资产名
        // 如 "camera.json"
        String assetName = dialogLottieAnimationEnum.getAssetName();
        if (TextUtils.isEmpty(assetName)) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = lottieAnimationViewDialogLav.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, dialogLottieAnimationEnum.getWidth());
        layoutParams.height = DensityUtils.dipToPxByFloat(context, dialogLottieAnimationEnum.getHeight());
        lottieAnimationViewDialogLav.setLayoutParams(layoutParams);
        LottieKit lottieKit = new LottieKit();
        lottieKit.useWithAsset(lottieAnimationViewDialogLav, assetName, ValueAnimator.INFINITE, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                if (loading) {
                    return;
                }
                LottieAnimationViewDialog.this.dismiss();
            }
        });
    }

    /**
     * 设置提示和宽高
     *
     * @param hint 提示
     */
    private void setHintAndWidthHeight(String hint) {
        int value;
        if (StringUtils.areEmpty(hint)) {
            value = 80;
            ViewUtils.hideView(lottieAnimationViewDialogTv, View.GONE);
        } else {
            value = 120;
            lottieAnimationViewDialogTv.setText(hint);
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, value);
        layoutParams.height = DensityUtils.dipToPxByFloat(context, value);
        view.setLayoutParams(layoutParams);
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
        private final LottieAnimationViewDialog lottieAnimationViewDialog;

        public Builder(Context context, int selfThemeResId) {
            this.lottieAnimationViewDialog = new LottieAnimationViewDialog(context, selfThemeResId);
        }

        public Builder setAnimation(DialogLottieAnimationEnum dialogLottieAnimationEnum) {
            lottieAnimationViewDialog.setAnimation(dialogLottieAnimationEnum);
            return this;
        }

        public Builder setHintAndWidthHeight(String hint) {
            lottieAnimationViewDialog.setHintAndWidthHeight(hint);
            return this;
        }

        public Builder setLoading(boolean loading) {
            lottieAnimationViewDialog.loading = loading;
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            lottieAnimationViewDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public LottieAnimationViewDialog build() {
            return lottieAnimationViewDialog;
        }
    }
}

