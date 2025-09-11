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
import util.layoutparams.LayoutParamsUtils;
import util.view.ViewUtils;
import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogAnimationEndListener;

/**
 * Created on 2022/4/6
 *
 * @author zsp
 * @desc BOC Lottie 普通对话框
 */
public class BocLottieCommonDialog extends BaseBocInstanceDialog {
    private LottieAnimationView bocLottieCommonDialogLav;
    private TextView bocLottieCommonDialogTv;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocLottieCommonDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_lottie_common;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocLottieCommonDialogLav = view.findViewById(R.id.bocLottieCommonDialogLav);
        bocLottieCommonDialogTv = view.findViewById(R.id.bocLottieCommonDialogTv);
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
            ViewUtils.hideView(bocLottieCommonDialogTv, View.GONE);
        } else {
            value = 120;
            bocLottieCommonDialogTv.setText(hint);
        }
        // 设置窗口管理器布局参数
        LayoutParamsUtils.setWindowManagerLayoutParams(getWindow(), true, DensityUtils.dipToPxByFloat(context, value), true, DensityUtils.dipToPxByFloat(context, value));
    }

    /**
     * 设置动画
     * <p>
     * 无限次数重复时，不执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}，故而 {@link BocLottieDialogAnimationEndListener} 可空。
     * 限定次数重复时，执行 {@link android.animation.Animator.AnimatorListener#onAnimationEnd(Animator)}，故而 {@link BocLottieDialogAnimationEndListener} 按需非空。
     *
     * @param bocLottieDialogEnum                 BOC Lottie 对话框枚举
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框动画结束监听
     */
    private void setAnimation(@NonNull BocLottieDialogEnum bocLottieDialogEnum, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener) {
        String assetName = bocLottieDialogEnum.getAssetName();
        if (TextUtils.isEmpty(assetName)) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = bocLottieCommonDialogLav.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getWidth());
        layoutParams.height = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getHeight());
        bocLottieCommonDialogLav.setLayoutParams(layoutParams);
        if (repeatCount == ValueAnimator.INFINITE) {
            // 无限次数重复
            LottieKit.getInstance().useWithAsset(bocLottieCommonDialogLav, assetName, repeatCount, null);
        } else {
            // 限定次数重复
            LottieKit.getInstance().useWithAsset(bocLottieCommonDialogLav, assetName, repeatCount, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (null != bocLottieDialogAnimationEndListener) {
                        bocLottieDialogAnimationEndListener.onAnimationEnd();
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
     * @param bocLottieDialogEnum                 BOC Lottie 对话框枚举
     * @param hint                                提示
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框枚举
     */
    public void update(@NonNull BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener) {
        // 设置提示
        bocLottieCommonDialogTv.setText(hint);
        // 移除原有动画监听
        // 结束动画前设
        bocLottieCommonDialogLav.removeAllAnimatorListeners();
        // 结束动画
        // 移除原有动画监听后设
        LottieKit.getInstance().endAnimation(bocLottieCommonDialogLav);
        // 设置动画
        setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogAnimationEndListener);
    }

    public static class Builder {
        private final BocLottieCommonDialog bocLottieCommonDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocLottieCommonDialog = new BocLottieCommonDialog(context, selfThemeResId);
        }

        public Builder setHintAndWidthHeight(String hint) {
            bocLottieCommonDialog.setHintAndWidthHeight(hint);
            return this;
        }

        public Builder setAnimation(BocLottieDialogEnum bocLottieDialogEnum, int count, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener) {
            bocLottieCommonDialog.setAnimation(bocLottieDialogEnum, count, bocLottieDialogAnimationEndListener);
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            bocLottieCommonDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public BocLottieCommonDialog build() {
            return bocLottieCommonDialog;
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