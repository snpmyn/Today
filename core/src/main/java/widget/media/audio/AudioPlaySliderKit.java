package widget.media.audio;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.zsp.core.R;

import util.layoutparams.LayoutParamsUtils;
import util.screen.ScreenUtils;
import widget.dialog.materialalertdialog.MaterialAlertDialogBuilderKit;
import widget.picture.PictureRandomPicker;

/**
 * Created on 2025/9/22.
 *
 * @author 郑少鹏
 * @desc 音频播放滑块配套原件
 */
public class AudioPlaySliderKit {
    private final AppCompatActivity appCompatActivity;
    private View view;
    /**
     * 是否播放完成
     */
    private boolean playComplete;
    /**
     * 是否正在播放
     * <p>
     * 初始正在播放
     */
    private boolean arePlaying = true;
    private Slider audioPlaySliderSlider;
    private MaterialButton audioPlaySliderMb;

    /**
     * constructor
     *
     * @param appCompatActivity 活动
     */
    public AudioPlaySliderKit(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * 准备
     *
     * @param audioPLaySliderKitHandleListener 音频播放滑块配套原件处理监听
     */
    @SuppressLint("InflateParams")
    public void prepare(AudioPLaySliderKitHandleListener audioPLaySliderKitHandleListener) {
        // 视图
        view = LayoutInflater.from(appCompatActivity).inflate(R.layout.audio_play_slider, null);
        // ShapeableImageView
        ShapeableImageView audioPlaySliderSiv = view.findViewById(R.id.audioPlaySliderSiv);
        audioPlaySliderSiv.setImageResource(PictureRandomPicker.getRandomPicture());
        // Slider
        audioPlaySliderSlider = view.findViewById(R.id.audioPlaySliderSlider);
        audioPlaySliderSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // 开始追踪触摸
                // 场景一 - 此时音频仍在播放
                // 场景二 - 此时音频播放完成
                // 暂停 Slider 更新
                arePlaying = false;
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // 停止追踪触摸
                // 允许 Slider 更新
                arePlaying = true;
                float value = slider.getValue();
                // 场景三 - 此时音频播放完成 + 滑动后值等于音频总进度
                if (playComplete && (value == slider.getValueTo())) {
                    // 不触发回调
                    // 可点击按钮播放
                    return;
                }
                // 场景一 - 此时音频仍在播放
                // 场景二 - 此时音频播放完成 + 滑动后值不等音频总进度
                // 触发回调
                // 设置按钮图标
                if (null != audioPLaySliderKitHandleListener) {
                    audioPLaySliderKitHandleListener.onStopTrackingTouch(value);
                }
                audioPlaySliderMb.setIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.ic_pause_cos_24dp));
            }
        });
        // MaterialButton
        audioPlaySliderMb = view.findViewById(R.id.audioPlaySliderMb);
        audioPlaySliderMb.setOnClickListener(v -> {
            if (!playComplete) {
                // 未播放完成场景
                // 1. 重设是否正在播放标志
                arePlaying = !arePlaying;
                // 2. 禁止追踪触摸
                audioPlaySliderSlider.setEnabled(arePlaying);
                if (null != audioPLaySliderKitHandleListener) {
                    // 3. 回调是否可以播放
                    audioPLaySliderKitHandleListener.canPlay(arePlaying);
                }
                // 4. 设置按钮图标
                audioPlaySliderMb.setIcon(ContextCompat.getDrawable(appCompatActivity, arePlaying ? R.drawable.ic_pause_cos_24dp : R.drawable.ic_artist_cos_24dp));
                return;
            }
            if (null != audioPLaySliderKitHandleListener) {
                // 播放完成场景
                // 1. 允许追踪触摸
                audioPlaySliderSlider.setEnabled(true);
                // 2. 回调再次播放
                audioPLaySliderKitHandleListener.playAgain();
                // 3. 设置按钮图标
                audioPlaySliderMb.setIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.ic_pause_cos_24dp));
            }
        });
    }

    /**
     * 显示
     *
     * @param audioPLaySliderKitStartAndDismissListener 音频播放滑块配套原件开始和消失监听
     */
    public void show(AudioPLaySliderKitStartAndDismissListener audioPLaySliderKitStartAndDismissListener) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity).setView(view).setCancelable(true).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        // 设置视图布局参数
        // 当 AlertDialog 显示后
        // 这时 view 才被添加到内部 FrameLayout 中
        // view.getParent() 才不为空
        // 修改 LayoutParams 才会生效
        LayoutParamsUtils.setViewLayoutParams((View) view.getParent(), false, 0, true, (int) (ScreenUtils.screenHeight(appCompatActivity) * 0.7));
        if (null != audioPLaySliderKitStartAndDismissListener) {
            // 开始
            audioPLaySliderKitStartAndDismissListener.start();
        }
        alertDialog.setOnDismissListener(dialog -> {
            if (null != audioPLaySliderKitStartAndDismissListener) {
                // 消失
                audioPLaySliderKitStartAndDismissListener.dismiss();
                audioPlaySliderMb.setIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.ic_artist_cos_24dp));
            }
        });
    }

    /**
     * 更新
     *
     * @param currentMs 当前进度毫秒
     * @param totalMs   总进度毫秒
     */
    public void update(int currentMs, int totalMs) {
        if (arePlaying) {
            // 正在播放
            audioPlaySliderSlider.setValueTo(totalMs);
            audioPlaySliderSlider.setValue(currentMs);
        }
        // 每次更新进度后判断是否播放完成
        playComplete = (currentMs == totalMs);
        if (playComplete) {
            // 播放完成
            audioPlaySliderMb.setIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.ic_artist_cos_24dp));
        }
    }

    /**
     * 音频播放滑块配套原件处理监听
     */
    public interface AudioPLaySliderKitHandleListener {
        /**
         * 停止追踪触摸
         *
         * @param value 值
         */
        void onStopTrackingTouch(float value);

        /**
         * 是否可以播放
         *
         * @param canPlay 可以播放否
         */
        void canPlay(boolean canPlay);

        /**
         * 再次播放
         */
        void playAgain();
    }

    /**
     * 音频播放滑块配套原件开始和消失监听
     */
    public interface AudioPLaySliderKitStartAndDismissListener {
        /**
         * 开始
         */
        void start();

        /**
         * 消失
         */
        void dismiss();
    }
}