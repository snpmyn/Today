package com.zsp.today.module.camera.function.fps;

import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import java.lang.ref.WeakReference;

/**
 * Created on 2026/9/23.
 *
 * @author 郑少鹏
 * @desc 帧率配套原件
 */
public class FpsKit {
    /**
     * 异步挂载任务
     */
    private Runnable runnable;
    /**
     * 帧率追踪器
     */
    private FpsTracker fpsTracker;
    /**
     * View 布局改变监听器
     */
    private View.OnLayoutChangeListener layoutChangeListener;
    /**
     * 绑定的 PreviewView 弱引用
     */
    private WeakReference<PreviewView> previewViewWeakReference;

    /**
     * 设置帧率追踪器
     *
     * @param fpsTracker 帧率追踪器
     */
    public void setFpsTracker(@Nullable FpsTracker fpsTracker) {
        this.fpsTracker = fpsTracker;
    }

    /**
     * 初始化帧率追踪器代理
     *
     * @param previewView 预览视图
     */
    public void setupFpsTrackerProxy(@NonNull PreviewView previewView) {
        this.previewViewWeakReference = new WeakReference<>(previewView);
        if (runnable != null) {
            previewView.removeCallbacks(runnable);
        }
        // 获取异步挂载 TextureView
        runnable = () -> {
            PreviewView pv = previewViewWeakReference.get();
            if (pv == null) {
                return;
            }
            TextureView textureView = findTextureView(pv);
            if (textureView != null) {
                attachTextureViewListener(textureView, fpsTracker);
            } else {
                // 首帧未成功获取 TextureView 则监听 View 树状态再次尝试并缓存 Listener 供销毁时精准移除
                if (layoutChangeListener != null) {
                    pv.removeOnLayoutChangeListener(layoutChangeListener);
                }
                layoutChangeListener = new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        TextureView tv = findTextureView(pv);
                        if (tv != null) {
                            pv.removeOnLayoutChangeListener(this);
                            layoutChangeListener = null;
                            attachTextureViewListener(tv, fpsTracker);
                        }
                    }
                };
                pv.addOnLayoutChangeListener(layoutChangeListener);
            }
        };
        previewView.post(runnable);
    }

    /**
     * 绑定 TextureView 的 SurfaceTextureListener 代理
     *
     * @param textureView TextureView
     * @param fpsTracker  帧率追踪器
     */
    private void attachTextureViewListener(@NonNull TextureView textureView, @Nullable FpsTracker fpsTracker) {
        TextureView.SurfaceTextureListener originalListener = textureView.getSurfaceTextureListener();
        if (!(originalListener instanceof FpsProxySurfaceTextureListener)) {
            textureView.setSurfaceTextureListener(new FpsProxySurfaceTextureListener(originalListener, fpsTracker));
        }
    }

    /**
     * 递归遍历寻找 PreviewView 内部 TextureView
     *
     * @param rootView 根视图
     * @return PreviewView 内部 TextureView
     */
    @Nullable
    private TextureView findTextureView(View rootView) {
        if (rootView instanceof TextureView) {
            return (TextureView) rootView;
        }
        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextureView tv = findTextureView(group.getChildAt(i));
                if (tv != null) {
                    return tv;
                }
            }
        }
        return null;
    }

    /**
     * 重置
     */
    public void reset() {
        if (fpsTracker != null) {
            fpsTracker.reset();
        }
    }

    /**
     * 释放
     *
     * @param previewView 预览视图
     */
    public void release(@Nullable PreviewView previewView) {
        reset();
        if (previewView != null) {
            if (runnable != null) {
                previewView.removeCallbacks(runnable);
                runnable = null;
            }
            if (layoutChangeListener != null) {
                previewView.removeOnLayoutChangeListener(layoutChangeListener);
                layoutChangeListener = null;
            }
        }
        if (previewViewWeakReference != null) {
            previewViewWeakReference.clear();
            previewViewWeakReference = null;
        }
    }

    /**
     * Fps Proxy SurfaceTextureListener 代理内部类
     */
    private static class FpsProxySurfaceTextureListener implements TextureView.SurfaceTextureListener {
        private final TextureView.SurfaceTextureListener originalSurfaceTextureListener;
        private final FpsTracker fpsTracker;

        public FpsProxySurfaceTextureListener(TextureView.SurfaceTextureListener originalSurfaceTextureListener, FpsTracker fpsTracker) {
            this.originalSurfaceTextureListener = originalSurfaceTextureListener;
            this.fpsTracker = fpsTracker;
        }

        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            if (originalSurfaceTextureListener != null) {
                originalSurfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            if (originalSurfaceTextureListener != null) {
                originalSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return (originalSurfaceTextureListener == null) || originalSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            if (originalSurfaceTextureListener != null) {
                originalSurfaceTextureListener.onSurfaceTextureUpdated(surface);
            }
            if (fpsTracker != null) {
                fpsTracker.onFrameAvailable();
            }
        }
    }
}