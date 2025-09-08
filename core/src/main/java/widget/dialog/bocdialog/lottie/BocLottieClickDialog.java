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
import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import lottie.kit.LottieKit;
import util.data.StringUtils;
import util.density.DensityUtils;
import util.screen.ScreenUtils;
import util.view.ViewUtils;
import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogAnimationEndListener;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogOnClickListener;

/**
 * Created on 2025/9/2.
 *
 * @author 郑少鹏
 * @desc BOC Lottie 点击对话框
 */
public class BocLottieClickDialog extends BaseBocInstanceDialog {
    private LottieAnimationView bocLottieClickDialogLav;
    private TextView bocLottieClickDialogTv;
    private MaterialButton bocLottieClickDialogMb;
    private OnBackPressedListener onBackPressedListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocLottieClickDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_lottie_click;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // 视图
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (ScreenUtils.screenWidth(getContext()) * 0.7);
        view.setLayoutParams(layoutParams);
        // 控件
        bocLottieClickDialogLav = view.findViewById(R.id.bocLottieClickDialogLav);
        bocLottieClickDialogTv = view.findViewById(R.id.bocLottieClickDialogTv);
        bocLottieClickDialogMb = view.findViewById(R.id.bocLottieClickDialogMb);
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
     * 设置提示
     *
     * @param contentHint 内容提示
     * @param clickHint   点击提示
     */
    public void setHint(String contentHint, String clickHint) {
        // 内容提示
        if (StringUtils.areEmpty(contentHint)) {
            ViewUtils.hideView(bocLottieClickDialogTv, View.GONE);
        } else {
            bocLottieClickDialogTv.setText(contentHint);
        }
        // 点击提示
        bocLottieClickDialogMb.setText(clickHint);
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
        ViewGroup.LayoutParams layoutParams = bocLottieClickDialogLav.getLayoutParams();
        layoutParams.width = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getWidth());
        layoutParams.height = DensityUtils.dipToPxByFloat(context, bocLottieDialogEnum.getHeight());
        bocLottieClickDialogLav.setLayoutParams(layoutParams);
        if (repeatCount == ValueAnimator.INFINITE) {
            // 无限次数重复
            LottieKit.getInstance().useWithAsset(bocLottieClickDialogLav, assetName, repeatCount, null);
        } else {
            // 限定次数重复
            LottieKit.getInstance().useWithAsset(bocLottieClickDialogLav, assetName, repeatCount, new Animator.AnimatorListener() {
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
     * @param contentHint                         内容提示
     * @param clickHint                           点击提示
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框枚举
     */
    public void update(@NonNull BocLottieDialogEnum bocLottieDialogEnum, String contentHint, String clickHint, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener) {
        // 内容提示
        bocLottieClickDialogTv.setText(contentHint);
        // 点击提示
        bocLottieClickDialogMb.setText(clickHint);
        // 移除原有动画监听
        // 结束动画前设
        bocLottieClickDialogLav.removeAllAnimatorListeners();
        // 结束动画
        // 移除原有动画监听后设
        LottieKit.getInstance().endAnimation(bocLottieClickDialogLav);
        // 设置动画
        setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogAnimationEndListener);
    }

    public static class Builder {
        private final BocLottieClickDialog bocLottieClickDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocLottieClickDialog = new BocLottieClickDialog(context, selfThemeResId);
        }

        public BocLottieClickDialog.Builder setHint(String contentHint, String clickHint) {
            bocLottieClickDialog.setHint(contentHint, clickHint);
            return this;
        }

        public BocLottieClickDialog.Builder setAnimation(BocLottieDialogEnum bocLottieDialogEnum, int count, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener) {
            bocLottieClickDialog.setAnimation(bocLottieDialogEnum, count, bocLottieDialogAnimationEndListener);
            return this;
        }

        public BocLottieClickDialog.Builder setOnClickListener(BocLottieDialogOnClickListener bocLottieDialogOnClickListener) {
            bocLottieClickDialog.bocLottieClickDialogMb.setOnClickListener(v -> bocLottieDialogOnClickListener.onClick());
            return this;
        }

        public BocLottieClickDialog.Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            bocLottieClickDialog.setOnBackPressedListener(onBackPressedListener);
            return this;
        }

        public BocLottieClickDialog build() {
            return bocLottieClickDialog;
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