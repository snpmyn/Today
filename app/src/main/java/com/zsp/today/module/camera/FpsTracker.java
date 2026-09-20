package com.zsp.today.module.camera;

/**
 * @decs: 帧率追踪器
 * @author: 郑少鹏
 * @date: 2026/9/18 15:30
 * @version: v 1.0
 */
@SuppressWarnings("unused")
public class FpsTracker {
    /**
     * 帧率更新回调
     */
    private final OnFpsUpdateCallback onFpsUpdateCallback;
    private int frameCount = 0;
    private long lastTimeNs = 0L;
    private volatile float realTimeFps = 0.0f;

    /**
     * constructor
     *
     * @param onFpsUpdateCallback 帧率更新回调
     */
    public FpsTracker(OnFpsUpdateCallback onFpsUpdateCallback) {
        this.onFpsUpdateCallback = onFpsUpdateCallback;
    }

    /**
     * 在相机每次渲染 / 输出新帧时调用该方法
     * <p>
     * 如 SurfaceTexture / ImageReader 的回调
     */
    public synchronized void onFrameAvailable() {
        long currentTimeNs = System.nanoTime();
        if (lastTimeNs == 0L) {
            lastTimeNs = currentTimeNs;
            return;
        }
        frameCount++;
        long timeDiffNs = (currentTimeNs - lastTimeNs);
        // 每隔 1 秒 (1,000,000,000 纳秒) 刷新一次 FPS 计算值
        if (timeDiffNs >= 1_000_000_000L) {
            realTimeFps = (float) frameCount * 1_000_000_000L / timeDiffNs;
            frameCount = 0;
            lastTimeNs = currentTimeNs;
            if (onFpsUpdateCallback != null) {
                onFpsUpdateCallback.onFpsUpdate(realTimeFps);
            }
        }
    }

    /**
     * 获取最近一次计算出的实时帧率
     *
     * @return 最近一次计算出的实时帧率
     */
    public float getLatestRealTimeFps() {
        return realTimeFps;
    }

    /**
     * 重置
     * <p>
     * 在切换分辨率或重置 Camera Preview 时调用
     */
    public synchronized void reset() {
        frameCount = 0;
        lastTimeNs = 0L;
        realTimeFps = 0.0f;
    }

    /**
     * 帧率更新回调
     */
    public interface OnFpsUpdateCallback {
        /**
         * 帧率更新
         * <p>
         * 回调在帧渲染 / 输出线程触发
         * 更新 UI 需自行切换至主线程
         *
         * @param fps 帧率
         */
        void onFpsUpdate(float fps);
    }
}