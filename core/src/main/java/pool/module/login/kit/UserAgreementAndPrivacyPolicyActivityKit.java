package pool.module.login.kit;

import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pool.application.BasePoolApp;
import pool.module.login.UserAgreementAndPrivacyPolicyActivity;
import pool.value.PoolConstant;
import util.intent.IntentJump;
import util.intent.IntentVerify;
import widget.webview.WebViewKit;

/**
 * Created on 2021/9/16
 *
 * @author zsp
 * @desc 用户协议和隐私政策页配套元件
 */
public class UserAgreementAndPrivacyPolicyActivityKit {
    /**
     * 设置标题
     *
     * @param webView         TextView
     * @param materialToolbar MaterialToolbar
     */
    public void setTitle(@NotNull WebView webView, MaterialToolbar materialToolbar) {
        WebViewKit.setWebChromeClient(webView, materialToolbar);
    }

    /**
     * 显示用户协议或隐私政策
     *
     * @param appCompatActivity 活动
     * @param webView           WebView
     */
    public void showUserAgreementOrPrivacyPolicy(@NonNull AppCompatActivity appCompatActivity, WebView webView) {
        if (TextUtils.equals(IntentVerify.getStringExtra(appCompatActivity.getIntent(), PoolConstant.USER_AGREEMENT), PoolConstant.USER_AGREEMENT)) {
            WebViewKit.loadUrl(webView, Objects.requireNonNull(BasePoolApp.getConfigMap().get(1)).get(3));
        } else if (TextUtils.equals(IntentVerify.getStringExtra(appCompatActivity.getIntent(), PoolConstant.PRIVACY_POLICY), PoolConstant.PRIVACY_POLICY)) {
            WebViewKit.loadUrl(webView, Objects.requireNonNull(BasePoolApp.getConfigMap().get(1)).get(4));
        }
    }

    /**
     * 显示用户协议和隐私政策策
     *
     * @param appCompatActivity 活动
     * @param functionalName    功能名称
     */
    public void showUserAgreementOrPrivacyPolicy(AppCompatActivity appCompatActivity, String functionalName) {
        Intent intentUserAgreement = new Intent();
        intentUserAgreement.putExtra(functionalName, functionalName);
        IntentJump.getInstance().jumpWithAnimation(intentUserAgreement, appCompatActivity, false, UserAgreementAndPrivacyPolicyActivity.class, 0, 0);
    }
}