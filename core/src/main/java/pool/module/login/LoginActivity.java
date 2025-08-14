package pool.module.login;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.login.kit.LoginActivityKit;
import pool.module.login.kit.UserAgreementAndPrivacyPolicyActivityKit;
import pool.module.login.listener.LoginActivityListener;
import pool.value.PoolConstant;

/**
 * @desc: 登录页
 * @author: zsp
 * @date: 2021/9/17 4:31 下午
 */
public class LoginActivity extends BasePoolActivity implements View.OnClickListener {
    /**
     * 登录页监听
     */
    private static LoginActivityListener loginActivityListener;
    private MaterialToolbar loginActivityMt;
    private EditText loginActivityEtPleaseInputPhoneNumber;
    private ImageView loginActivityIvPhoneNumberClear;
    private MaterialButton loginActivityMbLogin;
    private TextView loginActivityTvUserAgreement;
    private TextView loginActivityTvPrivacyPolicy;
    /**
     * 用户协议和隐私政策页配套元件
     */
    private UserAgreementAndPrivacyPolicyActivityKit userAgreementAndPrivacyPolicyActivityKit;
    /**
     * 登录页配套元件
     */
    private LoginActivityKit loginActivityKit;

    /**
     * 设登录页监听
     *
     * @param loginActivityListener 登录页监听
     */
    public static void setLoginActivityListener(LoginActivityListener loginActivityListener) {
        LoginActivity.loginActivityListener = loginActivityListener;
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_login;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        loginActivityMt = findViewById(R.id.loginActivityMt);
        loginActivityEtPleaseInputPhoneNumber = findViewById(R.id.loginActivityEtPleaseInputPhoneNumber);
        loginActivityIvPhoneNumberClear = findViewById(R.id.loginActivityIvPhoneNumberClear);
        loginActivityMbLogin = findViewById(R.id.loginActivityMbLogin);
        loginActivityTvUserAgreement = findViewById(R.id.loginActivityTvUserAgreement);
        loginActivityTvPrivacyPolicy = findViewById(R.id.loginActivityTvPrivacyPolicy);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 用户协议和隐私政策页配套元件
        userAgreementAndPrivacyPolicyActivityKit = new UserAgreementAndPrivacyPolicyActivityKit();
        // 登录页登录页配套元件
        loginActivityKit = new LoginActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        loginActivityMt.setNavigationOnClickListener(v -> finish());
        // 手机号
        loginActivityEtPleaseInputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    loginActivityMbLogin.setEnabled(true);
                    loginActivityMbLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.fontInput));
                    loginActivityMbLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.basic));
                } else {
                    loginActivityMbLogin.setEnabled(false);
                    loginActivityMbLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.fontHint));
                    loginActivityMbLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.pool_login_button_background));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 控件
        loginActivityIvPhoneNumberClear.setOnClickListener(this);
        loginActivityMbLogin.setOnClickListener(this);
        loginActivityTvUserAgreement.setOnClickListener(this);
        loginActivityTvPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.loginActivityIvPhoneNumberClear) {
            // 清手机号
            loginActivityEtPleaseInputPhoneNumber.setText("");
        } else if (id == R.id.loginActivityMbLogin) {
            // 登录
            loginActivityKit.login(this, loginActivityEtPleaseInputPhoneNumber, loginActivityListener);
        } else if (id == R.id.loginActivityTvUserAgreement) {
            // 用户协议
            userAgreementAndPrivacyPolicyActivityKit.showUserAgreementAndPrivacyPolicy(this, PoolConstant.USER_AGREEMENT);
        } else if (id == R.id.loginActivityTvPrivacyPolicy) {
            // 政策隐私
            userAgreementAndPrivacyPolicyActivityKit.showUserAgreementAndPrivacyPolicy(this, PoolConstant.PRIVACY_POLICY);
        }
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        loginActivityKit.phoneNumberPreShow(loginActivityEtPleaseInputPhoneNumber);
    }

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    @Override
    protected int[] hideSoftByEditViewIds() {
        return new int[]{R.id.loginActivityEtPleaseInputPhoneNumber};
    }
}