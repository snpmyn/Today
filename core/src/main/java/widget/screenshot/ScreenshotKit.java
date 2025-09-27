package widget.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import util.layoutparams.LayoutParamsUtils;
import util.screen.ScreenUtils;
import widget.dialog.materialalertdialog.kit.MaterialAlertDialogBuilderKit;

/**
 * @decs: 截屏配套原件
 * @author: 郑少鹏
 * @date: 2025/9/20 19:29
 * @version: v 1.0
 */
public class ScreenshotKit {
    /**
     * AlertDialog
     */
    private AlertDialog alertDialog;

    /**
     * constructor
     */
    private ScreenshotKit() {

    }

    public static class Builder {
        private final Context context;
        private View contentView;
        /**
         * 宽比率
         */
        private float widthRatio = 0.0F;
        /**
         * 高比率
         */
        private float heightRatio = 0.0F;
        /**
         * 是否使用自适应大小
         */
        private boolean useAutoSize = false;
        private int animationDuration = 200;
        private String fileNamePrefix = "截屏";
        private @LayoutRes int layoutResId = 0;
        private boolean cancelOnTouchOutside = true;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder setContentView(@NonNull View view) {
            this.contentView = view;
            this.layoutResId = 0;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutResId) {
            this.layoutResId = layoutResId;
            this.contentView = null;
            return this;
        }

        /**
         * 通过比率设置大小
         *
         * @param widthRatio  宽比率
         * @param heightRatio 高比率
         * @return 构建器
         */
        public Builder setSizeByRatio(float widthRatio, float heightRatio) {
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
            this.useAutoSize = false;
            return this;
        }

        /**
         * 设置自适应大小
         *
         * @return 构建器
         */
        public Builder setAutoSize() {
            this.useAutoSize = true;
            return this;
        }

        public Builder setAnimationDuration(int duration) {
            this.animationDuration = duration;
            return this;
        }

        public Builder setFileNamePrefix(String fileNamePrefix) {
            this.fileNamePrefix = fileNamePrefix;
            return this;
        }

        public Builder setCancelOnTouchOutside(boolean cancel) {
            this.cancelOnTouchOutside = cancel;
            return this;
        }

        public ScreenshotKit show() {
            if ((null == contentView) && (layoutResId != 0)) {
                contentView = LayoutInflater.from(context).inflate(layoutResId, null);
            }
            if (null == contentView) {
                throw new IllegalArgumentException("contentView must not be null");
            }
            ScreenshotKit screenshotKit = new ScreenshotKit();
            // wrapper 包裹 contentView
            FrameLayout wrapper = new FrameLayout(context);
            wrapper.addView(contentView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            // 创建 AlertDialog
            screenshotKit.alertDialog = new MaterialAlertDialogBuilderKit(context).setView(wrapper).create();
            screenshotKit.alertDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
            screenshotKit.alertDialog.show();
            // 设置视图布局参数
            LayoutParamsUtils.setViewLayoutParams((View) contentView.getParent(), false, getDialogWidth(), true, getDialogHeight());
            // 淡入动画
            Objects.requireNonNull(screenshotKit.alertDialog.getWindow()).getDecorView().post(() -> {
                View decor = screenshotKit.alertDialog.getWindow().getDecorView();
                decor.setAlpha(0.0F);
                decor.animate().alpha(1.0F).setDuration(animationDuration).start();
            });
            // 长按截屏
            wrapper.setOnLongClickListener(v -> {
                Bitmap bitmap = ScreenshotHelper.getBitmap(contentView);
                if (null != bitmap) {
                    ScreenshotHelper.saveBitmap(context, bitmap, fileNamePrefix);
                }
                return true;
            });
            return screenshotKit;
        }

        /**
         * 获取对话框宽
         *
         * @return 对话框宽
         */
        private int getDialogWidth() {
            if (useAutoSize) {
                return FrameLayout.LayoutParams.WRAP_CONTENT;
            }
            if ((widthRatio > 0.0F) && (widthRatio <= 1.0F)) {
                return (int) (ScreenUtils.screenWidth(context) * widthRatio);
            }
            return FrameLayout.LayoutParams.MATCH_PARENT;
        }

        /**
         * 获取对话框高
         *
         * @return 对话框高
         */
        private int getDialogHeight() {
            if (useAutoSize) {
                return FrameLayout.LayoutParams.WRAP_CONTENT;
            }
            if ((heightRatio > 0.0F) && (heightRatio <= 1.0F)) {
                return (int) (ScreenUtils.screenHeight(context) * heightRatio);
            }
            return FrameLayout.LayoutParams.MATCH_PARENT;
        }
    }

    /**
     * 消失
     */
    public void dismiss() {
        if ((null != alertDialog) && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}