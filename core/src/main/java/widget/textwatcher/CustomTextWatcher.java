package widget.textwatcher;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created on 2020/12/25
 *
 * @author zsp
 * @desc 自定文本观察者
 */
public class CustomTextWatcher implements TextWatcher {
    private final TextInputLayout textInputLayout;
    private final EditText editText;

    /**
     * constructor
     *
     * @param textInputLayout TextInputLayout
     * @param editText        EditText
     */
    public CustomTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        this.textInputLayout = textInputLayout;
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editText.getText()) && !TextUtils.isEmpty(textInputLayout.getError())) {
            textInputLayout.setError(null);
        }
    }
}
