package pool.login.kit;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zsp.core.R;

import pool.login.listener.LoginActivityListener;
import pool.value.PoolConstant;
import util.mmkv.MmkvKit;
import util.number.NumberFormatUtils;
import util.toast.ToastKit;
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
     * @param editTextPleaseInputPhoneNumber 请输入手机号输入框
     */
    public void phoneNumberPreShow(@NonNull EditText editTextPleaseInputPhoneNumber) {
        editTextPleaseInputPhoneNumber.setText(NumberFormatUtils.formatPhoneNumberTwo(MmkvKit.defaultMmkv().decodeString(PoolConstant.LOGIN_$_PHONE_NUMBER)));
        editTextPleaseInputPhoneNumber.setSelection(editTextPleaseInputPhoneNumber.getText().length());
    }

    /**
     * 登录
     *
     * @param appCompatActivity              活动
     * @param editTextPleaseInputPhoneNumber 请输入手机号输入框
     * @param loginActivityListener          登录页监听
     */
    public void login(AppCompatActivity appCompatActivity, @NonNull EditText editTextPleaseInputPhoneNumber, LoginActivityListener loginActivityListener) {
        String phoneNumber = editTextPleaseInputPhoneNumber.getText().toString().replace(" ", "");
        // 手机号（精确）
        if (RegularUtils.allMobile(phoneNumber)) {
            String formatPhoneNumber = NumberFormatUtils.formatPhoneNumberTwo(phoneNumber);
            editTextPleaseInputPhoneNumber.setText(formatPhoneNumber);
            loginActivityListener.handleWithPhoneNumber(appCompatActivity, formatPhoneNumber);
        } else {
            ToastKit.showShort(appCompatActivity.getString(R.string.pleaseInputCorrectPhoneNumber));
        }
    }
}
