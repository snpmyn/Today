package widget.dialog.materialalertdialog;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.zsp.core.R;

import util.density.DensityUtils;
import util.view.ViewUtils;

/**
 * Created on 2025/10/6.
 *
 * @author 郑少鹏
 * @desc 提示材料对话框配套元件
 */
public class HintMaterialAlertDialogKit {
    private HintMaterialAlertDialogKitOnPositiveClickListener hintMaterialAlertDialogKitOnPositiveClickListener;
    private HintMaterialAlertDialogKitOnNegativeClickListener hintMaterialAlertDialogKitOnNegativeClickListener;
    private HintMaterialAlertDialogKitOnNeutralClickListener hintMaterialAlertDialogKitOnNeutralClickListener;

    public static HintMaterialAlertDialogKit getInstance() {
        return HintMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity 活动
     * @param title             标题
     * @param message           内容
     * @param positiveText      积极文本
     * @param negativeText      消极文本
     * @param neutralText       中性文本
     */
    public void show(AppCompatActivity appCompatActivity, String title, String message, String positiveText, String negativeText, String neutralText) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).create();
        // 视图
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_hint_material, null);
        // 标题
        MaterialCardView hintMaterialAlertDialogMcvTitle = view.findViewById(R.id.hintMaterialAlertDialogMcvTitle);
        TextView hintMaterialAlertDialogTvTitle = view.findViewById(R.id.hintMaterialAlertDialogTvTitle);
        if (TextUtils.isEmpty(title)) {
            ViewUtils.hideView(hintMaterialAlertDialogMcvTitle, View.GONE);
        } else {
            hintMaterialAlertDialogTvTitle.setText(title);
            // 内容设置布局参数
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(DensityUtils.dipToPxByInt(12), DensityUtils.dipToPxByInt(12), DensityUtils.dipToPxByInt(12), 0);
            MaterialCardView hintMaterialAlertDialogMcvMessage = view.findViewById(R.id.hintMaterialAlertDialogMcvMessage);
            hintMaterialAlertDialogMcvMessage.setLayoutParams(layoutParams);
        }
        // 内容
        TextView hintMaterialAlertDialogTvMessage = view.findViewById(R.id.hintMaterialAlertDialogTvMessage);
        hintMaterialAlertDialogTvMessage.setMinHeight(DensityUtils.dipToPxByInt(66));
        hintMaterialAlertDialogTvMessage.setText(message);
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
                if (null != hintMaterialAlertDialogKitOnPositiveClickListener) {
                    hintMaterialAlertDialogKitOnPositiveClickListener.onPositiveClick(alertDialog);
                }
            });
        }
        // 消极按钮
        if (!TextUtils.isEmpty(negativeText)) {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                if (null != hintMaterialAlertDialogKitOnNegativeClickListener) {
                    hintMaterialAlertDialogKitOnNegativeClickListener.onNegativeClick(alertDialog);
                }
            });
        }
        // 中性按钮
        if (!TextUtils.isEmpty(neutralText)) {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (null != hintMaterialAlertDialogKitOnNeutralClickListener) {
                    hintMaterialAlertDialogKitOnNeutralClickListener.onNeutralClick(alertDialog);
                }
            });
        }
    }

    public void setHintMaterialAlertDialogKitOnPositiveClickListener(HintMaterialAlertDialogKitOnPositiveClickListener hintMaterialAlertDialogKitOnPositiveClickListener) {
        this.hintMaterialAlertDialogKitOnPositiveClickListener = hintMaterialAlertDialogKitOnPositiveClickListener;
    }

    public void setHintMaterialAlertDialogKitOnNegativeClickListener(HintMaterialAlertDialogKitOnNegativeClickListener hintMaterialAlertDialogKitOnNegativeClickListener) {
        this.hintMaterialAlertDialogKitOnNegativeClickListener = hintMaterialAlertDialogKitOnNegativeClickListener;
    }

    public void setHintMaterialAlertDialogKitOnNeutralClickListener(HintMaterialAlertDialogKitOnNeutralClickListener hintMaterialAlertDialogKitOnNeutralClickListener) {
        this.hintMaterialAlertDialogKitOnNeutralClickListener = hintMaterialAlertDialogKitOnNeutralClickListener;
    }

    /**
     * 提示材料对话框配套元件积极点击监听
     */
    public interface HintMaterialAlertDialogKitOnPositiveClickListener {
        /**
         * 积极点击
         *
         * @param alertDialog AlertDialog
         */
        void onPositiveClick(AlertDialog alertDialog);
    }

    /**
     * 提示材料对话框配套元件消极点击监听
     */
    public interface HintMaterialAlertDialogKitOnNegativeClickListener {
        /**
         * 消极点击
         *
         * @param alertDialog AlertDialog
         */
        void onNegativeClick(AlertDialog alertDialog);
    }

    /**
     * 提示材料对话框配套元件中性点击监听
     */
    public interface HintMaterialAlertDialogKitOnNeutralClickListener {
        /**
         * 中性点击
         *
         * @param alertDialog AlertDialog
         */
        void onNeutralClick(AlertDialog alertDialog);
    }

    private static final class InstanceHolder {
        static final HintMaterialAlertDialogKit INSTANCE = new HintMaterialAlertDialogKit();
    }
}