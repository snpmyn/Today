package com.zsp.today.module.dangerous.kit;

import android.Manifest;
import android.animation.ValueAnimator;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.amap.kit.AmapLocationKit;
import com.zsp.amap.value.AmapConstant;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.kit.BackupKit;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;
import com.zsp.today.module.dangerous.value.DangerousCondition;
import com.zsp.today.module.dangerous.value.DangerousConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import litepal.kit.LitePalKit;
import util.list.ListUtils;
import util.mmkv.MmkvKit;
import util.validate.RegularUtils;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.BocLottieClickDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.materialalertdialog.kit.MaterialAlertDialogBuilderKit;
import widget.dialog.materialalertdialog.kit.UseGuideMaterialAlertDialogKit;
import widget.location.value.LocationConstant;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.sms.kit.SmsKit;
import widget.toast.ToastKit;

/**
 * Created on 2025/8/19.
 *
 * @author 郑少鹏
 * @desc 险情页配套原件
 */
public class DangerousActivityKit implements SmsKit.SmsKitSendListener, SmsKit.SmsKitDeliverListener {
    /**
     * 活动
     */
    private final AppCompatActivity appCompatActivity;
    /**
     * 短信配套原件
     */
    private final SmsKit smsKit;
    /**
     * BOC Lottie 点击对话框
     */
    private BocLottieClickDialog bocLottieClickDialog;

    /**
     * constructor
     *
     * @param appCompatActivity 活动
     */
    public DangerousActivityKit(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        smsKit = new SmsKit(this.appCompatActivity);
    }

    /**
     * 检查发送短信权限
     *
     * @param showUseGuideAfterJustEnterPage 是否是刚进入页面后显示使用指南
     */
    public void checkSendSmsPermission(boolean showUseGuideAfterJustEnterPage) {
        PermissionxKit.execute(appCompatActivity, List.of(Manifest.permission.SEND_SMS), R.string.sendSmsAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                showUseGuide(showUseGuideAfterJustEnterPage);
            }

            @Override
            public void allGrantedContrary() {
                appCompatActivity.finish();
            }
        });
    }

    /**
     * 显示使用指南
     *
     * @param showUseGuideAfterJustEnterPage 是否是刚进入页面后显示使用指南
     */
    public void showUseGuide(boolean showUseGuideAfterJustEnterPage) {
        if (showUseGuideAfterJustEnterPage && MmkvKit.defaultMmkv().decodeBool(DangerousConstant.DANGEROUS_ACTIVITY_$_USE_GUIDE)) {
            return;
        }
        UseGuideMaterialAlertDialogKit useGuideMaterialAlertDialogKit = new UseGuideMaterialAlertDialogKit();
        useGuideMaterialAlertDialogKit.prepareData("步骤一", "☀ 输入险情通知 ☀\n\n令狐少侠，我现处危险中。马上帮我报警，不要回电。\n\n如上编辑即可，发送短信时自动附带你所处位置的定位信息。", "关闭", "下一步");
        useGuideMaterialAlertDialogKit.prepareData("步骤二", "☀ 输入紧急联系人手机号 ☀\n\n输入令狐少侠的手机号\n\n遇险情时向令狐少侠一键发送短信", "上一步", "下一步");
        useGuideMaterialAlertDialogKit.prepareData("步骤三", "☀ 保存配置 ☀\n\n建议尽早配置，以备不时之需。", "上一步", "去使用");
        useGuideMaterialAlertDialogKit.show(appCompatActivity, 0, false, () -> {
            if (MmkvKit.defaultMmkv().decodeBool(DangerousConstant.DANGEROUS_ACTIVITY_$_USE_GUIDE)) {
                return;
            }
            MmkvKit.defaultMmkv().encode(DangerousConstant.DANGEROUS_ACTIVITY_$_USE_GUIDE, true);
        });
    }

    /**
     * 预显示
     *
     * @param textInputEditTextDangerousNotice             险情通知框
     * @param textInputEditTextEmergencyContactPhoneNumber 紧急联系人手机号框
     */
    public void preShow(TextInputEditText textInputEditTextDangerousNotice, TextInputEditText textInputEditTextEmergencyContactPhoneNumber) {
        List<DangerousDataBaseTable> dangerousDataBaseTableList = LitePalKit.getInstance().queryByWhere(DangerousDataBaseTable.class, DangerousCondition.DANGEROUS_PHONE_NUMBER, App.getAppInstance().getPhoneNumber());
        if (ListUtils.listIsEmpty(dangerousDataBaseTableList)) {
            return;
        }
        DangerousDataBaseTable dangerousDataBaseTable = dangerousDataBaseTableList.get(0);
        textInputEditTextDangerousNotice.setText(dangerousDataBaseTable.getDangerousNotice());
        textInputEditTextEmergencyContactPhoneNumber.setText(dangerousDataBaseTable.getEmergencyContactPhoneNumber());
    }

    /**
     * 保存配置或发送
     *
     * @param send                                            发送
     * @param textInputLayoutInputDangerousNotice             险情通知输入框
     * @param textInputEditTextDangerousNotice                险情通知框
     * @param textInputLayoutInputEmergencyContactPhoneNumber 紧急联系人手机号输入框
     * @param textInputEditTextEmergencyContactPhoneNumber    紧急联系人手机号框
     */
    public void saveConfigOrSend(boolean send, TextInputLayout textInputLayoutInputDangerousNotice, @NonNull TextInputEditText textInputEditTextDangerousNotice, TextInputLayout textInputLayoutInputEmergencyContactPhoneNumber, TextInputEditText textInputEditTextEmergencyContactPhoneNumber) {
        String dangerousNotice = Objects.requireNonNull(textInputEditTextDangerousNotice.getText()).toString();
        // 输入险情通知
        if (TextUtils.isEmpty(dangerousNotice)) {
            textInputLayoutInputDangerousNotice.setError(appCompatActivity.getString(R.string.inputDangerousNotice));
            return;
        }
        String emergencyContactPhoneNumber = Objects.requireNonNull(textInputEditTextEmergencyContactPhoneNumber.getText()).toString();
        // 输入紧急联系人手机号
        if (TextUtils.isEmpty(emergencyContactPhoneNumber)) {
            textInputLayoutInputEmergencyContactPhoneNumber.setError(appCompatActivity.getString(R.string.inputEmergencyContactPhoneNumber));
            return;
        }
        // 紧急联系人手机号（精确）
        if (!RegularUtils.allMobile(emergencyContactPhoneNumber)) {
            textInputLayoutInputEmergencyContactPhoneNumber.setError(appCompatActivity.getString(R.string.inputCorrectEmergencyContactPhoneNumber));
            return;
        }
        if (send) {
            // 对话框提示
            bocLottieClickDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieClickDialog(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.updatingLocation), appCompatActivity.getString(R.string.hurryUpSendTheCoarseLocation), ValueAnimator.INFINITE, null, () -> {
                BocDialogKit.getInstance(appCompatActivity).end();
                // 内容
                String content = dangerousNotice + handleLocationInfo();
                // 对话框提示
                new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).setTitle(R.string.dangerousNotice).setMessage(content).setPositiveButton(R.string.send, (dialog, which) -> {
                    dialog.dismiss();
                    sendSms(content, emergencyContactPhoneNumber);
                }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
            }, null);
            // 开始
            AmapLocationKit.getInstance().start();
            // 处理配置
            handleConfig(dangerousNotice, emergencyContactPhoneNumber, false);
        } else {
            // 处理配置
            handleConfig(dangerousNotice, emergencyContactPhoneNumber, true);
        }
    }

    /**
     * 处理定位信息
     * <p>
     * 场景一
     * 更新定位未结束前发送
     * 首先使用本地最近一次存储的高德地图定位信息，为空则改用本地最近一次存储的原生定位信息。
     * <p>
     * 场景二
     * 更新定位成功后发送
     * 使用高德地图最新定位信息
     * <p>
     * 场景三
     * 更新定位失败后发送
     * 首先使用本地最近一次存储的高德地图定位信息，为空则改用本地最近一次存储的原生定位信息。
     *
     * @return 定位信息
     */
    private String handleLocationInfo() {
        String locationInfo;
        locationInfo = MmkvKit.defaultMmkv().decodeString(AmapConstant.AMAP_$_LOCATION_INFO);
        if (TextUtils.isEmpty(locationInfo)) {
            locationInfo = MmkvKit.defaultMmkv().decodeString(LocationConstant.LOCATION_INFO);
            if (TextUtils.isEmpty(locationInfo)) {
                locationInfo = appCompatActivity.getString(R.string.canNotGetLocationInfo);
            }
        }
        return locationInfo;
    }

    /**
     * 发送短信
     *
     * @param content                     内容
     * @param emergencyContactPhoneNumber 紧急联系人手机号
     */
    private void sendSms(String content, String emergencyContactPhoneNumber) {
        // 设短信配套原件发送监听
        smsKit.setSmsKitSendListener(this);
        // 设短信配套原件传送监听
        smsKit.setSmsKitDeliverListener(this);
        // 目标地址集
        List<String> destinationAddressList = new ArrayList<>();
        destinationAddressList.add(emergencyContactPhoneNumber);
        if (destinationAddressList.size() == 1) {
            // 单发
            smsKit.singleShot(appCompatActivity, destinationAddressList.get(0), content);
        } else {
            // 群发
            smsKit.massSend(appCompatActivity, destinationAddressList, content);
        }
    }

    /**
     * 处理配置
     *
     * @param dangerousNotice             险情通知
     * @param emergencyContactPhoneNumber 紧急联系人手机号
     * @param hint                        提示否
     */
    private void handleConfig(String dangerousNotice, String emergencyContactPhoneNumber, boolean hint) {
        // 查询险情数据库表集合
        List<DangerousDataBaseTable> dangerousDataBaseTableList = LitePalKit.getInstance().queryByWhere(DangerousDataBaseTable.class, DangerousCondition.DANGEROUS_PHONE_NUMBER, App.getAppInstance().getPhoneNumber());
        // 保存配置
        if (ListUtils.listIsEmpty(dangerousDataBaseTableList)) {
            DangerousDataBaseTable dangerousDataBaseTableNew = new DangerousDataBaseTable(App.getAppInstance().getPhoneNumber(), null, dangerousNotice, emergencyContactPhoneNumber);
            if (LitePalKit.getInstance().singleSave(dangerousDataBaseTableNew)) {
                // 备份
                BackupKit.getInstance().backup(appCompatActivity, DangerousDataBaseTable.class, null);
                if (hint) {
                    BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogOne(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.configSuccessful), 0, () -> BocDialogKit.getInstance(appCompatActivity).end(), null);
                }
            }
            return;
        }
        // 配置无变化
        DangerousDataBaseTable dangerousDataBaseTableOld = dangerousDataBaseTableList.get(0);
        if (TextUtils.equals(dangerousNotice, dangerousDataBaseTableOld.getDangerousNotice()) && TextUtils.equals(emergencyContactPhoneNumber, dangerousDataBaseTableOld.getEmergencyContactPhoneNumber())) {
            if (hint) {
                ToastKit.showShort(appCompatActivity.getString(R.string.configNoChange));
            }
            return;
        }
        // 更新配置
        DangerousDataBaseTable functionDataBaseTableUpdate = new DangerousDataBaseTable(dangerousDataBaseTableOld.getPhoneNumber(), dangerousDataBaseTableOld.getDate(), dangerousNotice, emergencyContactPhoneNumber);
        if (LitePalKit.getInstance().singleUpdate(functionDataBaseTableUpdate, dangerousDataBaseTableOld.getBaseObjectId()) != 0) {
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, DangerousDataBaseTable.class, null);
            if (hint) {
                BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogOne(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.updateSuccessful), 0, () -> BocDialogKit.getInstance(appCompatActivity).end(), null);
            }
        }
    }

    /**
     * 更新定位
     * <p>
     * 场景一
     * 更新定位未结束前发送
     * 首先使用本地最近一次存储的高德地图定位信息，为空则改用本地最近一次存储的原生定位信息。
     * <p>
     * 场景二
     * 更新定位成功后发送
     * 使用高德地图最新定位信息
     * <p>
     * 场景三
     * 更新定位失败后发送
     * 首先使用本地最近一次存储的高德地图定位信息，为空则改用本地最近一次存储的原生定位信息。
     *
     * @param updateLocationSuccessful 更新定位否
     */
    public void updateLocation(boolean updateLocationSuccessful) {
        if (BocDialogKit.getInstance(appCompatActivity).areEnd()) {
            // 更新定位未结束前已发送短信则不做处理
            return;
        }
        if (updateLocationSuccessful) {
            bocLottieClickDialog.update(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.updateLocationSuccessful), appCompatActivity.getString(R.string.sendQuickly), ValueAnimator.INFINITE, null);
        } else {
            bocLottieClickDialog.update(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.updateLocationFail), appCompatActivity.getString(R.string.hurryUpSendTheCoarseLocation), ValueAnimator.INFINITE, null);
        }
    }

    /**
     * 反注册接收器
     *
     * @param appCompatActivity 活动
     */
    public void unregisterReceiver(AppCompatActivity appCompatActivity) {
        smsKit.unregisterReceiver(appCompatActivity);
    }

    /**
     * 传送 (RESULT_OK)
     */
    @Override
    public void deliverResultOk() {
        ToastKit.showShort(appCompatActivity.getString(R.string.smsHasBeenDelivered));
    }

    /**
     * 传送 (RESULT_ERROR_GENERIC_FAILURE)
     */
    @Override
    public void deliverResultErrorCenericFailure() {
        ToastKit.showShort(appCompatActivity.getString(R.string.smsDeliverFail));
    }

    /**
     * 发送 (RESULT_OK)
     */
    @Override
    public void sendResultOk() {
        ToastKit.showShort(appCompatActivity.getString(R.string.smsHasBeenSent));
    }

    /**
     * 发送 (RESULT_ERROR_GENERIC_FAILURE)
     */
    @Override
    public void sendResultErrorCenericFailure() {
        ToastKit.showShort(appCompatActivity.getString(R.string.smsSendFail));
    }
}