package widget.dialog.customdialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.zsp.core.R;

import java.util.Objects;

import util.density.DensityUtils;
import util.screen.ScreenUtils;

/**
 * @decs: BaseDialog
 * @author: 郑少鹏
 * @date: 2018/4/4 13:40
 */
public abstract class BaseDialog extends DialogFragment {
    private static final String MARGIN = "margin";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DIM = "dim_amount";
    private static final String BOTTOM = "show_bottom";
    private static final String CANCEL = "out_cancel";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";
    private static final String CENTER = "show_center";
    /**
     * 布局 ID
     */
    int layoutId;
    /**
     * 左右边距
     */
    private int margin;
    /**
     * 宽
     */
    private int width;
    /**
     * 高
     */
    private int height;
    /**
     * 灰度
     */
    private float dimAmount = 0.5F;
    /**
     * 底部显
     */
    private boolean showBottom;
    /**
     * 外点取
     */
    private boolean outCancel = true;
    private int animStyle;
    /**
     * 中间显
     */
    private boolean showCenter;

    /**
     * 初始化布局 ID
     *
     * @return 布局 ID
     */
    public abstract int intLayoutId();

    /**
     * Convert View
     *
     * @param holder viewHolder
     * @param dialog baseDialog
     */
    public abstract void convertView(ViewHolder holder, BaseDialog dialog);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogStyle);
        layoutId = intLayoutId();
        // 恢复所保存数据
        if (null != savedInstanceState) {
            margin = savedInstanceState.getInt(MARGIN);
            width = savedInstanceState.getInt(WIDTH);
            height = savedInstanceState.getInt(HEIGHT);
            dimAmount = savedInstanceState.getFloat(DIM);
            showBottom = savedInstanceState.getBoolean(BOTTOM);
            outCancel = savedInstanceState.getBoolean(CANCEL);
            animStyle = savedInstanceState.getInt(ANIM);
            layoutId = savedInstanceState.getInt(LAYOUT);
            showCenter = savedInstanceState.getBoolean(CENTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        convertView(ViewHolder.create(view), this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    /**
     * 屏幕旋转等致 DialogFragment 销毁后重建时保存数据
     *
     * @param outState outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MARGIN, margin);
        outState.putInt(WIDTH, width);
        outState.putInt(HEIGHT, height);
        outState.putFloat(DIM, dimAmount);
        outState.putBoolean(BOTTOM, showBottom);
        outState.putBoolean(CANCEL, outCancel);
        outState.putInt(ANIM, animStyle);
        outState.putInt(LAYOUT, layoutId);
        outState.putBoolean(CENTER, showCenter);
    }

    private void initParams() {
        if (null != getDialog()) {
            Window window = getDialog().getWindow();
            if (null != window) {
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                // 调节灰色背景透明度 [0-1]（默 0.5F）
                layoutParams.dimAmount = dimAmount;
                // 底部显
                if (showBottom) {
                    layoutParams.gravity = Gravity.BOTTOM;
                    if (animStyle == 0) {
                        animStyle = R.style.DefaultAnimation;
                    }
                }
                // 中间显示
                if (showCenter) {
                    layoutParams.gravity = Gravity.CENTER;
                    if (animStyle == 0) {
                        animStyle = R.style.DefaultAnimation;
                    }
                }
                // Dialog 宽
                if ((width == 0) && (null != getContext())) {
                    layoutParams.width = ScreenUtils.screenWidth(getContext()) - 2 * DensityUtils.dipToPxByFloat(getContext(), margin);
                } else {
                    layoutParams.width = DensityUtils.dipToPxByFloat(Objects.requireNonNull(getContext(), "must not be null"), width);
                }
                // Dialog 高
                if (height == 0) {
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                } else {
                    layoutParams.height = DensityUtils.dipToPxByFloat(getContext(), height);
                }
                // Dialog 进 / 退动画
                window.setWindowAnimations(animStyle);
                window.setAttributes(layoutParams);
            }
        }
        setCancelable(outCancel);
    }

    public BaseDialog setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    public BaseDialog setWidth(int width) {
        this.width = width;
        return this;
    }

    public BaseDialog setHeight(int height) {
        this.height = height;
        return this;
    }

    public BaseDialog setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public BaseDialog setShowBottom(boolean showBottom) {
        this.showBottom = showBottom;
        return this;
    }

    public BaseDialog setShowCenter(boolean showCenter) {
        this.showCenter = showCenter;
        return this;
    }

    public BaseDialog setOutCancel(boolean outCancel) {
        this.outCancel = outCancel;
        return this;
    }

    public BaseDialog setAnimStyle(@StyleRes int animStyle) {
        this.animStyle = animStyle;
        return this;
    }

    public BaseDialog show(FragmentManager manager) {
        super.show(manager, String.valueOf(System.currentTimeMillis()));
        return this;
    }
}