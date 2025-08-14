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

import widget.dialog.bocdialog.base.BaseInstanceDialog;
import widget.dialog.bocdialog.message.listener.OnRoundCornerMessageDialogButtonClickListener;

/**
 * Created on 2020-08-21
 *
 * @author zsp
 * @desc 圆角消息对话框
 */
public class RoundCornerMessageDialog extends BaseInstanceDialog {
    private TextView roundCornerMessageDialogTvTitle;
    private TextView roundCornerMessageDialogTvContent;
    private Button roundCornerMessageDialogBtnLeft;
    private Button roundCornerMessageDialogBtnRight;
    private OnRoundCornerMessageDialogButtonClickListener onRoundCornerMessageDialogLeftButtonClickListener, onRoundCornerMessageDialogRightButtonClickListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private RoundCornerMessageDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_round_corner_message;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        roundCornerMessageDialogTvTitle = view.findViewById(R.id.roundCornerMessageDialogTvTitle);
        roundCornerMessageDialogTvContent = view.findViewById(R.id.roundCornerMessageDialogTvContent);
        roundCornerMessageDialogBtnLeft = view.findViewById(R.id.roundCornerMessageDialogBtnLeft);
        roundCornerMessageDialogBtnRight = view.findViewById(R.id.roundCornerMessageDialogBtnRight);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        roundCornerMessageDialogBtnLeft.setOnClickListener(view -> {
            if (null == onRoundCornerMessageDialogLeftButtonClickListener) {
                handle(RoundCornerMessageDialog.class);
            } else {
                onRoundCornerMessageDialogLeftButtonClickListener.buttonClick(view, RoundCornerMessageDialog.this);
            }
        });
        roundCornerMessageDialogBtnRight.setOnClickListener(view -> {
            if (null == onRoundCornerMessageDialogRightButtonClickListener) {
                handle(RoundCornerMessageDialog.class);
            } else {
                onRoundCornerMessageDialogRightButtonClickListener.buttonClick(view, RoundCornerMessageDialog.this);
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
        roundCornerMessageDialogTvTitle.setText(title);
        roundCornerMessageDialogTvTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        roundCornerMessageDialogTvContent.setText(content);
    }

    /**
     * 设置内容水平居中
     */
    private void setContentHorizontalCenter() {
        roundCornerMessageDialogTvContent.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 设置标题颜色
     * <p>
     * 默认 <color name="textInput">#333333</color>
     *
     * @param titleColor 标题颜色
     */
    private void setTitleColor(int titleColor) {
        roundCornerMessageDialogTvTitle.setTextColor(titleColor);
    }

    /**
     * 设置内容颜色
     * <p>
     * 默认 <color name="textHint">#666666</color>
     *
     * @param contentColor 内容颜色
     */
    private void setContentColor(int contentColor) {
        roundCornerMessageDialogTvContent.setTextColor(contentColor);
    }

    /**
     * 设置左按钮文本
     *
     * @param leftButtonText 左按钮文本
     */
    private void setLeftButtonText(String leftButtonText) {
        roundCornerMessageDialogBtnLeft.setText(leftButtonText);
    }

    /**
     * 设置右按钮文本
     *
     * @param rightButtonText 右按钮文本
     */
    private void setRightButtonText(String rightButtonText) {
        roundCornerMessageDialogBtnRight.setText(rightButtonText);
    }

    /**
     * 设置左按钮默认选中
     */
    private void setLeftButtonDefaultSelect() {
        roundCornerMessageDialogBtnLeft.setBackground(ContextCompat.getDrawable(context, R.drawable.basic_white_solid_blr14_selector));
        setButtonDefaultSelect(roundCornerMessageDialogBtnLeft);
    }

    /**
     * 设置右按钮默认选中
     */
    private void setRightButtonDefaultSelect() {
        roundCornerMessageDialogBtnRight.setBackground(ContextCompat.getDrawable(context, R.drawable.basic_white_solid_brr14_selector));
        setButtonDefaultSelect(roundCornerMessageDialogBtnRight);
    }

    /**
     * 设置按钮默认选中
     *
     * @param button 按钮
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtonDefaultSelect(final @NotNull Button button) {
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
     * 设置圆角消息对话框左按钮点击监听
     *
     * @param onRoundCornerMessageDialogLeftButtonClickListener 圆角消息对话框左按钮点击监听
     */
    private void setOnRoundCornerMessageDialogLeftButtonClickListener(OnRoundCornerMessageDialogButtonClickListener onRoundCornerMessageDialogLeftButtonClickListener) {
        this.onRoundCornerMessageDialogLeftButtonClickListener = onRoundCornerMessageDialogLeftButtonClickListener;
    }

    /**
     * 设置圆角消息对话框右按钮点击监听
     *
     * @param onRoundCornerMessageDialogRightButtonClickListener 圆角消息对话框右按钮点击监听
     */
    private void setOnRoundCornerMessageDialogRightButtonClickListener(OnRoundCornerMessageDialogButtonClickListener onRoundCornerMessageDialogRightButtonClickListener) {
        this.onRoundCornerMessageDialogRightButtonClickListener = onRoundCornerMessageDialogRightButtonClickListener;
    }

    public static class Builder {
        private final RoundCornerMessageDialog roundCornerMessageDialog;

        public Builder(Context context, int selfThemeResId) {
            this.roundCornerMessageDialog = new RoundCornerMessageDialog(context, selfThemeResId);
        }

        public Builder setTitle(String title) {
            roundCornerMessageDialog.setTitle(title);
            return this;
        }

        public Builder setContent(String content) {
            roundCornerMessageDialog.setContent(content);
            return this;
        }

        public Builder setContentHorizontalCenter() {
            roundCornerMessageDialog.setContentHorizontalCenter();
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            roundCornerMessageDialog.setTitleColor(titleColor);
            return this;
        }

        public Builder setContentColor(int contentColor) {
            roundCornerMessageDialog.setContentColor(contentColor);
            return this;
        }

        public Builder setLeftButtonText(String leftButtonText) {
            roundCornerMessageDialog.setLeftButtonText(leftButtonText);
            return this;
        }

        public Builder setRightButtonText(String rightButtonText) {
            roundCornerMessageDialog.setRightButtonText(rightButtonText);
            return this;
        }

        public Builder setLeftButtonDefaultSelect() {
            roundCornerMessageDialog.setLeftButtonDefaultSelect();
            return this;
        }

        public Builder setRightButtonDefaultSelect() {
            roundCornerMessageDialog.setRightButtonDefaultSelect();
            return this;
        }

        public Builder setOnRoundCornerMessageDialogLeftButtonClickListener(OnRoundCornerMessageDialogButtonClickListener onRoundCornerMessageDialogLeftButtonClickListener) {
            roundCornerMessageDialog.setOnRoundCornerMessageDialogLeftButtonClickListener(onRoundCornerMessageDialogLeftButtonClickListener);
            return this;
        }

        public Builder setOnRoundCornerMessageDialogRightButtonClickListener(OnRoundCornerMessageDialogButtonClickListener onRoundCornerMessageDialogRightButtonClickListener) {
            roundCornerMessageDialog.setOnRoundCornerMessageDialogRightButtonClickListener(onRoundCornerMessageDialogRightButtonClickListener);
            return this;
        }

        public RoundCornerMessageDialog build() {
            return roundCornerMessageDialog;
        }
    }
}