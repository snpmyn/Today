package com.zsp.today.module.heartbox.kit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;
import com.zsp.today.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import util.animation.AnimationManager;
import util.animation.BreatheAnimationKit;
import util.list.ListUtils;
import util.typeface.TypefaceUtils;
import util.view.ViewUtils;
import widget.emoji.MoodEmojiKit;
import widget.media.MediaFileDeleteWatchKit;
import widget.media.MediaFileDirectoryEnum;
import widget.media.MediaFileInfo;
import widget.media.MediaFileInfoHelper;
import widget.media.MediaFileKit;
import widget.media.MediaFileTypeEnum;
import widget.media.audio.AudioPlayKit;
import widget.media.audio.AudioPlaySliderKit;
import widget.media.audio.AudioRecordKit;
import widget.media.audio.DurationFormatTypeEnum;
import widget.permissionx.kit.PermissionKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.quote.ComfortQuoteLooper;
import widget.screen.kit.ScreenHandleKit;
import widget.screen.listener.ScreenHandleListener;
import widget.screenshot.ScreenshotKit;
import widget.view.EmotionWaveView;
import widget.view.LeafView;
import widget.view.PaperCrushView;

/**
 * Created on 2025/9/13.
 *
 * @author 郑少鹏
 * @desc 心盒页配套原件
 */
public class HeartBoxActivityKit {
    /**
     * 音频录制配套原件
     */
    private AudioRecordKit audioRecordKit;
    /**
     * 音频文件删除配套原件
     */
    private MediaFileDeleteWatchKit mediaFileDeleteWatchKit;
    /**
     * 文件名前缀
     */
    private final String fileNamePrefix = "心声 " + MoodEmojiKit.Mood.LOVE.getEmoji() + " ";
    /**
     * 媒体文件类型枚举
     */
    private final MediaFileTypeEnum mediaFileTypeEnum = MediaFileTypeEnum.MP3;
    /**
     * 媒体文件目录枚举
     */
    private final MediaFileDirectoryEnum mediaFileDirectoryEnum = MediaFileDirectoryEnum.MUSIC;
    /**
     * 名称
     */
    private String name;
    /**
     * 舒适短语循环器
     */
    private ComfortQuoteLooper comfortQuoteLooper;

    /**
     * 检查录制音频权限
     *
     * @param appCompatActivity               活动
     * @param emotionWaveView                 心情波视图
     * @param paperCrushView                  纸张粉碎视图
     * @param heartBoxActivityMcv             材料卡片视图
     * @param heartBoxActivityTvDurationSize  时长大小
     * @param heartBoxActivityTvTime          时间
     * @param heartBoxActivityTvDate          日期
     * @param heartBoxActivityMd              材料分割线
     * @param heartBoxActivityMbg             按钮组
     * @param heartBoxActivityMbListen        听听按钮
     * @param heartBoxActivityMbRelease       释放按钮
     * @param heartBoxActivityMbLook          看看按钮
     * @param heartBoxActivityMbPauseOrResume 暂停或恢复按钮
     * @param heartBoxActivityMbStartOrStop   开始或停止按钮
     */
    public void checkRecordAudioPermission(AppCompatActivity appCompatActivity, EmotionWaveView emotionWaveView, PaperCrushView paperCrushView, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook, MaterialButton heartBoxActivityMbPauseOrResume, MaterialButton heartBoxActivityMbStartOrStop) {
        PermissionxKit.execute(appCompatActivity, true, ListUtils.mergeLists(PermissionKit.recordAudio(), PermissionKit.readAudio()), R.string.recordReadAudioAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                watchFileDelete(appCompatActivity, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
                initAudioRecord(appCompatActivity, emotionWaveView, paperCrushView, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook, heartBoxActivityMbPauseOrResume, heartBoxActivityMbStartOrStop);
                prepareMediaFileInfo(appCompatActivity, null, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
            }

            @Override
            public void allGrantedContrary() {
                appCompatActivity.finish();
            }
        });
    }

    /**
     * 观察文件删除
     *
     * @param appCompatActivity              活动
     * @param heartBoxActivityMcv            材料卡片视图
     * @param heartBoxActivityTvDurationSize 时长大小
     * @param heartBoxActivityTvTime         时间
     * @param heartBoxActivityTvDate         日期
     * @param heartBoxActivityMd             材料分割线
     * @param heartBoxActivityMbg            按钮组
     * @param heartBoxActivityMbListen       听听按钮
     * @param heartBoxActivityMbRelease      释放按钮
     * @param heartBoxActivityMbLook         看看按钮
     */
    private void watchFileDelete(AppCompatActivity appCompatActivity, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook) {
        mediaFileDeleteWatchKit = new MediaFileDeleteWatchKit(appCompatActivity);
        mediaFileDeleteWatchKit.setMediaFileDeleteWatchListener((absolutePathOrUri, mediaStoreUri) -> {
            prepareMediaFileInfo(appCompatActivity, null, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
            screenshot(appCompatActivity, absolutePathOrUri);
        });
        mediaFileDeleteWatchKit.watchSingleDirectory(mediaFileDirectoryEnum, fileNamePrefix, mediaFileTypeEnum);
    }

    /**
     * 截屏
     *
     * @param appCompatActivity 活动
     * @param absolutePathOrUri 绝对路径或统一资源标识符
     */
    private void screenshot(AppCompatActivity appCompatActivity, String absolutePathOrUri) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(appCompatActivity).inflate(R.layout.heart_box_release, null);
        // 上提示
        TextView heartBoxReleaseTvTopHint = view.findViewById(R.id.heartBoxReleaseTvTopHint);
        heartBoxReleaseTvTopHint.setText(R.string.yourHeartFeelsLighter);
        heartBoxReleaseTvTopHint.setTypeface(TypefaceUtils.sansBold(appCompatActivity));
        // 日期时间
        TextView heartBoxReleaseTvDateTime = view.findViewById(R.id.heartBoxReleaseTvDateTime);
        String date = MediaFileInfoHelper.getMediaFileChineseDateTimeMembers(appCompatActivity, absolutePathOrUri)[0];
        String time = MediaFileInfoHelper.getMediaFileChineseDateTimeMembers(appCompatActivity, absolutePathOrUri)[1];
        heartBoxReleaseTvDateTime.setText(String.format(appCompatActivity.getString(R.string.formatSsWithLineBreak), date, time));
        // 下提示
        TextView heartBoxReleaseTvBottomHint = view.findViewById(R.id.heartBoxReleaseTvBottomHint);
        heartBoxReleaseTvBottomHint.setText(R.string.myHeartLittleColdAndSmile);
        heartBoxReleaseTvBottomHint.setTypeface(TypefaceUtils.sansBold(appCompatActivity));
        new ScreenshotKit.Builder(appCompatActivity).setContentView(view).setSizeByRatio(1.0F, 0.5F).setCancelOnTouchOutside(false).setAnimationDuration(200).show();
    }

    /**
     * 初始化音频录制
     *
     * @param appCompatActivity               活动
     * @param emotionWaveView                 心情波视图
     * @param paperCrushView                  纸张粉碎视图
     * @param heartBoxActivityMcv             材料卡片视图
     * @param heartBoxActivityTvDurationSize  时长大小
     * @param heartBoxActivityTvTime          时间
     * @param heartBoxActivityTvDate          日期
     * @param heartBoxActivityMd              材料分割线
     * @param heartBoxActivityMbg             按钮组
     * @param heartBoxActivityMbListen        听听按钮
     * @param heartBoxActivityMbRelease       释放按钮
     * @param heartBoxActivityMbLook          看看按钮
     * @param heartBoxActivityMbPauseOrResume 暂停或恢复按钮
     * @param heartBoxActivityMbStartOrStop   开始或停止按钮
     */
    private void initAudioRecord(AppCompatActivity appCompatActivity, EmotionWaveView emotionWaveView, PaperCrushView paperCrushView, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook, MaterialButton heartBoxActivityMbPauseOrResume, MaterialButton heartBoxActivityMbStartOrStop) {
        audioRecordKit = new AudioRecordKit();
        audioRecordKit.setAudioRecordListener(new AudioRecordKit.AudioRecordListener() {
            @Override
            public void onState(@NonNull AudioRecordKit.State state) {
                update(appCompatActivity, state, heartBoxActivityMbPauseOrResume, heartBoxActivityMbStartOrStop);
            }

            @Override
            public void onVolume(double db, double level, @Nullable byte[] pcm, int read) {
                emotionWaveView.setVolume((float) (level * 10));
                paperCrushView.setVolume((float) (level * 10));
            }

            @Override
            public void onStop(@NonNull MediaFileInfo mediaFileInfo) {
                prepareMediaFileInfo(appCompatActivity, mediaFileInfo, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
            }
        });
    }

    /**
     * 更新
     *
     * @param appCompatActivity               活动
     * @param state                           状态
     * @param heartBoxActivityMbPauseOrResume 暂停或恢复按钮
     * @param heartBoxActivityMbStartOrStop   开始或停止按钮
     */
    private void update(AppCompatActivity appCompatActivity, @NonNull AudioRecordKit.State state, @NonNull MaterialButton heartBoxActivityMbPauseOrResume, @NonNull MaterialButton heartBoxActivityMbStartOrStop) {
        // 开始或停止按钮状态切换
        heartBoxActivityMbStartOrStop.setChecked((state == AudioRecordKit.State.RECORDING) || (state == AudioRecordKit.State.PAUSED));
        // 暂停或恢复按钮状态切换
        heartBoxActivityMbPauseOrResume.setChecked(state == AudioRecordKit.State.PAUSED);
        // 切换动画
        // 切换文本
        // 暂停或恢复按钮禁止可选
        switch (state) {
            case STOPPED:
            case CANCELED:
                BreatheAnimationKit.stop(heartBoxActivityMbStartOrStop, 300);
                heartBoxActivityMbPauseOrResume.setText(appCompatActivity.getString(R.string.pause));
                heartBoxActivityMbPauseOrResume.setCheckable(false);
                break;
            case RECORDING:
                BreatheAnimationKit.start(heartBoxActivityMbStartOrStop, 800, 800);
                heartBoxActivityMbPauseOrResume.setText(appCompatActivity.getString(R.string.pause));
                break;
            case PAUSED:
                BreatheAnimationKit.stop(heartBoxActivityMbStartOrStop, 300);
                heartBoxActivityMbPauseOrResume.setText(appCompatActivity.getString(R.string.resume));
                break;
        }
    }

    /**
     * 暂停或恢复
     *
     * @param context 上下文
     */
    public void pauseOrResume(Context context) {
        if (audioRecordKit.canPauseRecording()) {
            audioRecordKit.pauseRecording(context);
        } else if (audioRecordKit.canResumeRecording()) {
            audioRecordKit.resumeRecording(context);
        }
    }

    /**
     * 开始或结束
     *
     * @param appCompatActivity               活动
     * @param heartBoxActivityMbPauseOrResume 暂停或恢复按钮
     */
    public void startOrStop(AppCompatActivity appCompatActivity, MaterialButton heartBoxActivityMbPauseOrResume) {
        if (audioRecordKit.canStartRecording()) {
            heartBoxActivityMbPauseOrResume.setCheckable(true);
            audioRecordKit.startRecording(appCompatActivity, fileNamePrefix, mediaFileTypeEnum);
        } else if (audioRecordKit.canStopRecording()) {
            audioRecordKit.stopRecording(appCompatActivity, mediaFileTypeEnum, true, mediaFileDirectoryEnum, true);
        }
    }

    /**
     * 取消
     *
     * @param appCompatActivity 活动
     */
    public void cancel(AppCompatActivity appCompatActivity) {
        if (audioRecordKit.canCancelRecording()) {
            audioRecordKit.cancelRecording(appCompatActivity);
        }
    }

    /**
     * 准备媒体文件信息
     * <p>
     * 停止录制场景
     * 其它场景
     *
     * @param appCompatActivity              活动
     * @param mediaFileInfo                  媒体文件信息
     * @param heartBoxActivityMcv            材料卡片视图
     * @param heartBoxActivityTvDurationSize 时长大小
     * @param heartBoxActivityTvTime         时间
     * @param heartBoxActivityTvDate         日期
     * @param heartBoxActivityMd             材料分割线
     * @param heartBoxActivityMbg            按钮组
     * @param heartBoxActivityMbListen       听听按钮
     * @param heartBoxActivityMbRelease      释放按钮
     * @param heartBoxActivityMbLook         看看按钮
     */
    public void prepareMediaFileInfo(AppCompatActivity appCompatActivity, MediaFileInfo mediaFileInfo, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook) {
        // 动画过渡
        ViewUtils.showView(heartBoxActivityMcv);
        AnimationManager.alphaShow(heartBoxActivityMcv, 1000, null);
        // 临时媒体文件信息
        AtomicReference<MediaFileInfo> mediaFileInfoTemporary = new AtomicReference<>(mediaFileInfo);
        if (null != mediaFileInfo) {
            // 停止录制场景
            mediaFileInfoTemporary.set(mediaFileInfo);
            showDetail(appCompatActivity, mediaFileInfoTemporary.get(), heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
            return;
        }
        // 其它场景
        MediaFileKit.getFileAsync(appCompatActivity, null, mediaFileTypeEnum, mediaFileDirectoryEnum, false, MediaFileKit.FileReturnModeEnum.LATEST, 0, new MediaFileKit.OnDurationLoadListener() {
            @Override
            public void onSingleDurationLoad(@NonNull MediaFileInfo mediaFileInfo, int position) {

            }

            @Override
            public void onAllDurationLoad(@NonNull List<MediaFileInfo> mediaFileInfos) {
                mediaFileInfoTemporary.set(ListUtils.listIsEmpty(mediaFileInfos) ? null : mediaFileInfos.get(0));
                showDetail(appCompatActivity, mediaFileInfoTemporary.get(), heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
            }
        });
    }

    /**
     * 显示详情
     *
     * @param appCompatActivity              活动
     * @param mediaFileInfo                  媒体文件信息
     * @param heartBoxActivityMcv            材料卡片视图
     * @param heartBoxActivityTvDurationSize 时长大小
     * @param heartBoxActivityTvTime         时间
     * @param heartBoxActivityTvDate         日期
     * @param heartBoxActivityMd             材料分割线
     * @param heartBoxActivityMbg            按钮组
     * @param heartBoxActivityMbListen       听听按钮
     * @param heartBoxActivityMbRelease      释放按钮
     * @param heartBoxActivityMbLook         看看按钮
     */
    private void showDetail(AppCompatActivity appCompatActivity, MediaFileInfo mediaFileInfo, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook) {
        // 可下一步
        boolean canNext;
        // 临时统一资源标识符
        Uri uriTemporary;
        if (null == mediaFileInfo) {
            canNext = false;
            uriTemporary = null;
            name = null;
        } else {
            canNext = true;
            uriTemporary = mediaFileInfo.getUri();
            name = mediaFileInfo.getName();
        }
        if (null == comfortQuoteLooper) {
            comfortQuoteLooper = new ComfortQuoteLooper(ComfortQuoteLooper.Mode.THREE_LINES, 3000, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate);
        }
        if (canNext) {
            ViewUtils.hideView(heartBoxActivityMd, View.GONE);
            ViewUtils.showView(heartBoxActivityMbg);
            comfortQuoteLooper.stop();
            // 时长大小
            String durationSize = String.format("%1$s %2$s\u3000\u3000\u3000\u3000\u3000\u3000%3$s %4$s", MoodEmojiKit.Mood.HOURGLASS.getEmoji(), MediaFileInfoHelper.getMediaFileDuration(appCompatActivity, uriTemporary, null, DurationFormatTypeEnum.CHINESE), MoodEmojiKit.Mood.FILE_SIZE.getEmoji(), MediaFileInfoHelper.getMediaFileSize(mediaFileInfo.getSize()));
            heartBoxActivityTvDurationSize.setText(durationSize);
            heartBoxActivityTvDurationSize.setGravity(Gravity.START);
            // 时间
            String time = String.format(appCompatActivity.getString(R.string.formatSsWithSpace), MoodEmojiKit.Mood.SPIRAL_CALENDAR.getEmoji(), MediaFileInfoHelper.getMediaFileChineseDateTimeMembers(appCompatActivity, mediaFileInfo.getUri())[1]);
            heartBoxActivityTvTime.setText(time);
            heartBoxActivityTvTime.setGravity(Gravity.START);
            // 日期
            String date = String.format(appCompatActivity.getString(R.string.formatSsWithSpace), MoodEmojiKit.Mood.SPIRAL_CALENDAR.getEmoji(), MediaFileInfoHelper.getMediaFileChineseDateTimeMembers(appCompatActivity, mediaFileInfo.getUri())[0]);
            heartBoxActivityTvDate.setText(date);
            heartBoxActivityTvDate.setGravity(Gravity.START);
        } else {
            ViewUtils.hideView(heartBoxActivityMbg, View.GONE);
            ViewUtils.showView(heartBoxActivityMd);
            heartBoxActivityTvDurationSize.setGravity(Gravity.CENTER);
            heartBoxActivityTvTime.setGravity(Gravity.CENTER);
            heartBoxActivityTvDate.setGravity(Gravity.CENTER);
            comfortQuoteLooper.start();
        }
        // 听听
        heartBoxActivityMbListen.setOnClickListener(v -> listen(appCompatActivity, uriTemporary));
        // 释放
        heartBoxActivityMbRelease.setOnClickListener(v -> release(appCompatActivity, uriTemporary));
        // 看看
        heartBoxActivityMbLook.setOnClickListener(v -> look(appCompatActivity, heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook));
    }

    /**
     * 听听
     *
     * @param appCompatActivity 活动
     * @param uri               统一资源标识符
     */
    private void listen(AppCompatActivity appCompatActivity, Uri uri) {
        AudioPlaySliderKit audioPLaySliderKit = getAudioPLaySliderKit(appCompatActivity, uri);
        AudioPlayKit.setOnProgressListener(audioPLaySliderKit::update);
        audioPLaySliderKit.show(new AudioPlaySliderKit.AudioPLaySliderKitStartAndDismissListener() {
            @Override
            public void start() {
                AudioPlayKit.playWithUri(appCompatActivity, uri, 0);
            }

            @Override
            public void dismiss() {
                AudioPlayKit.stop();
            }
        });
    }

    /**
     * 获取音频播放滑块配套原件
     *
     * @param appCompatActivity 活动
     * @param uri               统一资源标识符
     * @return 音频播放滑块配套原件
     */
    @NonNull
    private AudioPlaySliderKit getAudioPLaySliderKit(AppCompatActivity appCompatActivity, Uri uri) {
        AudioPlaySliderKit audioPLaySliderKit = new AudioPlaySliderKit(appCompatActivity);
        audioPLaySliderKit.prepare(new AudioPlaySliderKit.AudioPLaySliderKitHandleListener() {
            @Override
            public void onStopTrackingTouch(float value) {
                if (AudioPlayKit.arePlayComplete()) {
                    AudioPlayKit.playWithUri(appCompatActivity, uri, (int) value);
                } else {
                    AudioPlayKit.seekTo((int) value);
                }
            }

            @Override
            public void canPlay(boolean canPlay) {
                if (canPlay) {
                    AudioPlayKit.resume();
                } else {
                    AudioPlayKit.pause();
                }
            }

            @Override
            public void playAgain() {
                AudioPlayKit.playWithUri(appCompatActivity, uri, 0);
            }
        });
        return audioPLaySliderKit;
    }

    /**
     * 释放
     *
     * @param appCompatActivity 活动
     * @param uri               统一资源标识符
     */
    private void release(AppCompatActivity appCompatActivity, Uri uri) {
        LeafView.startLeafAnimation(appCompatActivity, 100, 200, 3000, 6000, 25.0F, 8000, 0.0F, 0.005F, 1000, 1000, () -> MediaFileKit.delete(appCompatActivity, uri, (absolutePathOrUri, mediaStoreUri) -> {
        }), () -> AudioPlayKit.play(appCompatActivity, "audio/release.wav", null, 0));
    }

    /**
     * 看看
     *
     * @param appCompatActivity              活动
     * @param heartBoxActivityMcv            材料卡片视图
     * @param heartBoxActivityTvDurationSize 时长大小
     * @param heartBoxActivityTvTime         时间
     * @param heartBoxActivityTvDate         日期
     * @param heartBoxActivityMd             材料分割线
     * @param heartBoxActivityMbg            按钮组
     * @param heartBoxActivityMbListen       听听按钮
     * @param heartBoxActivityMbRelease      释放按钮
     * @param heartBoxActivityMbLook         看看按钮
     */
    private void look(AppCompatActivity appCompatActivity, MaterialCardView heartBoxActivityMcv, TextView heartBoxActivityTvDurationSize, TextView heartBoxActivityTvTime, TextView heartBoxActivityTvDate, MaterialDivider heartBoxActivityMd, MaterialButtonGroup heartBoxActivityMbg, MaterialButton heartBoxActivityMbListen, MaterialButton heartBoxActivityMbRelease, MaterialButton heartBoxActivityMbLook) {
        MediaFileKit.getFileAsync(appCompatActivity, null, mediaFileTypeEnum, mediaFileDirectoryEnum, false, MediaFileKit.FileReturnModeEnum.ALL, 0, new MediaFileKit.OnDurationLoadListener() {
            @Override
            public void onSingleDurationLoad(@NonNull MediaFileInfo mediaFileInfo, int position) {

            }

            @Override
            public void onAllDurationLoad(@NonNull List<MediaFileInfo> mediaFileInfos) {
                if (ListUtils.listIsEmpty(mediaFileInfos)) {
                    return;
                }
                List<String> stringList = new ArrayList<>(mediaFileInfos.size());
                Map<String, MediaFileInfo> mediaFileInfoMap = new HashMap<>(mediaFileInfos.size());
                for (MediaFileInfo mediaFileInfo : mediaFileInfos) {
                    String name = mediaFileInfo.getName();
                    stringList.add(name);
                    mediaFileInfoMap.put(name, mediaFileInfo);
                }
                Collections.reverse(stringList);
                // 临时条件
                final String[] conditionTemporary = new String[1];
                // ScreenHandleKit
                ScreenHandleKit screenHandleKit = new ScreenHandleKit(appCompatActivity);
                // 打包集合条件
                screenHandleKit.packListConditions(appCompatActivity.getString(R.string.voice), 1, true, stringList);
                // 默选
                screenHandleKit.defaultSelect(appCompatActivity.getString(R.string.voice), name);
                // 关联
                screenHandleKit.associate();
                // 设筛选操作监听
                screenHandleKit.setScreenHandleListener(new ScreenHandleListener() {
                    @Override
                    public void click(View view, String classification, String condition, boolean selected) {
                        conditionTemporary[0] = condition;
                    }

                    @Override
                    public void reset() {
                        screenHandleKit.reset();
                    }

                    @Override
                    public void ensure() {
                        screenHandleKit.dismiss();
                        prepareMediaFileInfo(appCompatActivity, mediaFileInfoMap.get(conditionTemporary[0]), heartBoxActivityMcv, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate, heartBoxActivityMd, heartBoxActivityMbg, heartBoxActivityMbListen, heartBoxActivityMbRelease, heartBoxActivityMbLook);
                    }
                });
                // 显示
                screenHandleKit.show();
            }
        });
    }

    /**
     * 活动销毁时执行
     *
     * @param heartBoxActivityMbStartOrStop 开始或停止按钮
     */
    public void executeOnActivityDestroy(MaterialButton heartBoxActivityMbStartOrStop) {
        // 呼吸动画配套原件
        BreatheAnimationKit.stop(heartBoxActivityMbStartOrStop, 0);
        // 音频录制配套原件
        if (null != audioRecordKit) {
            audioRecordKit.releaseResources();
        }
        // 音频播放配套原件
        AudioPlayKit.release();
        // 音频文件删除配套原件
        if (null != mediaFileDeleteWatchKit) {
            mediaFileDeleteWatchKit.stopWatching();
        }
        // 舒适短语循环器
        if (null != comfortQuoteLooper) {
            comfortQuoteLooper.stop();
            comfortQuoteLooper = null;
        }
    }
}