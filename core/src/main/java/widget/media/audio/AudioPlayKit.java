package widget.media.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.core.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import timber.log.Timber;
import widget.toast.ToastKit;

/**
 * @decs: 音频播放配套原件
 * @author: 郑少鹏
 * @date: 2025/9/23 14:12
 * @version: v 1.0
 * <p>
 * 支持本地播放
 * {@link #play(Context, String, DownloadProgressListener, int)}
 * File file = new File(getExternalFilesDir(null), "sample.mp3");
 * AudioPlayKit.play(this, file.getAbsolutePath(), null, 0);
 * <p>
 * 支持网络播放
 * {@link #play(Context, String, DownloadProgressListener, int)}
 * String url = "<a href="https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3">...</a>";
 * AudioPlayKit.play(this, url, null, 0);
 * <p>
 * 支持 assets 播放
 * {@link #play(Context, String, DownloadProgressListener, int)}
 * AudioPlayKit.play(this, "release.mp3", null, 0);
 * <p>
 * 支持 URI 播放
 * {@link #playWithUri(Context, Uri, int)}
 * <p>
 * 支持缓存管理
 * {@link CacheHelper#setEnableNetworkCache(boolean)}
 * {@link CacheHelper#setMaxNetworkCacheSize(long)} (boolean)}
 * <p>
 * 获取缓存文件
 * {@link CacheHelper#getCacheFile(Context, String)}
 * <p>
 * 清理缓存
 * {@link CacheHelper#clearCache(Context)}
 * <p>
 * 支持下载进度回调
 * <p>
 * 支持播放进度回调
 * 支持播放状态回调
 */
public class AudioPlayKit {
    private static MediaPlayer mediaPlayer;
    private static PlayProgressListener playProgressListener;
    private static PlayStateListener playStateListener;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    /**
     * 进度间隔
     * <p>
     * 毫秒
     */
    private static final int PROGRESS_INTERVAL = 100;
    /**
     * 是否正在播放或准备
     */
    private static boolean arePlayingOrPreparing = false;
    private static int currentPlayId = 0;
    /**
     * 是否播放完成
     */
    private static boolean arePlayComplete = false;
    /**
     * 播放进度回调
     */
    private static final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if ((null != mediaPlayer) && mediaPlayer.isPlaying()) {
                if (null != playProgressListener) {
                    try {
                        playProgressListener.onProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    } catch (Exception e) {
                        Timber.e(e, "progress callback error");
                    }
                }
                mainHandler.postDelayed(this, PROGRESS_INTERVAL);
            }
        }
    };

    /**
     * 播放
     *
     * @param context                  上下文
     * @param path                     路径
     * @param downloadProgressListener 下载进度监听
     * @param startPositionMs          开始位置毫秒
     *                                 传 0 表示从头开始
     */
    public static synchronized void play(@NonNull Context context, @NonNull String path, @Nullable DownloadProgressListener downloadProgressListener, int startPositionMs) {
        // 停止上次播放
        stop();
        mediaPlayer = new MediaPlayer();
        arePlayingOrPreparing = true;
        currentPlayId++;
        final int playId = currentPlayId;
        try {
            File localFile = new File(path);
            if (localFile.exists()) {
                // 本地
                mediaPlayer.setDataSource(path);
                prepareAndPlay(context, mediaPlayer, playId, startPositionMs);
            } else if (path.startsWith("http://") || path.startsWith("https://")) {
                // 网络
                CacheHelper.prepareNetworkSource(context, path, mediaPlayer, () -> {
                    if (playId != currentPlayId) {
                        return;
                    }
                    errorHandle(context);
                }, downloadProgressListener, playId, startPositionMs);
            } else {
                // assets
                AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(path);
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                assetFileDescriptor.close();
                prepareAndPlay(context, mediaPlayer, playId, startPositionMs);
            }
        } catch (Exception e) {
            if (playId == currentPlayId) {
                errorHandle(context);
            }
        }
    }

    /**
     * 通过统一资源标识符播放
     *
     * @param context         上下文
     * @param uri             统一资源标识符
     * @param startPositionMs 开始位置毫秒
     *                        传 0 表示从头开始
     */
    public static synchronized void playWithUri(@NonNull Context context, @NonNull Uri uri, int startPositionMs) {
        stop();
        mediaPlayer = new MediaPlayer();
        arePlayingOrPreparing = true;
        currentPlayId++;
        final int playId = currentPlayId;
        try {
            mediaPlayer.setDataSource(context, uri);
            prepareAndPlay(context, mediaPlayer, playId, startPositionMs);
        } catch (Exception e) {
            if (playId == currentPlayId) errorHandle(context);
        }
    }

    /**
     * 错误处理
     *
     * @param context 上下文
     */
    private static void errorHandle(@NonNull Context context) {
        release();
        mainHandler.post(() -> ToastKit.showShort(context.getString(R.string.playFail)));
        if (null != playStateListener) {
            playStateListener.onError(-1, -1);
        }
    }

    /**
     * 暂停
     */
    public static synchronized void pause() {
        if ((null != mediaPlayer) && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                stopUpdateProgress();
                if (null != playStateListener) {
                    playStateListener.onPause();
                }
            } catch (Exception e) {
                Timber.e(e, "pause fail");
            }
        }
    }

    /**
     * 恢复
     */
    public static synchronized void resume() {
        if ((null != mediaPlayer) && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
                startUpdateProgress(currentPlayId);
                if (null != playStateListener) {
                    playStateListener.onResume();
                }
            } catch (Exception e) {
                Timber.e(e, "resume fail");
            }
        }
    }

    /**
     * 停止
     */
    public static synchronized void stop() {
        if ((null != mediaPlayer) || arePlayingOrPreparing) {
            try {
                if ((null != mediaPlayer) && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {

            }
            // 中断下载线程
            if ((null != CacheHelper.currentDownloadThread) && CacheHelper.currentDownloadThread.isAlive()) {
                CacheHelper.currentDownloadThread.interrupt();
                CacheHelper.currentDownloadThread = null;
            }
            release();
            arePlayingOrPreparing = false;
            if (null != playStateListener) {
                playStateListener.onStop();
            }
        }
    }

    /**
     * 释放
     */
    public static synchronized void release() {
        if (null != mediaPlayer) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception ignored) {

            }
            mediaPlayer = null;
        }
        mainHandler.removeCallbacks(progressRunnable);
    }

    /**
     * 指定到
     *
     * @param positionMs 位置毫秒
     */
    public static synchronized void seekTo(int positionMs) {
        if (null != mediaPlayer) {
            try {
                int duration = mediaPlayer.getDuration();
                positionMs = Math.max(0, Math.min(positionMs, duration));
                mediaPlayer.seekTo(positionMs);
                if (null != playProgressListener) {
                    playProgressListener.onProgress(mediaPlayer.getCurrentPosition(), duration);
                }
            } catch (Exception e) {
                Timber.e(e, "seek fail");
            }
        }
    }

    /**
     * 是否正在播放
     *
     * @return 正在播放否
     */
    public static boolean arePlaying() {
        return ((null != mediaPlayer) && mediaPlayer.isPlaying());
    }

    /**
     * 获取时长
     *
     * @return 时长
     */
    public static int getDuration() {
        return (null != mediaPlayer) ? mediaPlayer.getDuration() : 0;
    }

    /**
     * 获取当前位置
     *
     * @return 当前位置
     */
    public static int getCurrentPosition() {
        return (null != mediaPlayer) ? mediaPlayer.getCurrentPosition() : 0;
    }

    /**
     * 设置播放进度监听
     *
     * @param playProgressListener 播放进度监听
     */
    public static void setOnProgressListener(PlayProgressListener playProgressListener) {
        AudioPlayKit.playProgressListener = playProgressListener;
    }

    /**
     * 设置播放状态监听
     *
     * @param playStateListener 播放状态监听
     */
    public static void setOnStateListener(PlayStateListener playStateListener) {
        AudioPlayKit.playStateListener = playStateListener;
    }

    /**
     * 开始更新进度
     *
     * @param playId 播放 ID
     */
    private static void startUpdateProgress(int playId) {
        mainHandler.removeCallbacks(progressRunnable);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (playId != currentPlayId) {
                    return;
                }
                if ((null != mediaPlayer) && mediaPlayer.isPlaying()) {
                    if (null != playProgressListener) {
                        try {
                            playProgressListener.onProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                        } catch (Exception e) {
                            Timber.e(e, "progress callback error");
                        }
                    }
                    mainHandler.postDelayed(this, PROGRESS_INTERVAL);
                }
            }
        });
    }

    /**
     * 停止更新进度
     */
    private static void stopUpdateProgress() {
        mainHandler.removeCallbacks(progressRunnable);
    }

    /**
     * 准备并播放
     *
     * @param context         上下文
     * @param mp              MediaPlayer
     * @param playId          播放 ID
     * @param startPositionMs 开始位置毫秒
     *                        传 0 表示从头开始
     */
    private static void prepareAndPlay(@NonNull Context context, @NonNull MediaPlayer mp, int playId, int startPositionMs) {
        arePlayingOrPreparing = true;
        mp.setOnPreparedListener(mediaPlayer -> {
            if (playId != currentPlayId) {
                return;
            }
            arePlayComplete = false;
            if (startPositionMs > 0) {
                mediaPlayer.seekTo(startPositionMs);
            }
            mediaPlayer.start();
            startUpdateProgress(playId);
            if (null != playStateListener) {
                playStateListener.onStart();
            }
        });
        mp.setOnCompletionListener(mediaPlayer -> {
            if (playId != currentPlayId) {
                return;
            }
            arePlayingOrPreparing = false;
            arePlayComplete = true;
            if (null != playProgressListener) {
                try {
                    playProgressListener.onProgress(mediaPlayer.getDuration(), mediaPlayer.getDuration());
                } catch (Exception e) {
                    Timber.e(e, "completion progress callback error");
                }
            }
            stop();
            if (null != playStateListener) {
                playStateListener.onComplete();
            }
        });
        mp.setOnErrorListener((mediaPlayer, what, extra) -> {
            if (playId != currentPlayId) {
                return true;
            }
            arePlayingOrPreparing = false;
            release();
            mainHandler.post(() -> ToastKit.showShort(context.getString(R.string.playFail)));
            if (null != playStateListener) {
                playStateListener.onError(what, extra);
            }
            return true;
        });
        mp.prepareAsync();
    }

    /**
     * 是否播放完成
     *
     * @return 播放完成否
     */
    public static boolean arePlayComplete() {
        return arePlayComplete;
    }

    /**
     * 播放进度监听
     */
    public interface PlayProgressListener {
        /**
         * 进度
         *
         * @param currentMs 当前进度毫秒
         * @param totalMs   总进度毫秒
         */
        void onProgress(int currentMs, int totalMs);
    }

    /**
     * 播放状态监听
     */
    public interface PlayStateListener {
        /**
         * 开始
         */
        void onStart();

        /**
         * 暂停
         */
        void onPause();

        /**
         * 恢复
         */
        void onResume();

        /**
         * 停止
         */
        void onStop();

        /**
         * 完成
         */
        void onComplete();

        /**
         * 错误
         *
         * @param what  错误类型
         *              MediaPlayer 定义的常量
         *              如 MEDIA_ERROR_UNKNOWN、MEDIA_ERROR_SERVER_DIED 等
         * @param extra 附加信息
         *              错误码
         *              含义依赖具体错误类型
         */
        void onError(int what, int extra);
    }

    /**
     * 下载进度监听
     */
    public interface DownloadProgressListener {
        /**
         * 下载进度
         *
         * @param progress 进度
         */
        void onDownloadProgress(int progress);

        /**
         * 下载完成
         */
        void onDownloadComplete();

        /**
         * 下载错误
         */
        void onDownloadError();
    }

    /**
     * 缓存帮助者
     */
    public static class CacheHelper {
        /**
         * 50 MB
         */
        private static long MAX_NETWORK_CACHE_SIZE = 50 * 1024 * 1024;
        private static boolean enableNetworkCache = true;
        private static Thread currentDownloadThread;

        /**
         * 设置是否允许网络缓存
         *
         * @param enable 允许否
         */
        public static void setEnableNetworkCache(boolean enable) {
            enableNetworkCache = enable;
        }

        /**
         * 设置最大网络缓存大小
         *
         * @param maxNetworkCacheSize 最大网络缓存大小
         *                            单位字节
         */
        public static void setMaxNetworkCacheSize(long maxNetworkCacheSize) {
            MAX_NETWORK_CACHE_SIZE = maxNetworkCacheSize;
        }

        /**
         * 获取缓存文件
         *
         * @param context 上下文
         * @param url     统一资源标识符
         * @return 缓存文件
         */
        @NonNull
        public static File getCacheFile(@NonNull Context context, @NonNull String url) {
            return new File(context.getCacheDir(), "audio-cache-" + url.hashCode() + ".mp3");
        }

        /**
         * 确认网络缓存大小
         * <p>
         * 超 MAX_CACHE_SIZE 时删最老文件
         *
         * @param context 上下文
         */
        private static void ensureNetworkCacheSize(@NonNull Context context) {
            File cacheDir = context.getCacheDir();
            File[] files = cacheDir.listFiles((dir, name) -> name.startsWith("audio-cache-"));
            if (null == files) {
                return;
            }
            long totalSize = 0;
            for (File file : files) {
                totalSize += file.length();
            }
            if (totalSize > MAX_NETWORK_CACHE_SIZE) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                long sizeToFree = (totalSize - MAX_NETWORK_CACHE_SIZE);
                for (File cacheFile : files) {
                    sizeToFree -= cacheFile.length();
                    if (!cacheFile.delete()) {
                        Timber.w("删除缓存文件失败: %s", cacheFile.getAbsolutePath());
                    }
                    if (sizeToFree <= 0) {
                        break;
                    }
                }
            }
        }

        /**
         * 准备网络资源
         *
         * @param context                  上下文
         * @param url                      链接
         * @param mediaPlayer              MediaPlayer
         * @param errorRunnable            Runnable
         * @param downloadProgressListener 下载进度监听
         * @param playId                   播放 ID
         * @param startPositionMs          开始位置毫秒
         *                                 传 0 表示从头开始
         */
        public static void prepareNetworkSource(@NonNull Context context, @NonNull String url, @NonNull MediaPlayer mediaPlayer, @NonNull Runnable errorRunnable, @Nullable DownloadProgressListener downloadProgressListener, int playId, int startPositionMs) {
            if ((null != currentDownloadThread) && currentDownloadThread.isAlive()) {
                currentDownloadThread.interrupt();
            }
            File cacheFile = getCacheFile(context, url);
            // 使用缓存文件
            if (enableNetworkCache && cacheFile.exists() && (cacheFile.length() > 0)) {
                try {
                    if (playId != currentPlayId) {
                        return;
                    }
                    mediaPlayer.setDataSource(cacheFile.getAbsolutePath());
                    AudioPlayKit.prepareAndPlay(context, mediaPlayer, playId, startPositionMs);
                    if ((null != downloadProgressListener) && playId == currentPlayId) {
                        mainHandler.post(downloadProgressListener::onDownloadComplete);
                    }
                    return;
                } catch (Exception e) {
                    if (!cacheFile.delete()) {
                        Timber.w("删除缓存文件失败: %s", cacheFile.getAbsolutePath());
                    }
                }
            }
            // 当前下载线程
            currentDownloadThread = new Thread(() -> {
                File downloaded = downloadToCache(context, url, downloadProgressListener, playId);
                if ((null != downloaded) && downloaded.exists()) {
                    try {
                        if (playId != currentPlayId) {
                            return;
                        }
                        mediaPlayer.setDataSource(downloaded.getAbsolutePath());
                        AudioPlayKit.prepareAndPlay(context, mediaPlayer, playId, startPositionMs);
                    } catch (Exception e) {
                        if (playId == currentPlayId) {
                            mainHandler.post(errorRunnable);
                        }
                    }
                } else {
                    if (playId == currentPlayId) {
                        mainHandler.post(errorRunnable);
                    }
                }
            });
            currentDownloadThread.start();
        }

        /**
         * 下载至缓存
         *
         * @param context                  上下文
         * @param urlString                链接字符串
         * @param downloadProgressListener 下载进度监听
         * @param playId                   播放 ID
         * @return 下载至缓存文件
         */
        @Nullable
        private static File downloadToCache(@NonNull Context context, @NonNull String urlString, @Nullable DownloadProgressListener downloadProgressListener, int playId) {
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                long totalBytes = httpURLConnection.getContentLength();
                if ((null != downloadProgressListener) && playId == currentPlayId) {
                    mainHandler.post(() -> downloadProgressListener.onDownloadProgress(0));
                }
                inputStream = httpURLConnection.getInputStream();
                if (enableNetworkCache) {
                    ensureNetworkCacheSize(context);
                }
                File cacheFile = getCacheFile(context, urlString);
                fileOutputStream = new FileOutputStream(cacheFile);
                byte[] buffer = new byte[4096];
                int len;
                long downloaded = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                    fileOutputStream.write(buffer, 0, len);
                    downloaded += len;
                    if ((null != downloadProgressListener) && (totalBytes > 0) && (playId == currentPlayId)) {
                        int percent = (int) (downloaded * 100 / totalBytes);
                        mainHandler.post(() -> downloadProgressListener.onDownloadProgress(percent));
                    }
                }
                fileOutputStream.flush();
                if ((null != downloadProgressListener) && playId == currentPlayId) {
                    mainHandler.post(downloadProgressListener::onDownloadComplete);
                }
                return cacheFile;
            } catch (Exception e) {
                if ((null != downloadProgressListener) && playId == currentPlayId) {
                    mainHandler.post(downloadProgressListener::onDownloadError);
                }
                return null;
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != fileOutputStream) {
                        fileOutputStream.close();
                    }
                } catch (Exception ignored) {

                }
            }
        }

        /**
         * 清理缓存
         *
         * @param context 上下文
         */
        public static void clearCache(@NonNull Context context) {
            File cacheDir = context.getCacheDir();
            File[] files = cacheDir.listFiles((dir, name) -> name.startsWith("audio-cache-"));
            if (null == files) {
                return;
            }
            for (File file : files) {
                if (!file.delete()) {
                    Timber.w("删除缓存文件失败: %s", file.getAbsolutePath());
                }
            }
        }
    }
}