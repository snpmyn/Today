package widget.dialog.bocdialog.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import widget.dialog.bocdialog.base.BaseBocInstanceDialog;
import widget.dialog.bocdialog.message.listener.OnBocRightAngleMessageDialogClickListener;

/**
 * Created on 2020-08-19
 *
 * @author zsp
 * @desc BOC 直角消息对话框
 */
public class BocRightAngleMessageDialog extends BaseBocInstanceDialog {
    private TextView bocRightAngleMessageDialogTvTitle;
    private TextView bocRightAngleMessageDialogTvContent;
    private Button bocRightAngleMessageDialogBtnLeft;
    private Button bocRightAngleMessageDialogBtnRight;
    private OnBocRightAngleMessageDialogClickListener onBocRightAngleMessageDialogLeftButtonClickListener, onBocRightAngleMessageDialogRightButtonClickListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocRightAngleMessageDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_right_angle_message;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocRightAngleMessageDialogTvTitle = view.findViewById(R.id.bocRightAngleMessageDialogTvTitle);
        bocRightAngleMessageDialogTvContent = view.findViewById(R.id.bocRightAngleMessageDialogTvContent);
        bocRightAngleMessageDialogBtnLeft = view.findViewById(R.id.bocRightAngleMessageDialogBtnLeft);
        bocRightAngleMessageDialogBtnRight = view.findViewById(R.id.bocRightAngleMessageDialogBtnRight);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        bocRightAngleMessageDialogBtnLeft.setOnClickListener(view -> {
            if (null == onBocRightAngleMessageDialogLeftButtonClickListener) {
                handle(BocRightAngleMessageDialog.class);
            } else {
                onBocRightAngleMessageDialogLeftButtonClickListener.buttonClick(view, BocRightAngleMessageDialog.this);
            }
        });
        bocRightAngleMessageDialogBtnRight.setOnClickListener(view -> {
            if (null == onBocRightAngleMessageDialogRightButtonClickListener) {
                handle(BocRightAngleMessageDialog.class);
            } else {
                onBocRightAngleMessageDialogRightButtonClickListener.buttonClick(view, BocRightAngleMessageDialog.this);
            }
        });
    }

    /**
     * 初始数据
     */
    @Override
    protected void initData() {

    }

    /**
     * 设置标题
     * <p>
     * 默认隐藏，设置标题时显示。
     *
     * @param title 标题
     */
    private void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        bocRightAngleMessageDialogTvTitle.setText(title);
        bocRightAngleMessageDialogTvTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        bocRightAngleMessageDialogTvContent.setText(content);
    }

    /**
     * 设置内容水平居中
     */
    private void setContentHorizontalCenter() {
        bocRightAngleMessageDialogTvContent.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 设置标题颜色
     *
     * @param titleColor 标题颜色
     */
    private void setTitleColor(int titleColor) {
        bocRightAngleMessageDialogTvTitle.setTextColor(titleColor);
    }

    /**
     * 设置内容颜色
     * <p>
     * 默认 <color name="textHint">#666666</color>
     *
     * @param contentColor 内容颜色
     */
    private void setContentColor(int contentColor) {
        bocRightAngleMessageDialogTvContent.setTextColor(contentColor);
    }

    /**
     * 设置左按钮文本
     *
     * @param leftButtonText 左按钮文本
     */
    private void setLeftButtonText(String leftButtonText) {
        bocRightAngleMessageDialogBtnLeft.setText(leftButtonText);
    }

    /**
     * 设置右按钮文本
     *
     * @param rightButtonText 右按钮文本
     */
    private void setRightButtonText(String rightButtonText) {
        bocRightAngleMessageDialogBtnRight.setText(rightButtonText);
    }

    /**
     * 设置左按钮默认选中
     */
    private void setLeftButtonDefaultSelect() {
        setButtonDefaultSelect(bocRightAngleMessageDialogBtnLeft);
    }

    /**
     * 设置右按钮默认选中
     */
    private void setRightButtonDefaultSelect() {
        setButtonDefaultSelect(bocRightAngleMessageDialogBtnRight);
    }

    /**
     * 设置按钮默认选中
     *
     * @param button 按钮
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtonDefaultSelect(final @NotNull Button button) {
        button.setBackground(ContextCompat.getDrawable(context, R.drawable.basic_white_drawable_selector));
        button.setTextColor(ContextCompat.getColor(context, R.color.white));
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    button.setTextColor(ContextCompat.getColor(context, R.color.basic));
                    break;
                case MotionEvent.ACTION_MOVE:
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    float rawX = motionEvent.getRawX();
                    // TODO: 2020-09-25 motionEvent.getRawY() - 20 滑至按钮边缘优化
                    float rawY = motionEvent.getRawY() - 20;
                    if (rawX < x || rawX > (x + view.getWidth()) || rawY < y || rawY > (y + view.getHeight())) {
                        button.setTextColor(ContextCompat.getColor(context, R.color.white));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    button.setTextColor(ContextCompat.getColor(context, R.color.white));
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    /**
     * 设置 BOC 直角消息对话框左按钮点击监听
     *
     * @param onBocRightAngleMessageDialogLeftButtonClickListener BOC 直角消息对话框左按钮点击监听
     */
    private void setOnBocRightAngleMessageDialogLeftButtonClickListener(OnBocRightAngleMessageDialogClickListener onBocRightAngleMessageDialogLeftButtonClickListener) {
        this.onBocRightAngleMessageDialogLeftButtonClickListener = onBocRightAngleMessageDialogLeftButtonClickListener;
    }

    /**
     * 设置 BOC 直角消息对话框右按钮点击监听
     *
     * @param onBocRightAngleMessageDialogRightButtonClickListener BOC 直角消息对话框右按钮点击监听
     */
    private void setOnBocRightAngleMessageDialogRightButtonClickListener(OnBocRightAngleMessageDialogClickListener onBocRightAngleMessageDialogRightButtonClickListener) {
        this.onBocRightAngleMessageDialogRightButtonClickListener = onBocRightAngleMessageDialogRightButtonClickListener;
    }

    public static class Builder {
        private final BocRightAngleMessageDialog bocRightAngleMessageDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocRightAngleMessageDialog = new BocRightAngleMessageDialog(context, selfThemeResId);
        }

        public Builder setTitle(String title) {
            bocRightAngleMessageDialog.setTitle(title);
            return this;
        }

        public Builder setContent(String content) {
            bocRightAngleMessageDialog.setContent(content);
            return this;
        }

        public Builder setContentHorizontalCenter() {
            bocRightAngleMessageDialog.setContentHorizontalCenter();
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            bocRightAngleMessageDialog.setTitleColor(titleColor);
            return this;
        }

        public Builder setContentColor(int contentColor) {
            bocRightAngleMessageDialog.setContentColor(contentColor);
            return this;
        }

        public Builder setLeftButtonText(String leftButtonText) {
            bocRightAngleMessageDialog.setLeftButtonText(leftButtonText);
            return this;
        }

        public Builder setRightButtonText(String rightButtonText) {
            bocRightAngleMessageDialog.setRightButtonText(rightButtonText);
            return this;
        }

        public Builder setLeftButtonDefaultSelect() {
            bocRightAngleMessageDialog.setLeftButtonDefaultSelect();
            return this;
        }

        public Builder setRightButtonDefaultSelect() {
            bocRightAngleMessageDialog.setRightButtonDefaultSelect();
            return this;
        }

        public Builder setOnBocRightAngleMessageDialogLeftButtonClickListener(OnBocRightAngleMessageDialogClickListener onBocRightAngleMessageDialogLeftButtonClickListener) {
            bocRightAngleMessageDialog.setOnBocRightAngleMessageDialogLeftButtonClickListener(onBocRightAngleMessageDialogLeftButtonClickListener);
            return this;
        }

        public Builder setOnBocRightAngleMessageDialogRightButtonClickListener(OnBocRightAngleMessageDialogClickListener onBocRightAngleMessageDialogRightButtonClickListener) {
            bocRightAngleMessageDialog.setOnBocRightAngleMessageDialogRightButtonClickListener(onBocRightAngleMessageDialogRightButtonClickListener);
            return this;
        }

        public BocRightAngleMessageDialog build() {
            return bocRightAngleMessageDialog;
        }
    }
}