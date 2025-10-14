package widget.dialog.materialalertdialog;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.core.R;

import java.util.Objects;

import util.density.DensityUtils;
import util.view.ViewUtils;

/**
 * Created on 2025/10/11.
 *
 * @author 郑少鹏
 * @desc 输入材料对话框配套元件
 */
public class InputMaterialAlertDialogKit {
    private InputMaterialAlertDialogKitOnPositiveClickListener inputMaterialAlertDialogKitOnPositiveClickListener;
    private InputMaterialAlertDialogKitOnNegativeClickListener inputMaterialAlertDialogKitOnNegativeClickListener;
    private InputMaterialAlertDialogKitOnNeutralClickListener inputMaterialAlertDialogKitOnNeutralClickListener;

    public static InputMaterialAlertDialogKit getInstance() {
        return InputMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity 活动
     * @param title             标题
     * @param content           内容
     * @param positiveText      积极文本
     * @param negativeText      消极文本
     * @param neutralText       中性文本
     */
    public void show(AppCompatActivity appCompatActivity, String title, String content, String positiveText, String negativeText, String neutralText) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).create();
        // 视图
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_input_material, null);
        // 标题
        MaterialCardView inputMaterialAlertDialogMcvTitle = view.findViewById(R.id.inputMaterialAlertDialogMcvTitle);
        TextView inputMaterialAlertDialogTvTitle = view.findViewById(R.id.inputMaterialAlertDialogTvTitle);
        if (TextUtils.isEmpty(title)) {
            ViewUtils.hideView(inputMaterialAlertDialogMcvTitle, View.GONE);
        } else {
            inputMaterialAlertDialogTvTitle.setText(title);
            // 内容设置布局参数
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(DensityUtils.dipToPxByInt(12), DensityUtils.dipToPxByInt(12), DensityUtils.dipToPxByInt(12), 0);
            // TextInputLayout
            TextInputLayout inputMaterialAlertDialogTilContent = view.findViewById(R.id.inputMaterialAlertDialogTilContent);
            inputMaterialAlertDialogTilContent.setLayoutParams(layoutParams);
        }
        // TextInputEditText
        TextInputEditText inputMaterialAlertDialogTietContent = view.findViewById(R.id.inputMaterialAlertDialogTietContent);
        if (!TextUtils.isEmpty(content)) {
            inputMaterialAlertDialogTietContent.setText(content);
        }
        // 设置视图
        alertDialog.setView(view);
        // 积极按钮
        if (!TextUtils.isEmpty(positiveText)) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText, (dialog, which) -> {

            });
        }
        // 消极按钮
        if (!TextUtils.isEmpty(negativeText)) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, (dialog, which) -> {

            });
        }
        // 中性按钮
        if (!TextUtils.isEmpty(neutralText)) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, neutralText, (dialog, which) -> {

            });
        }
        // 可取消否
        alertDialog.setCancelable(false);
        // 显示
        alertDialog.show();
        // 积极按钮
        if (!TextUtils.isEmpty(positiveText)) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (null != inputMaterialAlertDialogKitOnPositiveClickListener) {
                    String content1 = Objects.requireNonNull(inputMaterialAlertDialogTietContent.getText()).toString();
                    // 内容
                    if (!TextUtils.isEmpty(content1)) {
                        inputMaterialAlertDialogKitOnPositiveClickListener.onPositiveClick(alertDialog, content1);
                    }
                }
            });
        }
        // 消极按钮
        if (!TextUtils.isEmpty(negativeText)) {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                if (null != inputMaterialAlertDialogKitOnNegativeClickListener) {
                    inputMaterialAlertDialogKitOnNegativeClickListener.onNegativeClick(alertDialog);
                }
            });
        }
        // 中性按钮
        if (!TextUtils.isEmpty(neutralText)) {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (null != inputMaterialAlertDialogKitOnNeutralClickListener) {
                    inputMaterialAlertDialogKitOnNeutralClickListener.onNeutralClick(alertDialog);
                }
            });
        }
    }

    public void setInputMaterialAlertDialogKitOnPositiveClickListener(InputMaterialAlertDialogKitOnPositiveClickListener inputMaterialAlertDialogKitOnPositiveClickListener) {
        this.inputMaterialAlertDialogKitOnPositiveClickListener = inputMaterialAlertDialogKitOnPositiveClickListener;
    }

    public void setInputMaterialAlertDialogKitOnNeutralClickListener(InputMaterialAlertDialogKitOnNeutralClickListener inputMaterialAlertDialogKitOnNeutralClickListener) {
        this.inputMaterialAlertDialogKitOnNeutralClickListener = inputMaterialAlertDialogKitOnNeutralClickListener;
    }

    public void setInputMaterialAlertDialogKitOnNegativeClickListener(InputMaterialAlertDialogKitOnNegativeClickListener inputMaterialAlertDialogKitOnNegativeClickListener) {
        this.inputMaterialAlertDialogKitOnNegativeClickListener = inputMaterialAlertDialogKitOnNegativeClickListener;
    }

    /**
     * 输入材料对话框配套元件积极点击监听
     */
    public interface InputMaterialAlertDialogKitOnPositiveClickListener {
        /**
         * 积极点击
         *
         * @param alertDialog AlertDialog
         * @param content     内容
         */
        void onPositiveClick(AlertDialog alertDialog, String content);
    }

    /**
     * 输入材料对话框配套元件消极点击监听
     */
    public interface InputMaterialAlertDialogKitOnNegativeClickListener {
        /**
         * 消极点击
         *
         * @param alertDialog AlertDialog
         */
        void onNegativeClick(AlertDialog alertDialog);
    }

    /**
     * 输入材料对话框配套元件中性点击监听
     */
    public interface InputMaterialAlertDialogKitOnNeutralClickListener {
        /**
         * 中性点击
         *
         * @param alertDialog AlertDialog
         */
        void onNeutralClick(AlertDialog alertDialog);
    }

    private static final class InstanceHolder {
        static final InputMaterialAlertDialogKit INSTANCE = new InputMaterialAlertDialogKit();
    }
}