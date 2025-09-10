package pool.module.splash.kit;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.zsp.core.R;

import pool.application.BasePoolApp;
import pool.module.login.kit.UserAgreementAndPrivacyPolicyActivityKit;
import pool.module.splash.listener.SplashActivityListener;
import pool.value.PoolConstant;
import util.activity.ActivitySuperviseManager;
import util.mmkv.MmkvKit;
import util.net.NetManager;
import widget.dialog.customdialog.BaseDialog;
import widget.dialog.customdialog.BaseViewConvertListener;
import widget.dialog.customdialog.CustomDialog;
import widget.dialog.customdialog.ViewHolder;
import widget.dialog.materialalertdialog.kit.MaterialAlertDialogBuilderKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.spannablestringbuilder.SpannableStringBuilderKit;

/**
 * Created on 2021/9/16
 *
 * @author zsp
 * @desc 闪屏页配套元件
 */
public class SplashActivityKit {
    private static SplashActivityListener splashActivityListener;

    /**
     * 执行
     *
     * @param appCompatActivity 活动
     */
    public void execute(AppCompatActivity appCompatActivity) {
        if (MmkvKit.defaultMmkv().decodeBool(PoolConstant.USER_AGREEMENT_AND_PRIVACY_POLICY)) {
            checkConnect(appCompatActivity);
        } else {
            userAgreementAndPrivacyPolicy(appCompatActivity);
        }
    }

    /**
     * 用户协议和隐私政策
     *
     * @param appCompatActivity 活动
     */
    private void userAgreementAndPrivacyPolicy(@NonNull AppCompatActivity appCompatActivity) {
        // 不同意并退出
        // 同意
        CustomDialog.init().setLayoutId(R.layout.dialog_user_agreement_and_privacy_policy).setBaseViewConvertListener(new BaseViewConvertListener() {
            @Override
            public void convertView(ViewHolder holder, final BaseDialog dialog) {
                TextView dialogUserAgreementAndPrivacyPolicyTvUserAgreementAndPrivacyPolicyContent = holder.getView(R.id.dialogUserAgreementAndPrivacyPolicyTvUserAgreementAndPrivacyPolicyContent);
                // 富文本构建器配套原件
                SpannableStringBuilderKit spannableStringBuilderKit = new SpannableStringBuilderKit(dialogUserAgreementAndPrivacyPolicyTvUserAgreementAndPrivacyPolicyContent.getText().toString());
                spannableStringBuilderKit.setClickableSpan(ContextCompat.getColor(appCompatActivity, R.color.color_FF6AAEFF), false, 46, 52, 0, () -> showUserAgreementAndPrivacyPolicy(appCompatActivity, PoolConstant.USER_AGREEMENT));
                spannableStringBuilderKit.setClickableSpan(ContextCompat.getColor(appCompatActivity, R.color.color_FF6AAEFF), false, 53, 59, 0, () -> showUserAgreementAndPrivacyPolicy(appCompatActivity, PoolConstant.PRIVACY_POLICY));
                // 配置可点击文本控件
                spannableStringBuilderKit.configClickableSpanTextView(dialogUserAgreementAndPrivacyPolicyTvUserAgreementAndPrivacyPolicyContent, spannableStringBuilderKit);
                // 不同意并退出
                TextView dialogUserAgreementAndPrivacyPolicyTvNotAgreeAngLoginOut = holder.getView(R.id.dialogUserAgreementAndPrivacyPolicyTvNotAgreeAngLoginOut);
                dialogUserAgreementAndPrivacyPolicyTvNotAgreeAngLoginOut.setOnClickListener(v -> {
                    dialog.dismiss();
                    appCompatActivity.finish();
                });
                // 同意
                TextView dialogUserAgreementAndPrivacyPolicyTvAgree = holder.getView(R.id.dialogUserAgreementAndPrivacyPolicyTvAgree);
                dialogUserAgreementAndPrivacyPolicyTvAgree.setOnClickListener(v -> {
                    dialog.dismiss();
                    MmkvKit.defaultMmkv().encode(PoolConstant.USER_AGREEMENT_AND_PRIVACY_POLICY, true);
                    checkConnect(appCompatActivity);
                });
            }
        }).setWidth(300).setOutCancel(false).setAnimStyle(R.style.DefaultAnimation).show(appCompatActivity.getSupportFragmentManager());
    }

    /**
     * 显示用户协议和隐私政策策
     *
     * @param appCompatActivity 活动
     * @param functionalName    功能名称
     */
    public void showUserAgreementAndPrivacyPolicy(AppCompatActivity appCompatActivity, String functionalName) {
        UserAgreementAndPrivacyPolicyActivityKit userAgreementAndPrivacyPolicyActivityKit = new UserAgreementAndPrivacyPolicyActivityKit();
        userAgreementAndPrivacyPolicyActivityKit.showUserAgreementAndPrivacyPolicy(appCompatActivity, functionalName);
    }

    /**
     * 检查连接
     *
     * @param appCompatActivity 活动
     */
    private void checkConnect(AppCompatActivity appCompatActivity) {
        if (!NetManager.areNetConnected(appCompatActivity)) {
            new MaterialAlertDialogBuilderKit(appCompatActivity).setTitle(R.string.hint).setMessage(R.string.currentNoNetwork).setPositiveButton(R.string.continueUse, (dialog, which) -> {
                dialog.dismiss();
                requestPermissions(appCompatActivity);
            }).setCancelable(false).show();
            return;
        }
        requestPermissions(appCompatActivity);
    }

    /**
     * 请求权限
     *
     * @param fragmentActivity FragmentActivity
     */
    private void requestPermissions(FragmentActivity fragmentActivity) {
        PermissionxKit.execute(fragmentActivity, BasePoolApp.getPermissionList(), R.string.coreFundamentalAreBasedOnThePermission, R.string.youNeedToAllowNecessaryPermissionInSettingManually, R.string.agree, R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                splashActivityListener.distribute((AppCompatActivity) fragmentActivity);
            }

            @Override
            public void allGrantedContrary() {
                ActivitySuperviseManager.getInstance().appExit();
            }
        });
    }

    /**
     * 设闪屏页监听
     *
     * @param splashActivityListener 闪屏页监听
     */
    public void setSplashActivityListener(SplashActivityListener splashActivityListener) {
        SplashActivityKit.splashActivityListener = splashActivityListener;
    }
}