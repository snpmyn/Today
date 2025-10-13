package widget.dialog.materialalertdialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.zsp.core.R;

import util.density.DensityUtils;
import util.view.ViewUtils;

/**
 * Created on 2025/10/11.
 *
 * @author 郑少鹏
 * @desc 按钮材料对话框配套元件
 */
public class ButtonMaterialAlertDialogKit {
    /**
     * 设置按钮材料对话框配套元件上点监听
     */
    private ButtonMaterialAlertDialogKitTopClickListener buttonMaterialAlertDialogKitTopClickListener;
    /**
     * 设置按钮材料对话框配套元件中点监听
     */
    private ButtonMaterialAlertDialogKitMiddleClickListener buttonMaterialAlertDialogKitMiddleClickListener;
    /**
     * 设置按钮材料对话框配套元件下点监听
     */
    private ButtonMaterialAlertDialogKitBottomClickListener buttonMaterialAlertDialogKitBottomClickListener;

    public static ButtonMaterialAlertDialogKit getInstance() {
        return ButtonMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity 活动
     * @param topText           上文本
     * @param middleText        中文本
     * @param bottomText        下文本
     */
    public void show(AppCompatActivity appCompatActivity, String topText, String middleText, String bottomText) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).create();
        // 视图
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_button_material, null);
        // 上
        TextView buttonMaterialAlertDialogTvTop = view.findViewById(R.id.buttonMaterialAlertDialogTvTop);
        buttonMaterialAlertDialogTvTop.setMinHeight(DensityUtils.dipToPxByInt(26));
        buttonMaterialAlertDialogTvTop.setText(topText);
        buttonMaterialAlertDialogTvTop.setOnClickListener(v -> {
            if (null != buttonMaterialAlertDialogKitTopClickListener) {
                buttonMaterialAlertDialogKitTopClickListener.onTopClick(alertDialog);
            }
        });
        // 中
        MaterialCardView buttonMaterialAlertDialogMcvMiddle = view.findViewById(R.id.buttonMaterialAlertDialogMcvMiddle);
        TextView buttonMaterialAlertDialogTvMiddle = view.findViewById(R.id.buttonMaterialAlertDialogTvMiddle);
        if (TextUtils.isEmpty(middleText)) {
            ViewUtils.hideView(buttonMaterialAlertDialogMcvMiddle, View.GONE);
        } else {
            buttonMaterialAlertDialogTvMiddle.setMinHeight(DensityUtils.dipToPxByInt(26));
            buttonMaterialAlertDialogTvMiddle.setText(middleText);
            buttonMaterialAlertDialogTvMiddle.setOnClickListener(v -> {
                if (null != buttonMaterialAlertDialogKitMiddleClickListener) {
                    buttonMaterialAlertDialogKitMiddleClickListener.onMiddleClick(alertDialog);
                }
            });
        }
        // 下
        MaterialCardView buttonMaterialAlertDialogMcvBottom = view.findViewById(R.id.buttonMaterialAlertDialogMcvBottom);
        TextView buttonMaterialAlertDialogTvBottom = view.findViewById(R.id.buttonMaterialAlertDialogTvBottom);
        if (TextUtils.isEmpty(bottomText)) {
            ViewUtils.hideView(buttonMaterialAlertDialogMcvBottom, View.GONE);
        } else {
            buttonMaterialAlertDialogTvBottom.setMinHeight(DensityUtils.dipToPxByInt(26));
            buttonMaterialAlertDialogTvBottom.setText(bottomText);
            buttonMaterialAlertDialogTvBottom.setOnClickListener(v -> {
                if (null != buttonMaterialAlertDialogKitBottomClickListener) {
                    buttonMaterialAlertDialogKitBottomClickListener.onBottomClick(alertDialog);
                }
            });
        }
        // 设置视图
        alertDialog.setView(view);
        // 显示
        alertDialog.show();
    }

    /**
     * 设置按钮材料对话框配套元件上点监听
     *
     * @param buttonMaterialAlertDialogKitTopClickListener 按钮材料对话框配套元件上点监听
     */
    public void setButtonMaterialAlertDialogKitTopClickListener(ButtonMaterialAlertDialogKitTopClickListener buttonMaterialAlertDialogKitTopClickListener) {
        this.buttonMaterialAlertDialogKitTopClickListener = buttonMaterialAlertDialogKitTopClickListener;
    }

    /**
     * 设置按钮材料对话框配套元件中点监听
     *
     * @param buttonMaterialAlertDialogKitMiddleClickListener 按钮材料对话框配套元件中点监听
     */
    public void setButtonMaterialAlertDialogKitMiddleClickListener(ButtonMaterialAlertDialogKitMiddleClickListener buttonMaterialAlertDialogKitMiddleClickListener) {
        this.buttonMaterialAlertDialogKitMiddleClickListener = buttonMaterialAlertDialogKitMiddleClickListener;
    }

    /**
     * 设置按钮材料对话框配套元件下点监听
     *
     * @param buttonMaterialAlertDialogKitBottomClickListener 按钮材料对话框配套元件下点监听
     */
    public void setButtonMaterialAlertDialogKitBottomClickListener(ButtonMaterialAlertDialogKitBottomClickListener buttonMaterialAlertDialogKitBottomClickListener) {
        this.buttonMaterialAlertDialogKitBottomClickListener = buttonMaterialAlertDialogKitBottomClickListener;
    }

    /**
     * 按钮材料对话框配套元件上点监听
     */
    public interface ButtonMaterialAlertDialogKitTopClickListener {
        /**
         * 上点
         *
         * @param alertDialog AlertDialog
         */
        void onTopClick(AlertDialog alertDialog);
    }

    /**
     * 按钮材料对话框配套元件中点监听
     */
    public interface ButtonMaterialAlertDialogKitMiddleClickListener {
        /**
         * 中点
         *
         * @param alertDialog AlertDialog
         */
        void onMiddleClick(AlertDialog alertDialog);
    }

    /**
     * 按钮材料对话框配套元件下点监听
     */
    public interface ButtonMaterialAlertDialogKitBottomClickListener {
        /**
         * 下点
         *
         * @param alertDialog AlertDialog
         */
        void onBottomClick(AlertDialog alertDialog);
    }

    private static final class InstanceHolder {
        static final ButtonMaterialAlertDialogKit INSTANCE = new ButtonMaterialAlertDialogKit();
    }
}