package widget.media.audio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zsp.core.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;
import util.datetime.CurrentTimeMillisClock;
import widget.media.MediaFileDirectoryEnum;
import widget.media.MediaFileInfo;
import widget.media.MediaFileInfoHelper;
import widget.media.MediaFileTypeEnum;
import widget.toast.ToastKit;

/**
 * @decs: 音频录制配套原件
 * @author: 郑少鹏
 * @date: 2025/9/21 22:30
 * @version: v 1.0
 * <p>
 * 必定 - 内部目录存储原始音频文件
 * <p>
 * 可选 - 复制原始音频文件到内部目录
 * 可选 - 复制原始音频文件到外部私有目录
 * 可选 - 复制原始音频文件到外部公共目录
 * {@link AudioRecordKit#copyToDirectory(Context, File, MediaFileTypeEnum, MediaFileDirectoryEnum, String, boolean, long)}
 * <p>
 * 可选 - 查询内部目录音频文件
 * 可选 - 查询外部私有目录音频文件
 * 可选 - 查询外部公共目录音频文件
 * {@link AudioRecordKit#getRecordAudioList(Context, String, MediaFileTypeEnum, MediaFileDirectoryEnum)}
 */
public class AudioRecordKit {
    /**
     * 状态
     */
    public enum State {
        /**
         * 空闲
         */
        IDLE,
        /**
         * 录制中
         */
        RECORDING,
        /**
         * 暂停
         */
        PAUSED,
        /**
         * 停止
         */
        STOPPED,
        /**
         * 取消
         */
        CANCELED
    }

    private MediaRecorder mediaRecorder;
    private AudioRecord audioRecord;
    private HandlerThread volumeHandlerThread;
    private Handler volumeHandler;
    private volatile boolean areRecording = false;
    private volatile boolean arePaused = false;
    private File outputFile;
    private String outputFilePrefix = "record-";
    private State currentState = State.IDLE;
    private AudioRecordListener audioRecordListener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    /**
     * 音量更新间隔
     * <p>
     * 单位毫秒
     * 值越小更新越频繁
     */
    private long volumeUpdateInterval = 50;

    /**
     * constructor
     * <p>
     * 初始化状态为 IDLE
     */
    public AudioRecordKit() {
        notifyState(currentState);
    }

    /**
     * 设置音量更新间隔
     *
     * @param intervalMillis 间隔毫秒
     *                       值越小更新越频繁
     */
    public void setVolumeUpdateInterval(long intervalMillis) {
        if (intervalMillis < 10) {
            intervalMillis = 10;
        }
        this.volumeUpdateInterval = intervalMillis;
    }

    /**
     * 开始录制
     * <p>
     * {@link MediaRecorder#setAudioSource(int)}
     * {@link MediaRecorder#setOutputFormat(int)} (int)}
     * {@link MediaRecorder#setAudioEncoder(int)} (int)}
     * 上面三个方法如果顺序错误，例如先设置音频编码器再设置输出格式，prepare() 会抛异常。
     * 不同 Android 版本对顺序要求可能略有差异
     * 但官方文档明确要求设置音源 → 输出格式 → 编码器 → 其他参数 → prepare → start
     *
     * @param context           上下文
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     */
    public void startRecording(@NonNull final Context context, @Nullable final String fileNamePrefix, MediaFileTypeEnum mediaFileTypeEnum) {
        executorService.execute(() -> {
            if (!canStartRecording()) {
                postToast(context.getString(R.string.canNotStartRecording));
                return;
            }
            final String prefix = TextUtils.isEmpty(fileNamePrefix) ? "record-" : (fileNamePrefix + "-");
            outputFilePrefix = prefix;
            // 获取唯一文件名
            final String fileName = getUniqueFileName(context.getFilesDir(), prefix, mediaFileTypeEnum);
            try {
                outputFile = new File(context.getFilesDir(), fileName);
                mediaRecorder = new MediaRecorder();
                // 设置音频源
                // 定义录制输入来源
                // 必须在设置输出格式和设置音频编码器前调用
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置输出格式
                // 定义输出文件的容器格式
                // 必须在设置音频编码器前调用
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                // 设置音频编码器
                // 定义音频数据压缩方式
                // 必须在 prepare() 前调用
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                // 设置采样率
                // 可选
                // 但必须在 prepare() 前调用
                mediaRecorder.setAudioSamplingRate(44100);
                // 设置输出文件
                // 可选
                // 但必须在 prepare() 前调用
                mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
                // 准备录制
                mediaRecorder.prepare();
                // 开始录制
                mediaRecorder.start();
                areRecording = true;
                arePaused = false;
                startVolumeMonitoring(context);
                setStateOnMainThread(State.RECORDING);
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    /**
     * 获取唯一文件名
     *
     * @param dir               目录
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     * @return 唯一文件名
     */
    private String getUniqueFileName(@NonNull File dir, @NonNull String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        String suffix = mediaFileTypeEnum.getSuffix();
        long now = CurrentTimeMillisClock.getInstance().now();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMdd-HH-mm-ss", Locale.CHINA);
        String name = (fileNamePrefix + simpleDateFormat.format(new Date(now)) + suffix);
        int index = 1;
        while (new File(dir, name).exists()) {
            name = (fileNamePrefix + "(" + index + ")" + simpleDateFormat.format(new Date(now)) + suffix);
            index++;
        }
        return name;
    }

    /**
     * 暂停录制
     * <p>
     * 仅 RECORDING 状态下有效
     *
     * @param context 上下文
     */
    public void pauseRecording(Context context) {
        setStateOnMainThread(State.PAUSED);
        executorService.execute(() -> {
            if (!canPauseRecording()) {
                postToast(context.getString(R.string.canNotPauseRecording));
                return;
            }
            try {
                if (null != mediaRecorder) {
                    mediaRecorder.pause();
                    arePaused = true;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    /**
     * 恢复录制
     * <p>
     * 仅 PAUSED 状态下有效
     *
     * @param context 上下文
     */
    public void resumeRecording(Context context) {
        setStateOnMainThread(State.RECORDING);
        executorService.execute(() -> {
            if (!canResumeRecording()) {
                postToast(context.getString(R.string.canNotResumeRecording));
                return;
            }
            try {
                if (null != mediaRecorder) {
                    mediaRecorder.resume();
                    arePaused = false;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    /**
     * 停止录制
     *
     * @param context                                上下文
     * @param mediaFileTypeEnum                      媒体文件类型枚举
     * @param copyToDirectory                        复制到目录否
     * @param mediaFileDirectoryEnum                 媒体文件目录枚举
     * @param deleteOriginalFileAfterCopyToDirectory 复制到目录后删除源文件否
     */
    public void stopRecording(@NonNull final Context context, MediaFileTypeEnum mediaFileTypeEnum, boolean copyToDirectory, MediaFileDirectoryEnum mediaFileDirectoryEnum, boolean deleteOriginalFileAfterCopyToDirectory) {
        setStateOnMainThread(State.STOPPED);
        executorService.execute(() -> {
            File fileToUse = outputFile;
            Uri resultUri = null;
            long fileLastModified = 0L;
            try {
                if (null != mediaRecorder) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                areRecording = false;
                stopVolumeMonitoring();
                if ((null != fileToUse) && fileToUse.exists()) {
                    fileLastModified = fileToUse.lastModified();
                    // ⚡ 这里用 lastModified 生成最终文件名
                    // 重命名一次
                    String finalName = getFileNameByLastModified(fileLastModified, outputFilePrefix, mediaFileTypeEnum);
                    File finalFile = new File(fileToUse.getParent(), finalName);
                    if (!fileToUse.equals(finalFile)) {
                        if (fileToUse.renameTo(finalFile)) {
                            fileToUse = finalFile;
                        }
                    }
                    resultUri = Uri.fromFile(fileToUse);
                }
                if (copyToDirectory && (null != fileToUse) && fileToUse.exists()) {
                    Uri copiedUri = copyToDirectory(context, fileToUse, mediaFileTypeEnum, mediaFileDirectoryEnum, outputFilePrefix, deleteOriginalFileAfterCopyToDirectory, fileLastModified);
                    if (null != copiedUri) {
                        resultUri = copiedUri;
                        if ("file".equals(copiedUri.getScheme())) {
                            fileToUse = new File(Objects.requireNonNull(copiedUri.getPath()));
                        } else {
                            fileToUse = null;
                        }
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            final MediaFileInfo mediaFileInfo = MediaFileInfoHelper.getMediaFileInfo(context, (resultUri != null) ? resultUri : Objects.requireNonNull(fileToUse));
            mainHandler.post(() -> {
                if (null != audioRecordListener) {
                    audioRecordListener.onStop(mediaFileInfo);
                }
            });
        });
    }

    /**
     * 复制到目录
     *
     * @param context                                上下文
     * @param sourceFile                             资源文件
     * @param mediaFileTypeEnum                      媒体文件类型枚举
     * @param mediaFileDirectoryEnum                 媒体文件目录枚举
     * @param fileNamePrefix                         文件名前缀
     * @param deleteOriginalFileAfterCopyToDirectory 复制到目录后删除源文件否
     * @param sourceFileLastModified                 资源文件最后修改时间
     * @return 复制到目录后的文件统一资源标识符
     */
    @Nullable
    public static Uri copyToDirectory(@NonNull Context context, @NonNull File sourceFile, @NonNull MediaFileTypeEnum mediaFileTypeEnum, @NonNull MediaFileDirectoryEnum mediaFileDirectoryEnum, @NonNull String fileNamePrefix, boolean deleteOriginalFileAfterCopyToDirectory, long sourceFileLastModified) {
        if (!sourceFile.exists()) {
            return null;
        }
        Uri targetUri;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            // ⚡ 这里用 lastModified 生成文件名
            String fileName = getFileNameByLastModified(sourceFileLastModified, fileNamePrefix, mediaFileTypeEnum);
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mediaFileTypeEnum.getMimeType());
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, mediaFileDirectoryEnum.getRelativePath());
            targetUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (null != targetUri) {
                try (OutputStream outputStream = contentResolver.openOutputStream(targetUri); FileInputStream fis = new FileInputStream(sourceFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        Objects.requireNonNull(outputStream).write(buffer, 0, len);
                    }
                }
                if (deleteOriginalFileAfterCopyToDirectory) {
                    if (sourceFile.delete()) {
                        Timber.w("delete source file: %s", sourceFile.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            targetUri = null;
        }
        return targetUri;
    }

    /**
     * 通过最后修改时间获取文件名
     *
     * @param lastModified      最后修改时间
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     * @return 文件名
     */
    @NonNull
    private static String getFileNameByLastModified(long lastModified, @NonNull String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        String suffix = mediaFileTypeEnum.getSuffix();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMdd-HH-mm-ss", Locale.CHINA);
        return (fileNamePrefix + simpleDateFormat.format(new Date(lastModified)) + suffix);
    }

    /**
     * 取消录制
     * <p>
     * 删除当前文件
     * 设状态为 CANCELED
     *
     * @param context 上下文
     */
    public void cancelRecording(Context context) {
        setStateOnMainThread(State.CANCELED);
        executorService.execute(() -> {
            try {
                if (null != mediaRecorder) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                areRecording = false;
                stopVolumeMonitoring();
                if ((null != outputFile) && outputFile.exists() && !outputFile.delete()) {
                    postToast(context.getString(R.string.deleteAudioFail));
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    /**
     * 开始音量监听
     *
     * @param context 上下文
     */
    @SuppressLint("MissingPermission")
    private void startVolumeMonitoring(@NonNull final Context context) {
        int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize <= 0) {
            bufferSize = 2048;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            postToast(context.getString(R.string.volumeMonitoringIsUnavailable));
            return;
        }
        audioRecord.startRecording();
        volumeHandlerThread = new HandlerThread("VolumeMonitor");
        volumeHandlerThread.start();
        volumeHandler = new Handler(volumeHandlerThread.getLooper());
        volumeHandler.post(getVolumeRunnable(bufferSize));
    }

    @NonNull
    private Runnable getVolumeRunnable(int bufferSize) {
        return new Runnable() {
            @Override
            public void run() {
                if (!areRecording) {
                    notifyVolume(0, 0, null, 0);
                    return;
                }
                double db, level;
                if (!arePaused && (null != audioRecord)) {
                    byte[] buffer = new byte[bufferSize];
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    double sum = 0;
                    for (int i = 0; i < (read / 2); i++) {
                        short sample = (short) ((buffer[i * 2] & 0xff) | (buffer[i * 2 + 1] << 8));
                        sum += (sample * sample);
                    }
                    double rms = Math.sqrt(sum / (read / 2.0));
                    if (rms < 1) {
                        rms = 1;
                    }
                    db = (20 * Math.log10(rms / 32768.0));
                    level = Math.min(1.0, rms / 32768.0);
                    byte[] pcmCopy = new byte[read];
                    System.arraycopy(buffer, 0, pcmCopy, 0, read);
                    volumeHandler.post(() -> notifyVolume(db, level, pcmCopy, read));
                } else {
                    volumeHandler.post(() -> notifyVolume(0, 0, null, 0));
                }
                if (areRecording) volumeHandler.postDelayed(this, volumeUpdateInterval);
            }
        };
    }

    /**
     * 停止音量监听
     * <p>
     * 释放资源
     */
    private void stopVolumeMonitoring() {
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (null != volumeHandlerThread) {
            volumeHandlerThread.quitSafely();
            try {
                volumeHandlerThread.join();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
            volumeHandlerThread = null;
            volumeHandler = null;
        }
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    private void setState(@NonNull State state) {
        currentState = state;
        notifyState(currentState);
    }

    /**
     * 在主线程设置状态
     *
     * @param state 状态
     */
    private void setStateOnMainThread(@NonNull State state) {
        mainHandler.post(() -> setState(state));
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * 唤醒状态
     *
     * @param state 状态
     */
    private void notifyState(@NonNull State state) {
        if (null != audioRecordListener) {
            mainHandler.post(() -> audioRecordListener.onState(state));
        }
    }

    /**
     * 唤醒音量
     *
     * @param db    分贝值
     *              负数
     * @param level 归一化等级
     *              范围 0 ~ 1
     * @param pcm   数据字节数组
     *              长度 = read
     * @param read  有效字节数
     */
    private void notifyVolume(double db, double level, @Nullable byte[] pcm, int read) {
        if (null != audioRecordListener)
            mainHandler.post(() -> audioRecordListener.onVolume(db, level, pcm, read));
    }

    /**
     * 是否可以停止录制
     *
     * @return 可以停止录制否
     */
    public boolean canStartRecording() {
        return (currentState == State.IDLE) || (currentState == State.STOPPED) || (currentState == State.CANCELED);
    }

    /**
     * 是否可以暂停录制
     *
     * @return 可以暂停录制否
     */
    public boolean canPauseRecording() {
        return (currentState == State.RECORDING);
    }

    /**
     * 是否可以恢复录制
     *
     * @return 可以恢复录制否
     */
    public boolean canResumeRecording() {
        return (currentState == State.PAUSED);
    }

    /**
     * 是否可以停止录制
     *
     * @return 可以停止录制否
     */
    public boolean canStopRecording() {
        return (currentState == State.RECORDING) || (currentState == State.PAUSED);
    }

    /**
     * 是否可以取消录制
     *
     * @return 可以取消录制否
     */
    public boolean canCancelRecording() {
        return (currentState == State.RECORDING) || (currentState == State.PAUSED);
    }

    /**
     * 获取录制音频列表
     *
     * @param context                上下文
     * @param fileNamePrefix         文件名前缀
     * @param mediaFileTypeEnum      媒体文件类型枚举
     * @param mediaFileDirectoryEnum 媒体文件目录枚举
     * @return 录制音频列表
     */
    @NonNull
    public static List<MediaFileInfo> getRecordAudioList(@NonNull Context context, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum, @NonNull MediaFileDirectoryEnum mediaFileDirectoryEnum) {
        List<MediaFileInfo> mediaFileInfos = new ArrayList<>();
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = null;
            String[] selectionArgs = null;
            if (!TextUtils.isEmpty(fileNamePrefix)) {
                selection = (MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?");
                selectionArgs = new String[]{fileNamePrefix + "%"};
            }
            String sortOrder = (MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
            try (Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder)) {
                if (null != cursor) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                        long modified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                        Uri fileUri = Uri.fromFile(new File(path));
                        long duration = getMediaFileDuration(context, fileUri, null);
                        mediaFileInfos.add(new MediaFileInfo(name, path, fileUri, size, duration, modified));
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return mediaFileInfos;
    }

    /**
     * 获取媒体文件时长
     *
     * @param context  上下文
     * @param uri      统一资源标识符
     * @param duration 时长
     * @return 媒体文件时长
     */
    public static long getMediaFileDuration(@NonNull Context context, @Nullable Uri uri, @Nullable Long duration) {
        if (null != duration) {
            return duration;
        }
        if (null == uri) {
            return 0;
        }
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(context, uri);
            String d = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return (!TextUtils.isEmpty(d) ? Long.parseLong(d) : 0L);
        } catch (Exception e) {
            Timber.e(e, "fail to retrieve duration: %s", uri);
            return 0L;
        }
    }

    /**
     * 音频录制监听
     */
    public interface AudioRecordListener {
        /**
         * 状态
         *
         * @param state 状态
         */
        void onState(@NonNull State state);

        /**
         * 音量
         *
         * @param db    分贝值
         *              负数
         * @param level 归一化等级
         *              范围 0 ~ 1
         * @param pcm   数据字节数组
         *              长度 = read
         * @param read  有效字节数
         */
        void onVolume(double db, double level, @Nullable byte[] pcm, int read);

        /**
         * 停止
         *
         * @param mediaFileInfo 媒体文件信息
         */
        void onStop(@NonNull MediaFileInfo mediaFileInfo);
    }

    /**
     * 设置音频录制监听
     *
     * @param audioRecordListener 音频录制监听
     */
    public void setAudioRecordListener(@NonNull AudioRecordListener audioRecordListener) {
        this.audioRecordListener = audioRecordListener;
    }

    /**
     * 发送吐司
     *
     * @param message 消息
     */
    private void postToast(@NonNull final String message) {
        mainHandler.post(() -> ToastKit.showShort(message));
    }

    /**
     * 释放资源
     */
    public void releaseResources() {
        try {
            if (null != mediaRecorder) {
                try {
                    mediaRecorder.release();
                } catch (Exception e) {
                    Timber.e(e);
                }
                mediaRecorder = null;
            }
            stopVolumeMonitoring();
            if ((null != executorService) && !executorService.isShutdown()) {
                executorService.shutdownNow();
            }
            audioRecordListener = null;
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}