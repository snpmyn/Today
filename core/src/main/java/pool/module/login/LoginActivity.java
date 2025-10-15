package pool.module.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.login.kit.LoginActivityKit;
import pool.module.login.kit.UserAgreementAndPrivacyPolicyActivityKit;
import pool.module.login.listener.LoginActivityListener;
import pool.value.PoolConstant;
import util.animation.AnimationManager;
import widget.textwatcher.CustomTextWatcher;
import widget.transition.kit.TransitionKit;

/**
 * @desc: 登录页
 * @author: zsp
 * @date: 2021/9/17 4:31 下午
 */
public class LoginActivity extends BasePoolActivity implements View.OnClickListener {
    private TextView loginActivityTvLoginPageTopHint;
    private TextInputLayout loginActivityTilInputPhoneNumber;
    private TextInputEditText loginActivityTietPhoneNumber;
    private MaterialButton loginActivityMbLogin;
    private MaterialCardView loginActivityMcvUserAgreement;
    private MaterialCardView loginActivityMcvPrivacyPolicy;
    /**
     * 登录页监听
     */
    private static LoginActivityListener loginActivityListener;
    /**
     * 登录页配套元件
     */
    private LoginActivityKit loginActivityKit;
    /**
     * 用户协议和隐私政策页配套元件
     */
    private UserAgreementAndPrivacyPolicyActivityKit userAgreementAndPrivacyPolicyActivityKit;

    /**
     * 设登录页监听
     *
     * @param loginActivityListener 登录页监听
     */
    public static void setLoginActivityListener(LoginActivityListener loginActivityListener) {
        LoginActivity.loginActivityListener = loginActivityListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TransitionKit.getInstance().endPageSetting(this);
        super.onCreate(savedInstanceState);
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
        loginActivityTvLoginPageTopHint = findViewById(R.id.loginActivityTvLoginPageTopHint);
        loginActivityTilInputPhoneNumber = findViewById(R.id.loginActivityTilInputPhoneNumber);
        loginActivityTietPhoneNumber = findViewById(R.id.loginActivityTietPhoneNumber);
        loginActivityMbLogin = findViewById(R.id.loginActivityMbLogin);
        loginActivityMcvUserAgreement = findViewById(R.id.loginActivityMcvUserAgreement);
        loginActivityMcvPrivacyPolicy = findViewById(R.id.loginActivityMcvPrivacyPolicy);
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
        // TextInputEditText
        loginActivityTietPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginActivityMbLogin.setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginActivityTietPhoneNumber.addTextChangedListener(new CustomTextWatcher(loginActivityTilInputPhoneNumber, loginActivityTietPhoneNumber));
        // MaterialButton
        loginActivityMbLogin.setOnClickListener(this);
        // MaterialCardView
        loginActivityMcvUserAgreement.setOnClickListener(this);
        loginActivityMcvPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.loginActivityMbLogin) {
            // 登录
            loginActivityKit.login(this, loginActivityTilInputPhoneNumber, loginActivityTietPhoneNumber, loginActivityListener);
        } else if (id == R.id.loginActivityMcvUserAgreement) {
            // 用户协议
            userAgreementAndPrivacyPolicyActivityKit.showUserAgreementOrPrivacyPolicy(this, PoolConstant.USER_AGREEMENT);
        } else if (id == R.id.loginActivityMcvPrivacyPolicy) {
            // 政策隐私
            userAgreementAndPrivacyPolicyActivityKit.showUserAgreementOrPrivacyPolicy(this, PoolConstant.PRIVACY_POLICY);
        }
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        AnimationManager.alphaShow(loginActivityTvLoginPageTopHint, 1000, null);
        loginActivityKit.phoneNumberPreShow(loginActivityTietPhoneNumber);
    }

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    @Override
    protected int[] hideSoftByEditViewIds() {
        return new int[]{R.id.loginActivityTietPhoneNumber};
    }
}