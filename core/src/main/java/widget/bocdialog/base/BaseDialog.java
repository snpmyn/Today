package widget.bocdialog.base;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.zsp.core.R;

/**
 * @desc: 基类
 * <p>
 * 继承 {@link BaseDialog} 实现对话框，关闭时调 {@link #dismiss()}。
 * 继承 {@link BaseInstanceDialog} 实现对话框，关闭时调 {@link BaseInstanceDialog#handle(Class)}。
 * @author: zsp
 * @date: 2020-08-19 16:17
 */
public abstract class BaseDialog extends Dialog {
    protected Context context;
    protected View view;

    /**
     * constructor
     *
     * @param context        上下文
     * @param selfThemeResId 自身主题资源 ID
     */
    BaseDialog(Context context, int selfThemeResId) {
        super(context, (selfThemeResId == 0) ? R.style.BocDialogStyle : selfThemeResId);
        this.context = context;
        setContentView(R.layout.dialog_base);
        // 基类调用
        stepView();
        // 子类实现
        stepUi();
        setListener();
        initData();
    }

    /**
     * 初始视图
     */
    private void stepView() {
        // 内容视图
        LinearLayout baseDialogLlContent = findViewById(R.id.baseDialogLlContent);
        view = LayoutInflater.from(context).inflate(layoutResId(), null);
        baseDialogLlContent.addView(view);
        // 根视图
        final View rootView = findViewById(R.id.baseDialogRl);
        rootView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                if (hasFocus && (rootView.getMeasuredWidth() == 0)) {
                    rootView.requestLayout();
                }
                if (hasFocus) {
                    rootView.getViewTreeObserver().removeOnWindowFocusChangeListener(this);
                }
            }
        });
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    protected abstract int layoutResId();

    /**
     * 初始控件
     */
    protected abstract void stepUi();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    /**
     * 初始数据
     */
    protected abstract void initData();
}