package com.zsp.today.module.dangerous;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.today.R;
import com.zsp.today.module.dangerous.kit.DangerousActivityKit;
import com.zsp.today.basic.value.RxBusConstant;

import pool.base.BasePoolActivity;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import widget.location.kit.LocationKit;
import widget.textwatcher.CustomTextWatcher;

/**
 * @decs: 险情页
 * @author: 郑少鹏
 * @date: 2025/8/19 14:09
 * @version: v 1.0
 */
public class DangerousActivity extends BasePoolActivity implements View.OnClickListener {
    private MaterialToolbar dangerousActivityMt;
    private TextInputLayout dangerousActivityLlTilInputDangerousNotice;
    private TextInputEditText dangerousActivityLlTietDangerousNotice;
    private TextInputLayout dangerousActivityLlTilInputEmergencyContactPhoneNumber;
    private TextInputEditText dangerousActivityLlTietEmergencyContactPhoneNumber;
    private MaterialButton dangerousActivityMbSaveConfig;
    private MaterialButton dangerousActivityMbSend;
    /**
     * 险情页配套原件
     */
    private DangerousActivityKit dangerousActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_dangerous;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        dangerousActivityMt = findViewById(R.id.dangerousActivityMt);
        dangerousActivityLlTilInputDangerousNotice = findViewById(R.id.dangerousActivityLlTilInputDangerousNotice);
        dangerousActivityLlTietDangerousNotice = findViewById(R.id.dangerousActivityLlTietDangerousNotice);
        dangerousActivityLlTilInputEmergencyContactPhoneNumber = findViewById(R.id.dangerousActivityLlTilInputEmergencyContactPhoneNumber);
        dangerousActivityLlTietEmergencyContactPhoneNumber = findViewById(R.id.dangerousActivityLlTietEmergencyContactPhoneNumber);
        dangerousActivityMbSaveConfig = findViewById(R.id.dangerousActivityMbSaveConfig);
        dangerousActivityMbSend = findViewById(R.id.dangerousActivityMbSend);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        dangerousActivityKit = new DangerousActivityKit(this);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        dangerousActivityMt.setNavigationOnClickListener(v -> finish());
        dangerousActivityMt.setOnMenuItemClickListener(item -> {
            // 显示使用指南
            dangerousActivityKit.showUseGuide(DangerousActivity.this, false);
            return true;
        });
        // EditText
        dangerousActivityLlTietDangerousNotice.addTextChangedListener(new CustomTextWatcher(dangerousActivityLlTilInputDangerousNotice, dangerousActivityLlTietDangerousNotice));
        dangerousActivityLlTietEmergencyContactPhoneNumber.addTextChangedListener(new CustomTextWatcher(dangerousActivityLlTilInputEmergencyContactPhoneNumber, dangerousActivityLlTietEmergencyContactPhoneNumber));
        // Button
        dangerousActivityMbSaveConfig.setOnClickListener(this);
        dangerousActivityMbSend.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 开始定位
        LocationKit.getInstance().execute(this, null);
        // 检查发送短信权限
        dangerousActivityKit.checkSendSmsPermission(this, true);
        // 预显示
        dangerousActivityKit.preShow(dangerousActivityLlTietDangerousNotice, dangerousActivityLlTietEmergencyContactPhoneNumber);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View v) {
        int viewId = v.getId();
        if (viewId == R.id.dangerousActivityMbSaveConfig) {
            // 保存配置
            dangerousActivityKit.saveConfigOrSend(this, false, dangerousActivityLlTilInputDangerousNotice, dangerousActivityLlTietDangerousNotice, dangerousActivityLlTilInputEmergencyContactPhoneNumber, dangerousActivityLlTietEmergencyContactPhoneNumber);
        } else if (viewId == R.id.dangerousActivityMbSend) {
            // 发送
            dangerousActivityKit.saveConfigOrSend(this, true, dangerousActivityLlTilInputDangerousNotice, dangerousActivityLlTietDangerousNotice, dangerousActivityLlTilInputEmergencyContactPhoneNumber, dangerousActivityLlTietEmergencyContactPhoneNumber);
        }
    }

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    @Override
    protected int[] hideSoftByEditViewIds() {
        return new int[]{R.id.dangerousActivityLlTietDangerousNotice, R.id.dangerousActivityLlTietEmergencyContactPhoneNumber};
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION)})
    public void accountDetailActivityRefreshAccount(Integer integer) {
        if (integer == RxBusConstant.DANGEROUS_ACTIVITY_$_UPDATE_LOCATION_CODE) {
            dangerousActivityKit.updateLocation(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除更新
        LocationKit.getInstance().removeUpdates();
        // 反注册接收器
        dangerousActivityKit.unregisterReceiver(this);
    }
}