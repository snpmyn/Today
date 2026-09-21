package com.zsp.today.module.camera.function;

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
import androidx.camera.core.ImageAnalysis;
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
import com.zsp.today.module.camera.LogKit;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;
import util.mmkv.MmkvKit;

/**
 * Created on 2026/9/11.
 *
 * @author 郑少鹏
 * @desc 相机控制器
 */
public class CameraController {
    /**
     * 增强实现
     */
    private ExecutorService executorService;
    /**
     * 预览用例对象
     */
    private Preview preview;
    /**
     * 抓拍用例对象
     */
    private ImageCapture imageCapture;
    /**
     * 图像分析用例对象
     */
    private ImageAnalysis imageAnalysis;
    /**
     * 生命周期绑定提供者
     */
    private ProcessCameraProvider processCameraProvider;
    /**
     * 当前相机配置
     */
    private CameraConfig currentCameraConfig = null;
    /**
     * 是否为 UVC 高拍仪设备
     */
    private boolean isUvcCamera = false;
    /**
     * 帧率追踪器
     */
    private FpsTracker fpsTracker;
    /**
     * 绑定的 PreviewView 弱引用
     */
    private WeakReference<PreviewView> previewViewWeakReference;
    /**
     * View 布局改变监听器
     */
    private View.OnLayoutChangeListener layoutChangeListener;

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
     * <p>
     * 按总像素量降序排列
     *
     * @param context  上下文
     * @param cameraId 相机 ID
     * @return 指定相机 ID 支持的原生分辨率列表
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
        // 优先读取持久化配置的角度，若无则使用窗口 Display 旋转角。
        int savedRotation = MmkvKit.defaultMmkv().decodeInt(CameraConstant.CAMERA_$_TARGET_ROTATION, Surface.ROTATION_0);
        int displayRotation = (previewView.getDisplay() != null) ? previewView.getDisplay().getRotation() : Surface.ROTATION_0;
        int targetRotation = (savedRotation != Surface.ROTATION_0) ? savedRotation : displayRotation;
        // 相机配置
        CameraConfig cameraConfig = new CameraConfig.Builder().setCameraId(cameraId).setResolution(resolution).setTargetRotation(targetRotation).build();
        startCamera(context, lifecycleOwner, previewViewContainerView, previewView, cameraConfig, cameraInitCallback);
    }

    /**
     * 启动相机
     * <p>
     * 本方法会绑定并配置两条不同的相机硬件工作管道 (Stream Pipelines)
     * <p>
     * 1. 硬件拍照流 (ImageCapture)
     * 走底层硬件传感器抓拍，调用硬件 ISP 深度算法 (HDR / 超分辨率 / 降噪)，画质高且高频纹理丰富，生成文件较大。
     * <p>
     * 2. 预览 / 帧分析流 (ImageAnalysis)
     * 走视频流管道，为保障 30 FPS 高帧率，ISP 会进行实时平滑与降噪处理，生成的文件体积较小，但可实现无延迟抓拍。
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
        // 清除帧缓存
        CaptureHelper.clearFrameCache();
        // 保证启动相机时后台工作线程池可用
        ensureExecutorAvailable();
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                // 构建 CameraSelector 相机选择器
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
                // 检查并记录当前绑定是否为 UVC 高拍仪设备
                this.isUvcCamera = checkIsUvcCamera(processCameraProvider, cameraSelector);
                // 获取配置的目标旋转角度
                int effectiveTargetRotation = currentCameraConfig.getTargetRotation();
                // 1. 构建全局统一 ResolutionSelector 分辨率选择器
                // 供 Preview、ImageCapture 与 ImageAnalysis 同步共享
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
                //    等比放大画面以完全覆盖 View。在外层 CardView 已锁定图像原生宽高比的前提下，以极微小裁切 (容错) 抵消亚像素黑边。
                // ==================================================================================================================================================
                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
                // 3. 构建 Preview 预览用例
                preview = new Preview.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(effectiveTargetRotation).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // 4. 构建 ImageCapture 拍照用例
                // ==================================================================================================================================================
                // [硬件拍照流 (ImageCapture)]
                // 触发底层传感器硬件重新曝光与硬件 ISP 算法 (HDR、空间降噪、超分辨率重构、边缘锐化)
                // 特性：画质极高，保留极多细节与微小噪点，导出文件较大 (通常 3MB ~ 10MB+)，存在毫秒级硬件抓拍延迟。
                // ==================================================================================================================================================
                imageCapture = new ImageCapture.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(effectiveTargetRotation).setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setJpegQuality(100).build();
                // 5. 构建 ImageAnalysis 帧数据分析用例
                // 强制使用与 Preview 与 ImageCapture 完全一致的全局 ResolutionSelector
                // 保证降级抓拍帧数据时输出当前配置的真实全高清 / 原生的实际分辨率
                // ==================================================================================================================================================
                // [预览 / 帧分析流 (ImageAnalysis)]
                // 为保障 30 FPS 实时性，ISP 仅进行轻量实时降噪和平滑处理。持续输出原始 YUV_420_888 视频帧。
                // 特性：画面高频噪点少、平滑度高，抓拍导出 JPEG 文件时具备更高的压缩率 (文件较小约 900KB ~ 1.5MB) 且具备零延迟抓拍优势。
                // ==================================================================================================================================================
                imageAnalysis = new ImageAnalysis.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(effectiveTargetRotation).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                // 传入 isUvcCamera 标识
                // 若为 UVC 设备则直接由 CaptureHelper 内部按对应偏置解析 Buffer
                imageAnalysis.setAnalyzer(executorService, image -> CaptureHelper.updateLatestFrame(image, isUvcCamera));
                // 7. 解绑并重新绑定生命周期
                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis);
                // 初始化帧率追踪器
                setupFpsTracker(previewView);
                // 根据选定分辨率及有效旋转角动态更新 ConstraintLayout 容器宽高比
                // 确保图像无形变且居中
                updatePreviewContainerRatio(previewViewContainerView, resolution, effectiveTargetRotation);
                Timber.tag(LogKit.TAG).i("CameraX 启动成功 - 旋转角度: %d, isUvc: %b", effectiveTargetRotation, isUvcCamera);
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
     * 设置目标旋转角度
     *
     * @param previewViewContainerView 预览视图容器
     * @param previewView              预览视图
     * @param targetRotation           目标旋转角度
     *                                 [Surface.ROTATION_0 / 90 / 180 / 270]
     */
    public void setTargetRotation(@NonNull View previewViewContainerView, @NonNull PreviewView previewView, int targetRotation) {
        // 1. 持久化存储
        MmkvKit.defaultMmkv().encode(CameraConstant.CAMERA_$_TARGET_ROTATION, targetRotation);
        // 2. 更新内存配置
        if (currentCameraConfig != null) {
            currentCameraConfig = new CameraConfig.Builder().setCameraId(currentCameraConfig.getCameraId()).setResolution(currentCameraConfig.getResolution()).setTargetRotation(targetRotation).build();
        }
        // 3. 动态刷新 CameraX 各用例的 TargetRotation
        if (preview != null) {
            preview.setTargetRotation(targetRotation);
        }
        if (imageCapture != null) {
            imageCapture.setTargetRotation(targetRotation);
        }
        if (imageAnalysis != null) {
            imageAnalysis.setTargetRotation(targetRotation);
        }
        // 4. 重新链接 SurfaceProvider 以使预览视图矩阵变换生效
        if (preview != null) {
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
        }
        // 5. 动态更新 ConstraintLayout 容器宽高比
        if (currentCameraConfig != null) {
            updatePreviewContainerRatio(previewViewContainerView, currentCameraConfig.getResolution(), targetRotation);
        }
    }

    /**
     * 根据选择的分辨率与当前旋转角度动态更新 ConstraintLayout 容器宽高比
     *
     * @param previewViewContainerView 预览视图容器
     * @param resolution               配置的宽高大小
     * @param targetRotation           当前旋转角度
     */
    private void updatePreviewContainerRatio(@NonNull View previewViewContainerView, @Nullable Size resolution, int targetRotation) {
        if (resolution == null) {
            return;
        }
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams layoutParams = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) previewViewContainerView.getLayoutParams();
        // 根据旋转角判断宽高的交换关系
        boolean isRotated = (targetRotation == Surface.ROTATION_90) || (targetRotation == Surface.ROTATION_270);
        int width = isRotated ? resolution.getHeight() : resolution.getWidth();
        int height = isRotated ? resolution.getWidth() : resolution.getHeight();

        layoutParams.dimensionRatio = "H," + width + ":" + height;
        previewViewContainerView.setLayoutParams(layoutParams);
    }

    /**
     * 检测是否为 UVC 高拍仪设备
     *
     * @param processCameraProvider 生命周期绑定提供者
     * @param cameraSelector        目标相机选择器
     * @return 是否为 UVC 高拍仪设备
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    private boolean checkIsUvcCamera(ProcessCameraProvider processCameraProvider, CameraSelector cameraSelector) {
        try {
            List<CameraInfo> cameraInfos = cameraSelector.filter(processCameraProvider.getAvailableCameraInfos());
            if (!cameraInfos.isEmpty()) {
                int lensFacing = cameraInfos.get(0).getLensFacing();
                return ((lensFacing == CameraSelector.LENS_FACING_EXTERNAL) || (lensFacing == CameraSelector.LENS_FACING_UNKNOWN));
            }
        } catch (Exception e) {
            Timber.tag(LogKit.TAG).w(e, "判断 UVC 相机类型失败，默认按普通设备处理。");
        }
        return false;
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
        CameraInfo fallbackCameraInfo = cameraInfos.get(cameraInfos.size() - 1);
        Timber.tag(LogKit.TAG).w("未匹配到标准 EXTERNAL 或 UNKNOWN 类型的摄像头，使用摄像头列表中最后一个节点兜底匹配: CameraInfo = %s", fallbackCameraInfo);
        return Collections.singletonList(fallbackCameraInfo);
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param cameraCaptureCallback 相机拍照回调
     */
    public void capture(@NonNull Context context, @NonNull PreviewView previewView, CameraCaptureCallback cameraCaptureCallback) {
        boolean isExecutorRecreated = ensureExecutorAvailable();
        if (isExecutorRecreated && (imageAnalysis != null)) {
            imageAnalysis.setAnalyzer(executorService, image -> CaptureHelper.updateLatestFrame(image, isUvcCamera));
        }
        CaptureHelper.capture(context, imageCapture, previewView, isUvcCamera, executorService, cameraCaptureCallback);
    }

    /**
     * 确保线程池可用
     *
     * @return 线程池是否可用
     */
    private synchronized boolean ensureExecutorAvailable() {
        if ((executorService == null) || executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
            return true;
        }
        return false;
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
        this.previewViewWeakReference = new WeakReference<>(previewView);
        // 使用 Runnable 延迟至下一帧 UI 消息循环
        // 尝试获取异步挂载的 TextureView
        previewView.post(() -> {
            TextureView textureView = findTextureView(previewView);
            if (textureView != null) {
                TextureView.SurfaceTextureListener originalListener = textureView.getSurfaceTextureListener();
                if (!(originalListener instanceof FpsProxySurfaceTextureListener)) {
                    textureView.setSurfaceTextureListener(new FpsProxySurfaceTextureListener(originalListener));
                }
            } else {
                // 若首帧未成功获取 TextureView
                // 监听 View 树状态再次尝试并缓存 Listener 供销毁时精准移除
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
                            TextureView.SurfaceTextureListener origListener = tv.getSurfaceTextureListener();
                            if (!(origListener instanceof FpsProxySurfaceTextureListener)) {
                                tv.setSurfaceTextureListener(new FpsProxySurfaceTextureListener(origListener));
                            }
                        }
                    }
                };
                previewView.addOnLayoutChangeListener(layoutChangeListener);
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
        // 清理 View 树层级监听
        // 防止内存泄漏
        if ((layoutChangeListener != null) && (previewViewWeakReference != null)) {
            PreviewView previewView = previewViewWeakReference.get();
            if (previewView != null) {
                previewView.removeOnLayoutChangeListener(layoutChangeListener);
            }
            layoutChangeListener = null;
            previewViewWeakReference.clear();
        }
        // 按照依赖倒置顺序先清除 Analyzer 分析器和解绑管道
        // 防止关闭 executorService 后仍触发 Task 回调导致 RejectedExecutionException
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
            imageAnalysis = null;
        }
        preview = null;
        imageCapture = null;
        if (processCameraProvider != null) {
            processCameraProvider.unbindAll();
            processCameraProvider = null;
        }
        if ((executorService != null) && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        CaptureHelper.clearFrameCache();
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