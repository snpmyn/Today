package widget.textwatcher;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created on 2020/12/25
 *
 * @author zsp
 * @desc 自定义文本观察者
 */
public class CustomTextWatcher implements TextWatcher {
    /**
     * TextInputLayout
     */
    private final TextInputLayout textInputLayout;
    /**
     * 文本变化回调
     */
    private OnTextChangedCallback onTextChangedCallback;

    /**
     * constructor
     *
     * @param textInputLayout TextInputLayout
     */
    public CustomTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    /**
     * constructor
     *
     * @param textInputLayout       TextInputLayout
     * @param onTextChangedCallback OnTextChangedCallback
     */
    public CustomTextWatcher(TextInputLayout textInputLayout, OnTextChangedCallback onTextChangedCallback) {
        this.textInputLayout = textInputLayout;
        this.onTextChangedCallback = onTextChangedCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // 预留：如有需要可以在此扩展 before 逻辑
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // 预留：如有需要可以在此扩展 on 逻辑
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // 1. 公共逻辑
        // 只要有输入且当前有错误提示，就自动清除错误。
        if (!TextUtils.isEmpty(editable) && (textInputLayout != null) && !TextUtils.isEmpty(textInputLayout.getError())) {
            textInputLayout.setError(null);
        }
        // 2. 额外逻辑
        // 传入回调则执行特定输入框扩展业务
        if (onTextChangedCallback != null) {
            onTextChangedCallback.afterTextChanged(editable);
        }
    }

    /**
     * 文本变化回调
     */
    public interface OnTextChangedCallback {
        /**
         * 文本变化
         *
         * @param editable Editable
         */
        void afterTextChanged(Editable editable);
    }
}