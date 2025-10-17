package com.zsp.today.module.detect;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.zsp.today.R;
import com.zsp.today.module.detect.kit.DetectActivityKit;

import pool.base.BasePoolActivity;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2025/10/16.
 *
 * @author 郑少鹏
 * @desc 探测页
 */
public class DetectActivity extends BasePoolActivity {
    private LinearLayout detectActivityLl;
    private MaterialCardView detectActivityMcvResult;
    private TextView detectActivityTvResult;
    private LottieAnimationView detectActivityLavDetect;
    private MaterialButtonToggleGroup detectActivityMbtg;
    private TextView detectActivityTvIllustrate;
    /**
     * 探测页配套原件
     */
    private DetectActivityKit detectActivityKit;

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
        return R.layout.activity_detect;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // LinearLayout
        detectActivityLl = findViewById(R.id.detectActivityLl);
        // MaterialCardView
        detectActivityMcvResult = findViewById(R.id.detectActivityMcvResult);
        // TextView
        detectActivityTvResult = findViewById(R.id.detectActivityTvResult);
        // LottieAnimationView
        detectActivityLavDetect = findViewById(R.id.detectActivityLavDetect);
        // MaterialButtonToggleGroup
        detectActivityMbtg = findViewById(R.id.detectActivityMbtg);
        // TextView
        detectActivityTvIllustrate = findViewById(R.id.detectActivityTvIllustrate);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        detectActivityKit = new DetectActivityKit();
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
        detectActivityKit.execute(this, detectActivityLl, detectActivityMcvResult, detectActivityTvResult, detectActivityLavDetect, detectActivityMbtg, detectActivityTvIllustrate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detectActivityKit.stopDetect(detectActivityMcvResult, detectActivityLavDetect);
    }
}