package pool.module.login;

import android.webkit.WebView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.login.kit.UserAgreementAndPrivacyPolicyActivityKit;

/**
 * @desc: 用户协议和隐私政策页
 * @author: zsp
 * @date: 2021/9/16 1:52 下午
 */
public class UserAgreementAndPrivacyPolicyActivity extends BasePoolActivity {
    MaterialToolbar userAgreementAndPrivacyPolicyActivityMt;
    WebView userAgreementAndPrivacyPolicyActivityWv;
    /**
     * 用户协议和隐私政策页配套元件
     */
    private UserAgreementAndPrivacyPolicyActivityKit userAgreementAndPrivacyPolicyActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_user_agreement_and_privacy_policy;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        userAgreementAndPrivacyPolicyActivityMt = findViewById(R.id.userAgreementAndPrivacyPolicyActivityMt);
        userAgreementAndPrivacyPolicyActivityWv = findViewById(R.id.userAgreementAndPrivacyPolicyActivityWv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        userAgreementAndPrivacyPolicyActivityKit = new UserAgreementAndPrivacyPolicyActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        userAgreementAndPrivacyPolicyActivityKit.setTitle(userAgreementAndPrivacyPolicyActivityWv, userAgreementAndPrivacyPolicyActivityMt);
        userAgreementAndPrivacyPolicyActivityKit.showUserAgreementOrPrivacyPolicy(this, userAgreementAndPrivacyPolicyActivityWv);
    }
}