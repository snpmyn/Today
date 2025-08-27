package widget.dialog.bocdialog.lottie;

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

import lottie.kit.LottieKit;
import util.data.StringUtils;
import util.density.DensityUtils;
import util.view.ViewUtils;
import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogListener;

/**
 * Created on 2022/4/6
 *
 * @author zsp
 * @desc BOC Lottie 对话框
 */
public class BocLottieDialog extends BaseBocInstanceDialog {
    private LottieAnimationView bocLottieDialogLav;
    private TextView bocLottieDialogTv;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocLottieDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_lottie;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocLottieDialogLav = view.findViewById(R.id.bocLottieDialogLav);
        bocLottieDialogTv = view.findViewById(R.id.bocLottieDialogTv);
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

    /**
     * 设置提示和宽高
     *
     * @param hint 提示
     */
    public void setHintAndWidthHeight(String hint) {
        int value;
        if (StringUtils.areEmpty(hint)) {
            value = 80;
            ViewUtils.hideView(bocLottieDialogTv, View.GONE);
        } else {
            value = 120;
            bocLottieDialogTv.setText(hint);
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, value);
        layoutParams.height = DensityUtils.dipToPxByFloat(context, value);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 设置动画
     * <p>
     * 无限次数重复时，不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}，故而 {@link BocLottieDialogListener} 可空。
     * 限定次数重复时，执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}，故而 {@link BocLottieDialogListener} 按需非空。
     *
     * @param bocLottieDialogEnum     BOC Lottie 对话框枚举
     * @param repeatCount             重复数量
     * @param bocLottieDialogListener BOC Lottie 对话框监听
     */
    private void setAnimation(@NonNull BocLottieDialogEnum bocLottieDialogEnum, int repeatCount, BocLottieDialogListener bocLottieDialogListener) {
        String assetName = bocLottieDialogEnum.getAssetName();
        if (TextUtils.isEmpty(assetName)) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = bocLottieDialogLav.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getWidth());
        layoutParams.height = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getHeight());
        bocLottieDialogLav.setLayoutParams(layoutParams);
        if (repeatCount == ValueAnimator.INFINITE) {
            // 无限次数重复
            LottieKit.getInstance().useWithAsset(bocLottieDialogLav, assetName, repeatCount, null);
        } else {
            // 限定次数重复
            LottieKit.getInstance().useWithAsset(bocLottieDialogLav, assetName, repeatCount, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (null != bocLottieDialogListener) {
                        bocLottieDialogListener.onAnimationEnd();
                    }
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
        }
    }

    /**
     * 设置回退按压监听
     *
     * @param onBackPressedListener 回退按压监听
     */
    private void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    /**
     * 更新
     *
     * @param bocLottieDialogEnum     BOC Lottie 对话框枚举
     * @param hint                    提示
     * @param repeatCount             重复数量
     * @param bocLottieDialogListener BOC Lottie 对话框枚举
     */
    public void update(@NonNull BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogListener bocLottieDialogListener) {
        bocLottieDialogTv.setText(hint);
        LottieKit.getInstance().endAnimation(bocLottieDialogLav);
        setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogListener);
    }

    public static class Builder {
        private final BocLottieDialog bocLottieDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocLottieDialog = new BocLottieDialog(context, selfThemeResId);
        }

        public Builder setHintAndWidthHeight(String hint) {
            bocLottieDialog.setHintAndWidthHeight(hint);
            return this;
        }

        public Builder setAnimation(BocLottieDialogEnum bocLottieDialogEnum, int count, BocLottieDialogListener bocLottieDialogListener) {
            bocLottieDialog.setAnimation(bocLottieDialogEnum, count, bocLottieDialogListener);
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            bocLottieDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public BocLottieDialog build() {
            return bocLottieDialog;
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
}