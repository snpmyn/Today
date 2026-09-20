package com.zsp.today.module.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalLensFacing;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.zsp.today.module.camera.storage.LogKit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created on 2026/9/11.
 *
 * @author 郑少鹏
 * @desc 相机控制器
 */
public class CameraController {
    /**
     * 增强实现
     * <p>
     * 异步单线程池
     * 处理磁盘 IO 文件保存 + 避免阻塞主 UI 线程
     */
    private ExecutorService executorService;
    /**
     * 抓拍用例对象
     */
    private ImageCapture imageCapture;
    /**
     * 生命周期绑定提供者
     */
    private ProcessCameraProvider processCameraProvider;
    /**
     * 当前相机配置
     */
    private CameraConfig currentCameraConfig = null;
    /**
     * 帧率追踪器
     */
    private FpsTracker fpsTracker;

    /**
     * constructor
     */
    public CameraController() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取系统底层注册的所有相机 ID 列表
     *
     * @param context 上下文
     * @return 系统底层注册的所有相机 ID 列表
     */
    public List<String> getAvailableCameraIds(@NonNull Context context) {
        return CameraManagerKit.getAvailableCameraIds(context);
    }

    /**
     * 获取指定相机 ID 支持的原生分辨率列表
     *
     * @param context  上下文
     * @param cameraId 相机 ID
     * @return 指定相机 ID 支持的原生分辨率列表 [按总像素量降序排列]
     */
    public List<Size> getSupportedResolutions(@NonNull Context context, String cameraId) {
        return CameraManagerKit.getSupportedResolutions(context, cameraId);
    }

    /**
     * 启动相机
     *
     * @param context                  上下文
     * @param lifecycleOwner           生命周期拥有者
     * @param previewViewContainerView 预览视图容器
     * @param previewView              预览视图
     * @param cameraId                 目标相机 ID
     * @param resolution               分辨率
     * @param cameraInitCallback       相机初始回调
     */
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull View previewViewContainerView, @NonNull PreviewView previewView, String cameraId, Size resolution, CameraInitCallback cameraInitCallback) {
        CameraConfig cameraConfig = new CameraConfig.Builder().setCameraId(cameraId).setResolution(resolution).setTargetRotation(Surface.ROTATION_90).build();
        startCamera(context, lifecycleOwner, previewViewContainerView, previewView, cameraConfig, cameraInitCallback);
    }

    /**
     * 启动相机
     *
     * @param context                  上下文
     * @param lifecycleOwner           生命周期拥有者
     * @param previewViewContainerView 预览视图容器
     * @param previewView              预览视图
     * @param cameraConfig             相机配置
     * @param cameraInitCallback       相机初始回调
     */
    @SuppressLint("WrongConstant")
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull View previewViewContainerView, @NonNull PreviewView previewView, @NonNull CameraConfig cameraConfig, CameraInitCallback cameraInitCallback) {
        this.currentCameraConfig = cameraConfig;
        if (fpsTracker != null) {
            fpsTracker.reset();
        }
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                // 1. 构建 ResolutionSelector 分辨率选择器
                Size resolution = currentCameraConfig.getResolution();
                ResolutionStrategy resolutionStrategy = (resolution != null) ? new ResolutionStrategy(resolution, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER) : ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY;
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder().setResolutionStrategy(resolutionStrategy).build();
                // 2. 配置 PreviewView 渲染模式与缩放策略
                // ==================================================================================================================================================
                // A. 渲染模式 - COMPATIBLE
                //    采用 TextureView 模式以提升复杂 UI (如圆角 CardView 裁剪、Overlay 覆盖物) 兼容性
                // B. 缩放策略 - FILL_CENTER
                //    - FIT_CENTER 缺陷
                //    TextureView 渲染层在进行矩阵变换时，因 Android 视图树测量 (Measure Pass) 与 Sensor 帧率同步的亚像素四舍五入偏差，极其容易在 View 边缘产生 1 ~ 2px 的补齐黑边 / 黑缝。
                //    - FILL_CENTER 优势
                //    由于外层 CardView 的 dimensionRatio 已被严格锁定为图像原生宽高比，FILL_CENTER 充当 [填满容错机制]，在消除亚像素黑边的同时，绝对不会造成任何画面的裁切。
                // ==================================================================================================================================================
                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
                // 3. 构建 Preview 预览用例
                Preview preview = new Preview.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(currentCameraConfig.getTargetRotation()).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // 4. 构建 ImageCapture 拍照用例
                imageCapture = new ImageCapture.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(currentCameraConfig.getTargetRotation()).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
                // 5. 构建 CameraSelector 相机选择器
                CameraSelector cameraSelector;
                String cameraId = currentCameraConfig.getCameraId();
                if ((cameraId != null) && !cameraId.isEmpty()) {
                    // 如已指定 CameraID 则根据 CameraID 进行精确过滤
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(cameraInfos -> {
                        List<CameraInfo> result = new ArrayList<>();
                        for (CameraInfo cameraInfo : cameraInfos) {
                            String id = Camera2CameraInfo.from(cameraInfo).getCameraId();
                            if (id.equals(cameraId)) {
                                result.add(cameraInfo);
                            }
                        }
                        return result;
                    }).build();
                } else {
                    // 未指定 CameraID 时使用自定义优先级过滤
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(this::filterCamera).build();
                }
                // 6. 解绑并重新绑定生命周期
                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
                // 初始化帧率追踪器
                setupFpsTracker(previewView);
                // 根据选定分辨率动态更新 ConstraintLayout 容器宽高比
                // 确保图像无形变且居中
                if (resolution != null) {
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams layoutParams = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) previewViewContainerView.getLayoutParams();
                    layoutParams.dimensionRatio = "H," + resolution.getWidth() + ":" + resolution.getHeight();
                    previewViewContainerView.setLayoutParams(layoutParams);
                }
                Timber.tag(LogKit.TAG).i("CameraX 启动成功 - 旋转角度: %d", currentCameraConfig.getTargetRotation());
                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitSuccess();
                }
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "CameraX 初始化失败");
                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitError(e);
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * 过滤相机
     * <p>
     * 优先级顺序
     * 1. 外接摄像头 (LENS_FACING_EXTERNAL) - 适用于标准 USB 高拍仪 / UVC 外接设备
     * 2. 朝向未知的摄像头 (LENS_FACING_UNKNOWN) - 兼容驱动未按标准实现的定制硬件
     * 3. 相机列表中的最后一个摄像头 - 作为异常硬件架构下的兜底选项
     *
     * @param cameraInfos 可用相机信息列表
     * @return 过滤后的目标相机列表
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    private List<CameraInfo> filterCamera(List<CameraInfo> cameraInfos) {
        if ((cameraInfos == null) || cameraInfos.isEmpty()) {
            return Collections.emptyList();
        }
        CameraInfo unknownCameraInfo = null;
        for (CameraInfo cameraInfo : cameraInfos) {
            int lensFacing = cameraInfo.getLensFacing();
            if (lensFacing == CameraSelector.LENS_FACING_EXTERNAL) {
                return Collections.singletonList(cameraInfo);
            } else if ((lensFacing == CameraSelector.LENS_FACING_UNKNOWN) && (unknownCameraInfo == null)) {
                unknownCameraInfo = cameraInfo;
            }
        }
        if (unknownCameraInfo != null) {
            return Collections.singletonList(unknownCameraInfo);
        }
        return Collections.singletonList(cameraInfos.get(cameraInfos.size() - 1));
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param cameraCaptureCallback 相机拍照回调
     */
    public void capture(@NonNull Context context, @NonNull PreviewView previewView, CameraCaptureCallback cameraCaptureCallback) {
        ensureExecutorAvailable();
        CaptureHelper.capture(context, imageCapture, previewView, executorService, cameraCaptureCallback);
    }

    /**
     * 确保线程池可用
     */
    private synchronized void ensureExecutorAvailable() {
        if ((executorService == null) || executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * 设置帧率追踪器
     *
     * @param fpsTracker 帧率追踪器
     */
    public void setFpsTracker(@Nullable FpsTracker fpsTracker) {
        this.fpsTracker = fpsTracker;
    }

    /**
     * 初始化帧率追踪器
     * <p>
     * 在 COMPATIBLE 模式下挂载 SurfaceTextureListener 代理
     * 拦截 onSurfaceTextureUpdated 驱动 FpsTracker 计算实时帧率
     *
     * @param previewView 预览视图
     */
    private void setupFpsTracker(@NonNull PreviewView previewView) {
        // 使用 Runnable 提交延迟监听
        // 避开 View 初始测量与内部 TextureView 创建的时序错位
        previewView.post(() -> {
            TextureView textureView = findTextureView(previewView);
            if (textureView != null) {
                TextureView.SurfaceTextureListener originalListener = textureView.getSurfaceTextureListener();
                if (!(originalListener instanceof FpsProxySurfaceTextureListener)) {
                    textureView.setSurfaceTextureListener(new FpsProxySurfaceTextureListener(originalListener));
                }
            } else {
                // 若首帧未成功获取 TextureView
                // 监听 View 树状态再次尝试
                previewView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        TextureView tv = findTextureView(previewView);
                        if (tv != null) {
                            previewView.removeOnLayoutChangeListener(this);
                            TextureView.SurfaceTextureListener origListener = tv.getSurfaceTextureListener();
                            if (!(origListener instanceof FpsProxySurfaceTextureListener)) {
                                tv.setSurfaceTextureListener(new FpsProxySurfaceTextureListener(origListener));
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 递归遍历寻找 PreviewView 内部的 TextureView
     *
     * @param rootView 根视图
     * @return PreviewView 内部的 TextureView
     */
    @Nullable
    private TextureView findTextureView(View rootView) {
        if (rootView instanceof TextureView) {
            return (TextureView) rootView;
        }
        if (rootView instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) rootView;
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
     */
    public void release() {
        if (fpsTracker != null) {
            fpsTracker.reset();
        }
        if (processCameraProvider != null) {
            processCameraProvider.unbindAll();
        }
        if ((executorService != null) && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 相机初始回调
     */
    public interface CameraInitCallback {
        /**
         * 相机初始成功
         */
        void onCameraInitSuccess();

        /**
         * 相机初始错误
         *
         * @param throwable 异常
         */
        void onCameraInitError(Throwable throwable);
    }

    /**
     * 相机拍照回调
     */
    public interface CameraCaptureCallback {
        /**
         * 相机拍照成功
         *
         * @param photoFile 照片文件
         */
        void onCameraCaptureSuccess(File photoFile);

        /**
         * 相机拍照错误
         *
         * @param imageCaptureException 拍照异常
         */
        void onCameraCaptureError(ImageCaptureException imageCaptureException);
    }

    /**
     * Fps Proxy SurfaceTextureListener 代理内部类
     */
    private class FpsProxySurfaceTextureListener implements TextureView.SurfaceTextureListener {
        private final TextureView.SurfaceTextureListener originalListener;

        public FpsProxySurfaceTextureListener(TextureView.SurfaceTextureListener originalListener) {
            this.originalListener = originalListener;
        }

        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            if (originalListener != null) {
                originalListener.onSurfaceTextureAvailable(surface, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            if (originalListener != null) {
                originalListener.onSurfaceTextureSizeChanged(surface, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return (originalListener == null) || originalListener.onSurfaceTextureDestroyed(surface);
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            if (originalListener != null) {
                originalListener.onSurfaceTextureUpdated(surface);
            }
            if (fpsTracker != null) {
                fpsTracker.onFrameAvailable();
            }
        }
    }
}