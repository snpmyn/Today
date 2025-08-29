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
import widget.dialog.bocdialog.message.listener.OnBocRoundCornerMessageDialogClickListener;

/**
 * Created on 2020-08-21
 *
 * @author zsp
 * @desc BOC 圆角消息对话框
 */
public class BocRoundCornerMessageDialog extends BaseBocInstanceDialog {
    private TextView bocRoundCornerMessageDialogTvTitle;
    private TextView bocRoundCornerMessageDialogTvContent;
    private Button bocRoundCornerMessageDialogBtnLeft;
    private Button bocRoundCornerMessageDialogBtnRight;
    private OnBocRoundCornerMessageDialogClickListener onBocRoundCornerMessageDialogLeftButtonClickListener, onBocRoundCornerMessageDialogRightButtonClickListener;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    private BocRoundCornerMessageDialog(Context context, int selfThemeResId) {
        super(context, selfThemeResId);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.dialog_boc_round_corner_message;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bocRoundCornerMessageDialogTvTitle = view.findViewById(R.id.bocRoundCornerMessageDialogTvTitle);
        bocRoundCornerMessageDialogTvContent = view.findViewById(R.id.bocRoundCornerMessageDialogTvContent);
        bocRoundCornerMessageDialogBtnLeft = view.findViewById(R.id.bocRoundCornerMessageDialogBtnLeft);
        bocRoundCornerMessageDialogBtnRight = view.findViewById(R.id.bocRoundCornerMessageDialogBtnRight);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        bocRoundCornerMessageDialogBtnLeft.setOnClickListener(view -> {
            if (null == onBocRoundCornerMessageDialogLeftButtonClickListener) {
                handle(BocRoundCornerMessageDialog.class);
            } else {
                onBocRoundCornerMessageDialogLeftButtonClickListener.buttonClick(view, BocRoundCornerMessageDialog.this);
            }
        });
        bocRoundCornerMessageDialogBtnRight.setOnClickListener(view -> {
            if (null == onBocRoundCornerMessageDialogRightButtonClickListener) {
                handle(BocRoundCornerMessageDialog.class);
            } else {
                onBocRoundCornerMessageDialogRightButtonClickListener.buttonClick(view, BocRoundCornerMessageDialog.this);
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
        bocRoundCornerMessageDialogTvTitle.setText(title);
        bocRoundCornerMessageDialogTvTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        bocRoundCornerMessageDialogTvContent.setText(content);
    }

    /**
     * 设置内容水平居中
     */
    private void setContentHorizontalCenter() {
        bocRoundCornerMessageDialogTvContent.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 设置标题颜色
     *
     * @param titleColor 标题颜色
     */
    private void setTitleColor(int titleColor) {
        bocRoundCornerMessageDialogTvTitle.setTextColor(titleColor);
    }

    /**
     * 设置内容颜色
     * <p>
     * 默认 <color name="textHint">#666666</color>
     *
     * @param contentColor 内容颜色
     */
    private void setContentColor(int contentColor) {
        bocRoundCornerMessageDialogTvContent.setTextColor(contentColor);
    }

    /**
     * 设置左按钮文本
     *
     * @param leftButtonText 左按钮文本
     */
    private void setLeftButtonText(String leftButtonText) {
        bocRoundCornerMessageDialogBtnLeft.setText(leftButtonText);
    }

    /**
     * 设置右按钮文本
     *
     * @param rightButtonText 右按钮文本
     */
    private void setRightButtonText(String rightButtonText) {
        bocRoundCornerMessageDialogBtnRight.setText(rightButtonText);
    }

    /**
     * 设置左按钮默认选中
     */
    private void setLeftButtonDefaultSelect() {
        bocRoundCornerMessageDialogBtnLeft.setBackground(ContextCompat.getDrawable(context, R.drawable.basic_white_solid_blr14_selector));
        setButtonDefaultSelect(bocRoundCornerMessageDialogBtnLeft);
    }

    /**
     * 设置右按钮默认选中
     */
    private void setRightButtonDefaultSelect() {
        bocRoundCornerMessageDialogBtnRight.setBackground(ContextCompat.getDrawable(context, R.drawable.basic_white_solid_brr14_selector));
        setButtonDefaultSelect(bocRoundCornerMessageDialogBtnRight);
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
     * 设置 BOC 圆角消息对话框左按钮点击监听
     *
     * @param onBocRoundCornerMessageDialogLeftButtonClickListener BOC 圆角消息对话框左按钮点击监听
     */
    private void setOnBocRoundCornerMessageDialogLeftButtonClickListener(OnBocRoundCornerMessageDialogClickListener onBocRoundCornerMessageDialogLeftButtonClickListener) {
        this.onBocRoundCornerMessageDialogLeftButtonClickListener = onBocRoundCornerMessageDialogLeftButtonClickListener;
    }

    /**
     * 设置 BOC 圆角消息对话框右按钮点击监听
     *
     * @param onBocRoundCornerMessageDialogRightButtonClickListener BOC 圆角消息对话框右按钮点击监听
     */
    private void setOnBocRoundCornerMessageDialogRightButtonClickListener(OnBocRoundCornerMessageDialogClickListener onBocRoundCornerMessageDialogRightButtonClickListener) {
        this.onBocRoundCornerMessageDialogRightButtonClickListener = onBocRoundCornerMessageDialogRightButtonClickListener;
    }

    public static class Builder {
        private final BocRoundCornerMessageDialog bocRoundCornerMessageDialog;

        public Builder(Context context, int selfThemeResId) {
            this.bocRoundCornerMessageDialog = new BocRoundCornerMessageDialog(context, selfThemeResId);
        }

        public Builder setTitle(String title) {
            bocRoundCornerMessageDialog.setTitle(title);
            return this;
        }

        public Builder setContent(String content) {
            bocRoundCornerMessageDialog.setContent(content);
            return this;
        }

        public Builder setContentHorizontalCenter() {
            bocRoundCornerMessageDialog.setContentHorizontalCenter();
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            bocRoundCornerMessageDialog.setTitleColor(titleColor);
            return this;
        }

        public Builder setContentColor(int contentColor) {
            bocRoundCornerMessageDialog.setContentColor(contentColor);
            return this;
        }

        public Builder setLeftButtonText(String leftButtonText) {
            bocRoundCornerMessageDialog.setLeftButtonText(leftButtonText);
            return this;
        }

        public Builder setRightButtonText(String rightButtonText) {
            bocRoundCornerMessageDialog.setRightButtonText(rightButtonText);
            return this;
        }

        public Builder setLeftButtonDefaultSelect() {
            bocRoundCornerMessageDialog.setLeftButtonDefaultSelect();
            return this;
        }

        public Builder setRightButtonDefaultSelect() {
            bocRoundCornerMessageDialog.setRightButtonDefaultSelect();
            return this;
        }

        public Builder setOnBocRoundCornerMessageDialogLeftButtonClickListener(OnBocRoundCornerMessageDialogClickListener onBocRoundCornerMessageDialogLeftButtonClickListener) {
            bocRoundCornerMessageDialog.setOnBocRoundCornerMessageDialogLeftButtonClickListener(onBocRoundCornerMessageDialogLeftButtonClickListener);
            return this;
        }

        public Builder setOnBocRoundCornerMessageDialogRightButtonClickListener(OnBocRoundCornerMessageDialogClickListener onBocRoundCornerMessageDialogRightButtonClickListener) {
            bocRoundCornerMessageDialog.setOnBocRoundCornerMessageDialogRightButtonClickListener(onBocRoundCornerMessageDialogRightButtonClickListener);
            return this;
        }

        public BocRoundCornerMessageDialog build() {
            return bocRoundCornerMessageDialog;
        }
    }
}