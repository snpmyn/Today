package com.zsp.today.module.camera.function.preview;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zsp.today.module.camera.function.fps.FpsTracker;

import java.lang.ref.WeakReference;

/**
 * Created on 2026/9/15.
 *
 * @author 郑少鹏
 * @desc 相机预览配套原件
 */
public class CameraPreviewKit {
    /**
     * View 布局改变监听器
     */
    private View.OnLayoutChangeListener layoutChangeListener;
    /**
     * 绑定的 PreviewView 弱引用
     */
    private WeakReference<PreviewView> previewViewWeakReference;

    /**
     * 应用预览配置
     *
     * @param previewView 预览视图
     */
    public void applyPreviewConfig(@NonNull PreviewView previewView) {
        // ==================================================================================================================================================
        // A. 渲染模式 - COMPATIBLE
        //    采用 TextureView 模式以提升复杂 UI (如圆角 CardView 裁剪、Overlay 覆盖物) 兼容性
        // B. 缩放策略 - FILL_CENTER
        //    - FIT_CENTER 缺陷
        //    TextureView 渲染层在进行矩阵变换时，因 Android 视图树测量 (Measure Pass) 与 Sensor 帧率同步的亚像素四舍五入偏差，极其容易在 View 边缘产生 1 ~ 2px 的补齐黑边 / 黑缝。
        //    - FILL_CENTER 优势
        //    等比放大画面以完全覆盖 View。在外层 CardView 已锁定图像原生宽高比的前提下，以极微小裁切 (容错) 抵消亚像素黑边。
        // ==================================================================================================================================================
        previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
    }

    /**
     * 更新预览视图容器宽高比
     *
     * @param previewViewContainerView 预览视图容器
     * @param resolution               分辨率
     */
    public void updatePreviewContainerRatio(@NonNull View previewViewContainerView, @Nullable Size resolution) {
        if (resolution == null) {
            return;
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) previewViewContainerView.getLayoutParams();
        int width = resolution.getWidth();
        int height = resolution.getHeight();
        layoutParams.dimensionRatio = "H," + width + ":" + height;
        previewViewContainerView.setLayoutParams(layoutParams);
    }

    /**
     * 初始化帧率追踪器代理
     *
     * @param previewView 预览视图
     * @param fpsTracker  帧率追踪器
     */
    public void setupFpsTrackerProxy(@NonNull PreviewView previewView, @Nullable FpsTracker fpsTracker) {
        this.previewViewWeakReference = new WeakReference<>(previewView);
        // 获取异步挂载 TextureView
        previewView.post(() -> {
            TextureView textureView = findTextureView(previewView);
            if (textureView != null) {
                attachTextureViewListener(textureView, fpsTracker);
            } else {
                // 首帧未成功获取 TextureView 则监听 View 树状态再次尝试并缓存 Listener 供销毁时精准移除
                if (layoutChangeListener != null && previewViewWeakReference.get() != null) {
                    previewViewWeakReference.get().removeOnLayoutChangeListener(layoutChangeListener);
                }
                layoutChangeListener = new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        TextureView tv = findTextureView(previewView);
                        if (tv != null) {
                            previewView.removeOnLayoutChangeListener(this);
                            layoutChangeListener = null;
                            attachTextureViewListener(tv, fpsTracker);
                        }
                    }
                };
                previewView.addOnLayoutChangeListener(layoutChangeListener);
            }
        });
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
     * 释放
     *
     * @param previewView 预览视图
     */
    public void release(@Nullable PreviewView previewView) {
        if ((layoutChangeListener != null) && (previewView != null)) {
            previewView.removeOnLayoutChangeListener(layoutChangeListener);
            layoutChangeListener = null;
        }
        if (previewViewWeakReference != null) {
            previewViewWeakReference.clear();
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