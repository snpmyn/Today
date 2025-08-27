package widget.dialog.bocdialog.kit;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.SoftReference;

import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.loading.BocCanCancelLoadingDialog;
import widget.dialog.bocdialog.loading.BocCommonLoadingDialog;
import widget.dialog.bocdialog.loading.listener.OnBackPressedListener;
import widget.dialog.bocdialog.loading.listener.OnClickToCloseListener;
import widget.dialog.bocdialog.loading.listener.OnDialogCloseListener;
import widget.dialog.bocdialog.lottie.BocLottieDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogListener;

/**
 * Created on 2022/6/24
 *
 * @author zsp
 * @desc BOC 对话框配套元件
 */
public class BocDialogKit {
    private static SoftReference<AppCompatActivity> softReference;
    private BaseBocInstanceDialog baseBocInstanceDialog;

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
     * BOC Lottie 对话框一
     *
     * @param bocLottieDialogEnum     BOC Lottie 对话框枚举
     * @param hint                    提示
     * @param repeatCount             重复数量
     * @param bocLottieDialogListener BOC Lottie 对话框监听
     * @param onBackPressedListener   回退按压监听
     */
    public void bocLottieDialogOne(BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogListener bocLottieDialogListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocLottieDialog.Builder(softReference.get(), 0).setHintAndWidthHeight(hint).setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
    }

    /**
     * BOC Lottie 对话框二
     *
     * @param bocLottieDialogEnum     BOC Lottie 对话框枚举
     * @param hint                    提示
     * @param repeatCount             重复数量
     * @param bocLottieDialogListener BOC Lottie 对话框监听
     * @param onBackPressedListener   回退按压监听
     * @return BOC Lottie 对话框
     */
    public BocLottieDialog bocLottieDialogTwo(BocLottieDialogEnum bocLottieDialogEnum, String hint, int repeatCount, BocLottieDialogListener bocLottieDialogListener, OnBackPressedListener onBackPressedListener) {
        baseBocInstanceDialog = new BocLottieDialog.Builder(softReference.get(), 0).setHintAndWidthHeight(hint).setAnimation(bocLottieDialogEnum, repeatCount, bocLottieDialogListener).setOnBackPressedListener(onBackPressedListener).build();
        baseBocInstanceDialog.setCancelable(false);
        baseBocInstanceDialog.show();
        return (BocLottieDialog) baseBocInstanceDialog;
    }

    /**
     * 结束
     */
    public void end() {
        if (null != softReference) {
            softReference.clear();
        }
        if (null != baseBocInstanceDialog) {
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