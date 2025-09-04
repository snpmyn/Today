package widget.dialog.bocdialog.kit;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.SoftReference;

import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.BocCanCancelLoadingDialog;
import widget.dialog.bocdialog.loading.BocCommonLoadingDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.loading.listener.OnClickToCloseListener;
import widget.dialog.bocdialog.loading.listener.OnDialogCloseListener;
import widget.dialog.bocdialog.lottie.BocLottieClickDialog;
import widget.dialog.bocdialog.lottie.BocLottieLoadingDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogAnimationEndListener;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogOnClickListener;

/**
 * Created on 2022/6/24
 *
 * @author zsp
 * @desc BOC 对话框配套元件
 */
public class BocDialogKit {
    private static SoftReference<AppCompatActivity> softReference;
    public BaseBocInstanceDialog baseBocInstanceDialog;

    public static BocDialogKit getInstance(AppCompatActivity appCompatActivity) {
        softReference = new SoftReference<>(appCompatActivity);
        return InstanceHolder.INSTANCE;
    }

    /**
     * BOC 普通加载
     *
     * @param hint                  提示
     * @param onBackPressedListener 回退按压监听
     */
    public void bocCommonLoading(String hint, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocCommonLoadingDialog.Builder(softReference.get(), 0).setHint(hint).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
    }

    /**
     * BOC 可取消加载
     *
     * @param hint                   提示
     * @param onClickToCloseListener 点击关闭监听
     * @param onDialogCloseListener  对话框关闭监听
     * @param onBackPressedListener  回退按压监听
     */
    public void bocCanCancelLoading(String hint, OnClickToCloseListener onClickToCloseListener, OnDialogCloseListener onDialogCloseListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocCanCancelLoadingDialog.Builder(softReference.get(), 0).setHint(hint).setOnClickToCloseListener(onClickToCloseListener).setOnDialogCloseListener(onDialogCloseListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
    }

    /**
     * BOC Lottie 加载对话框一
     *
     * @param bocLottieDialogEnum                 BOC Lottie 对话框枚举
     * @param hint                                提示
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框动画结束监听
     * @param onBackPressedListener               回退按压监听
     */
    public void bocLottieLoadingDialogOne(BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocLottieLoadingDialog.Builder(softReference.get(), 0).setHintAndWidthHeight(hint).setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogAnimationEndListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
    }

    /**
     * BOC Lottie 加载对话框二
     *
     * @param bocLottieDialogEnum                 BOC Lottie 对话框枚举
     * @param hint                                提示
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框动画结束监听
     * @param onBackPressedListener               回退按压监听
     * @return BOC Lottie 加载对话框
     */
    public BocLottieLoadingDialog bocLottieLoadingDialogTwo(BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocLottieLoadingDialog.Builder(softReference.get(), 0).setHintAndWidthHeight(hint).setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogAnimationEndListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
        return (BocLottieLoadingDialog) baseBocInstanceDialog;
    }

    /**
     * BOC Lottie 点击对话框
     *
     * @param bocLottieDialogEnum                 BOC Lottie 对话框枚举
     * @param contentHint                         内容提示
     * @param clickHint                           点击提示
     * @param repeatCount                         重复数量
     * @param bocLottieDialogAnimationEndListener BOC Lottie 对话框动画结束监听
     * @param bocLottieDialogOnClickListener      BOC Lottie 对话框点击监听
     * @param onBackPressedListener               回退按压监听
     * @return BOC Lottie 点击对话框
     */
    public BocLottieClickDialog bocLottieClickDialog(BocLottieDialogEnum bocLottieDialogEnum, String contentHint, String clickHint, int repeatCount, BocLottieDialogAnimationEndListener bocLottieDialogAnimationEndListener, BocLottieDialogOnClickListener bocLottieDialogOnClickListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocLottieClickDialog.Builder(softReference.get(), 0).setHint(contentHint, clickHint).setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogAnimationEndListener).setOnClickListener(bocLottieDialogOnClickListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
        return (BocLottieClickDialog) baseBocInstanceDialog;
    }

    /**
     * 结束
     */
    public void end() {
        if (null != softReference) {
            softReference.clear();
        }
        if (null != baseBocInstanceDialog) {
            if (baseBocInstanceDialog instanceof BocLottieLoadingDialog) {
                // BOC Lottie 加载对话框需要关动画
                ((BocLottieLoadingDialog) baseBocInstanceDialog).end();
            }
            if (baseBocInstanceDialog instanceof BocLottieClickDialog) {
                // BOC Lottie 点击对话框需要关动画
                ((BocLottieClickDialog) baseBocInstanceDialog).end();
            }
            if (baseBocInstanceDialog.isShowing()) {
                baseBocInstanceDialog.dismiss();
            }
            baseBocInstanceDialog = null;
        }
    }

    private static final class InstanceHolder {
        static final BocDialogKit INSTANCE = new BocDialogKit();
    }
}