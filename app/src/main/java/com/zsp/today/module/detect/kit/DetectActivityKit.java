package com.zsp.today.module.detect.kit;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.zsp.today.R;

import util.view.ViewUtils;
import widget.sensor.metal.MetalDetectKit;

/**
 * Created on 2025/10/16.
 *
 * @author 郑少鹏
 * @desc 探测页配套原件
 */
public class DetectActivityKit {
    /**
     * 金属探测配套原件
     */
    private MetalDetectKit metalDetectKit;
    /**
     * 探测中否
     */
    private boolean areDetecting = false;

    /**
     * 执行
     *
     * @param appCompatActivity          活动
     * @param detectActivityLl           LinearLayout
     * @param detectActivityMcvResult    MaterialCardView
     * @param detectActivityTvResult     TextView
     * @param detectActivityLavDetect    LottieAnimationView
     * @param detectActivityMbtg         MaterialButtonToggleGroup
     * @param detectActivityTvIllustrate TextView
     */
    public void execute(AppCompatActivity appCompatActivity, LinearLayout detectActivityLl, MaterialCardView detectActivityMcvResult, TextView detectActivityTvResult, LottieAnimationView detectActivityLavDetect, @NonNull MaterialButtonToggleGroup detectActivityMbtg, @NonNull TextView detectActivityTvIllustrate) {
        // 设置说明
        detectActivityTvIllustrate.setText(R.string.detectIllustrate);
        // 金属探测配套原件
        metalDetectKit = new MetalDetectKit(appCompatActivity, new MetalDetectKit.MetalDetectListener() {
            @Override
            public void onMagneticFieldChange(float magnitude) {
                if (!areDetecting) {
                    return;
                }
                ViewUtils.hideView(detectActivityMcvResult, View.GONE);
                ViewUtils.showView(detectActivityLavDetect);
            }

            @Override
            public void onMetalDetect() {
                if (!areDetecting) {
                    return;
                }
                ViewUtils.hideView(detectActivityLavDetect, View.GONE);
                detectActivityTvResult.setText(R.string.metalDetected);
                ViewUtils.showView(detectActivityMcvResult);
            }
        }, detectActivityLl);
        metalDetectKit.setThreshold(72.0F);
        metalDetectKit.setDeltaThreshold(16.0F);
        // 按钮选中监听
        detectActivityMbtg.addOnButtonCheckedListener((materialButtonToggleGroup, i, b) -> {
            if (!b) {
                return;
            }
            if (i == R.id.detectActivityMbStart) {
                startDetect(detectActivityMcvResult, detectActivityLavDetect);
            } else if (i == R.id.detectActivityMbStop) {
                stopDetect(detectActivityMcvResult, detectActivityLavDetect);
            }
        });
    }

    /**
     * 开始探测
     *
     * @param detectActivityMcvResult MaterialCardView
     * @param detectActivityLavDetect LottieAnimationView
     */
    public void startDetect(MaterialCardView detectActivityMcvResult, LottieAnimationView detectActivityLavDetect) {
        if (areDetecting) {
            return;
        }
        areDetecting = true;
        ViewUtils.hideView(detectActivityMcvResult, View.GONE);
        ViewUtils.showView(detectActivityLavDetect);
        detectActivityLavDetect.playAnimation();
        metalDetectKit.start();
    }

    /**
     * 停止探测
     *
     * @param detectActivityMcvResult MaterialCardView
     * @param detectActivityLavDetect LottieAnimationView
     */
    public void stopDetect(MaterialCardView detectActivityMcvResult, LottieAnimationView detectActivityLavDetect) {
        if (!areDetecting) {
            return;
        }
        areDetecting = false;
        ViewUtils.hideView(detectActivityMcvResult, View.GONE);
        ViewUtils.showView(detectActivityLavDetect);
        detectActivityLavDetect.pauseAnimation();
        metalDetectKit.stop();
    }
}