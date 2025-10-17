package com.zsp.today.module.lingfang;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.lingfang.kit.LingFangActivityKit;

import pool.base.BasePoolActivity;
import widget.sensor.bagua.BaguaCompassView;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2025/10/16.
 *
 * @author 郑少鹏
 * @desc 灵方页
 */
public class LingFangActivity extends BasePoolActivity implements View.OnClickListener {
    private ImageView lingFangActivityIvBackground;
    private BaguaCompassView lingFangActivityBcv;
    private TextView lingFangActivityTv;
    private MaterialButton lingFangActivityMbSelectBackground;
    /**
     * 灵方页配套原件
     */
    private LingFangActivityKit lingFangActivityKit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TransitionKit.getInstance().startPageSetting(this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_ling_fang;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // ImageView
        lingFangActivityIvBackground = findViewById(R.id.lingFangActivityIvBackground);
        // BaguaCompassView
        lingFangActivityBcv = findViewById(R.id.lingFangActivityBcv);
        // TextView
        lingFangActivityTv = findViewById(R.id.lingFangActivityTv);
        // MaterialButton
        lingFangActivityMbSelectBackground = findViewById(R.id.lingFangActivityMbSelectBackground);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        lingFangActivityKit = new LingFangActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        lingFangActivityMbSelectBackground.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 设置背景
        lingFangActivityKit.setBackground(this, lingFangActivityIvBackground);
        // 执行
        lingFangActivityKit.execute(this, lingFangActivityBcv, lingFangActivityTv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lingFangActivityBcv.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lingFangActivityBcv.stop();
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.lingFangActivityMbSelectBackground) {
            // 选择背景
            lingFangActivityKit.selectBackground(this, lingFangActivityIvBackground);
        }
    }
}