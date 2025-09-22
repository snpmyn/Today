package com.zsp.today.module.heartbox;

import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;
import com.zsp.today.R;
import com.zsp.today.module.heartbox.kit.HeartBoxActivityKit;

import pool.base.BasePoolActivity;
import util.handler.HandlerKit;
import util.layoutparams.LayoutParamsUtils;
import util.screen.ScreenUtils;
import util.view.ViewAntiBruteForceClickKit;
import widget.view.EmotionWaveView;
import widget.view.PaperCrushView;

/**
 * Created on 2025/9/10.
 *
 * @author 郑少鹏
 * @desc 心盒页
 */
public class HeartBoxActivity extends BasePoolActivity {
    private MaterialToolbar heartBoxActivityMt;
    private MaterialCardView heartBoxActivityMcvEmotionWaveView;
    private EmotionWaveView heartBoxActivityEwv;
    private MaterialCardView heartBoxActivityMcvPaperCrushView;
    private PaperCrushView heartBoxActivityPcv;
    private MaterialCardView heartBoxActivityMcv;
    private TextView heartBoxActivityTvDurationSize;
    private TextView heartBoxActivityTvTime;
    private TextView heartBoxActivityTvDate;
    private MaterialDivider heartBoxActivityMd;
    private MaterialButtonGroup heartBoxActivityMbg;
    private MaterialButton heartBoxActivityMbListen;
    private MaterialButton heartBoxActivityMbRelease;
    private MaterialButton heartBoxActivityMbLook;
    private MaterialButton heartBoxActivityMbPauseOrResume;
    private MaterialButton heartBoxActivityMbStartOrStop;
    private MaterialButton heartBoxActivityMbCancel;
    /**
     * 心盒页配套原件
     */
    private HeartBoxActivityKit heartBoxActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_heart_box;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // MaterialToolbar
        heartBoxActivityMt = findViewById(R.id.heartBoxActivityMt);
        // MaterialCardView
        heartBoxActivityMcvEmotionWaveView = findViewById(R.id.heartBoxActivityMcvEmotionWaveView);
        // EmotionWaveView
        heartBoxActivityEwv = findViewById(R.id.heartBoxActivityEwv);
        // MaterialCardView
        heartBoxActivityMcvPaperCrushView = findViewById(R.id.heartBoxActivityMcvPaperCrushView);
        // PaperCrushView
        heartBoxActivityPcv = findViewById(R.id.heartBoxActivityPcv);
        // MaterialCardView
        heartBoxActivityMcv = findViewById(R.id.heartBoxActivityMcv);
        // TextView
        heartBoxActivityTvDurationSize = findViewById(R.id.heartBoxActivityTvDurationSize);
        heartBoxActivityTvTime = findViewById(R.id.heartBoxActivityTvTime);
        heartBoxActivityTvDate = findViewById(R.id.heartBoxActivityTvDate);
        // MaterialDivider
        heartBoxActivityMd = findViewById(R.id.heartBoxActivityMd);
        // MaterialButtonGroup
        heartBoxActivityMbg = findViewById(R.id.heartBoxActivityMbg);
        // MaterialButton
        heartBoxActivityMbListen = findViewById(R.id.heartBoxActivityMbListen);
        heartBoxActivityMbRelease = findViewById(R.id.heartBoxActivityMbRelease);
        heartBoxActivityMbLook = findViewById(R.id.heartBoxActivityMbLook);
        heartBoxActivityMbPauseOrResume = findViewById(R.id.heartBoxActivityMbPauseOrResume);
        heartBoxActivityMbStartOrStop = findViewById(R.id.heartBoxActivityMbStartOrStop);
        heartBoxActivityMbCancel = findViewById(R.id.heartBoxActivityMbCancel);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 宽高值
        int widthAndHeightValue = ((ScreenUtils.screenWidth(this) - 36) / 2);
        // 设置视图布局参数
        LayoutParamsUtils.setViewLayoutParams(heartBoxActivityMcvEmotionWaveView, true, widthAndHeightValue, true, widthAndHeightValue);
        // 设置视图布局参数
        LayoutParamsUtils.setViewLayoutParams(heartBoxActivityMcvPaperCrushView, true, widthAndHeightValue, true, widthAndHeightValue);
        // 心盒页配套原件
        heartBoxActivityKit = new HeartBoxActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        heartBoxActivityMt.setNavigationOnClickListener(v -> finish());
        // MaterialButton
        ViewAntiBruteForceClickKit.setViewAntiBruteForceClickListener(heartBoxActivityMbPauseOrResume, 200, v -> heartBoxActivityKit.pauseOrResume(HeartBoxActivity.this));
        ViewAntiBruteForceClickKit.setViewAntiBruteForceClickListener(heartBoxActivityMbStartOrStop, 200, v -> heartBoxActivityKit.startOrStop(HeartBoxActivity.this, heartBoxActivityMbPauseOrResume));
        ViewAntiBruteForceClickKit.setViewAntiBruteForceClickListener(heartBoxActivityMbCancel, 200, v -> heartBoxActivityKit.cancel(HeartBoxActivity.this));
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 耗时操作导致跳转页面卡顿
        // 用 Handler.post(Runnable)
        // 于 onCreate() 末尾提交仍排在主线程消息队列后执行
        HandlerKit.getInstance().post(() -> heartBoxActivityKit.checkRecordAudioPermission(HeartBoxActivity.this, heartBoxActivityEwv, heartBoxActivityPcv, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook, heartBoxActivityMbPauseOrResume, heartBoxActivityMbStartOrStop));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        heartBoxActivityKit.executeOnActivityDestroy(heartBoxActivityMbStartOrStop);
    }
}