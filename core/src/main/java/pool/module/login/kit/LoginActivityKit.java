package pool.module.login.kit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.core.R;

import java.util.Objects;

import pool.module.login.listener.LoginActivityListener;
import pool.value.PoolConstant;
import util.mmkv.MmkvKit;
import util.validate.RegularUtils;

/**
 * Created on 2021/9/18
 *
 * @author zsp
 * @desc 登录页配套元件
 */
public class LoginActivityKit {
    /**
     * 手机号预显示
     *
     * @param textInputEditTextPhoneNumber 手机号框
     */
    public void phoneNumberPreShow(@NonNull TextInputEditText textInputEditTextPhoneNumber) {
        textInputEditTextPhoneNumber.setText(MmkvKit.defaultMmkv().decodeString(PoolConstant.LOGIN_$_PHONE_NUMBER));
        textInputEditTextPhoneNumber.setSelection(Objects.requireNonNull(textInputEditTextPhoneNumber.getText()).length());
    }

    /**
     * 登录
     *
     * @param appCompatActivity               活动
     * @param textInputLayoutInputPhoneNumber 手机号输入框
     * @param textInputEditTextPhoneNumber    手机号框
     * @param loginActivityListener           登录页监听
     */
    public void login(AppCompatActivity appCompatActivity, TextInputLayout textInputLayoutInputPhoneNumber, @NonNull TextInputEditText textInputEditTextPhoneNumber, LoginActivityListener loginActivityListener) {
        String phoneNumber = Objects.requireNonNull(textInputEditTextPhoneNumber.getText()).toString().replace(" ", "");
        // 手机号（精确）
        if (RegularUtils.allMobile(phoneNumber)) {
            textInputEditTextPhoneNumber.setText(phoneNumber);
            loginActivityListener.handleWithPhoneNumber(appCompatActivity, phoneNumber);
        } else {
            textInputLayoutInputPhoneNumber.setError(appCompatActivity.getString(R.string.inputCorrectPhoneNumber));
        }
    }
}