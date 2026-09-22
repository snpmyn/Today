package com.zsp.today.module.camera.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalLensFacing;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.zsp.today.module.camera.LogKit;
import com.zsp.today.module.camera.function.callback.CameraCaptureCallback;
import com.zsp.today.module.camera.function.callback.CameraInitCallback;
import com.zsp.today.module.camera.function.config.CameraConfig;
import com.zsp.today.module.camera.function.config.CameraConfigKit;
import com.zsp.today.module.camera.function.other.FpsTracker;
import com.zsp.today.module.camera.function.value.EnhanceMode;

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
     * 相机预览配套原件
     */
    private final CameraPreviewKit cameraPreviewKit;
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
     * 是否为 UVC 高拍仪
     */
    private boolean isUvcCamera = false;
    /**
     * 帧率追踪器
     */
    private FpsTracker fpsTracker;

    /**
     * constructor
     */
    public CameraController() {
        // 相机预览配套原件
        this.cameraPreviewKit = new CameraPreviewKit();
        // 增强实现
        this.executorService = Executors.newSingleThreadExecutor();
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
     * @param cameraId                 目标相机 ID
     * @param resolution               分辨率
     * @param rotation                 旋转角度
     * @param cameraInitCallback       相机初始回调
     */
    @SuppressLint("WrongConstant")
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull View previewViewContainerView, @NonNull PreviewView previewView, String cameraId, Size resolution, Integer rotation, CameraInitCallback cameraInitCallback) {
        // 帧率追踪器
        if (fpsTracker != null) {
            fpsTracker.reset();
        }

        // 确保线程池可用
        ensureExecutorAvailable();

        // 清除帧缓存
        CaptureHelper.clearFrameCache();

        // 当前相机配置
        this.currentCameraConfig = new CameraConfig.Builder().setCameraId(cameraId).setResolution(resolution).setRotation(rotation).setEnhanceMode(EnhanceMode.DOCUMENT).build();

        // 相机提供者异步任务
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                // 构建相机选择器
                CameraSelector cameraSelector;
                // 获取相机配置相机 ID
                String cameraIdFromCameraConfig = currentCameraConfig.getCameraId();
                if ((cameraIdFromCameraConfig != null) && !cameraIdFromCameraConfig.isEmpty()) {
                    // 已指定 CameraID 则根据 CameraID 精确过滤
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(new CameraFilter() {
                        @Override
                        public @org.jspecify.annotations.NonNull List<CameraInfo> filter(@org.jspecify.annotations.NonNull List<CameraInfo> cameraInfos) {
                            List<CameraInfo> result = new ArrayList<>();
                            for (CameraInfo cameraInfo : cameraInfos) {
                                try {
                                    String id = Camera2CameraInfo.from(cameraInfo).getCameraId();
                                    if (id.equals(cameraIdFromCameraConfig)) {
                                        result.add(cameraInfo);
                                        break;
                                    }
                                } catch (Exception e) {
                                    Timber.tag(LogKit.TAG).w(e, "解析 CameraInfo 的 Camera2 ID 失败: %s", cameraInfo);
                                }
                            }
                            // 兜底校验
                            // 指定 CameraID 匹配失败 (如设备拔出) 时降级走自定义优先级过滤，防止返回空列表抛出异常。
                            if (result.isEmpty()) {
                                Timber.tag(LogKit.TAG).w("未匹配到 CameraID 为 [%s] 的摄像头，降级使用默认优先级匹配规则", cameraIdFromCameraConfig);
                                return filterCamera(cameraInfos);
                            }
                            return result;
                        }
                    }).build();
                } else {
                    // 兜底校验
                    // 未指定 CameraID 则降级走自定义优先级过滤
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(this::filterCamera).build();
                }

                // 检测是否为 UVC 高拍仪
                this.isUvcCamera = CameraDeviceKit.checkIsUvcCamera(processCameraProvider, cameraSelector);

                // 获取相机配置旋转角度
                int rotationFromCameraConfig = currentCameraConfig.getRotation();

                // 1. 构建全局统一 ResolutionSelector 分辨率选择器
                // 供 Preview、ImageCapture 与 ImageAnalysis 同步共享
                Size resolutionFromCameraConfig = currentCameraConfig.getResolution();
                ResolutionStrategy resolutionStrategy = (resolutionFromCameraConfig != null) ? new ResolutionStrategy(resolutionFromCameraConfig, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER) : ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY;
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder().setResolutionStrategy(resolutionStrategy).build();

                // 2. 配置 PreviewView 渲染模式与缩放策略
                cameraPreviewKit.applyPreviewConfig(previewView);

                // 3. 构建 Preview 预览用例
                preview = new Preview.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(rotationFromCameraConfig).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // 4. 构建 ImageCapture 拍照用例
                imageCapture = new ImageCapture.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(rotationFromCameraConfig).setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setJpegQuality(100).build();
                // 5. 构建 ImageAnalysis 帧数据分析用例
                imageAnalysis = new ImageAnalysis.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(rotationFromCameraConfig).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

                // 6. 传入 isUvcCamera 标识
                // 若为 UVC 高拍仪则直接由 CaptureHelper 内部按对应偏置解析 Buffer
                imageAnalysis.setAnalyzer(executorService, image -> CaptureHelper.updateLatestFrame(image, isUvcCamera));

                // 7. 解绑并重新绑定生命周期
                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis);

                // 8. 初始化帧率追踪器代理
                cameraPreviewKit.setupFpsTrackerProxy(previewView, fpsTracker);

                // 9. 更新预览视图容器宽高比
                cameraPreviewKit.updatePreviewContainerRatio(previewViewContainerView, resolutionFromCameraConfig);

                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitSuccess();
                }

                Timber.tag(LogKit.TAG).i("CameraX 启动成功:\n" + //
                                "├─ Camera ID: %s\n" + //
                                "├─ 旋转角度: %d\n" + //
                                "├─ 分辨率: %s\n" + //
                                "├─ 是否为 UVC 高拍仪: %b\n" + //
                                "└─ 用例绑定状态: [Preview: %b, ImageCapture: %b, ImageAnalysis: %b]", //
                        cameraIdFromCameraConfig, //
                        rotationFromCameraConfig, //
                        (resolutionFromCameraConfig != null) ? resolutionFromCameraConfig.toString() : "无分辨率", //
                        isUvcCamera, //
                        preview != null, //
                        imageCapture != null, //
                        imageAnalysis != null //
                );
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "CameraX 启动失败: %s", e.getMessage());
                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitError(e);
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * 设置分辨率
     *
     * @param context                  上下文
     * @param lifecycleOwner           生命周期拥有者
     * @param previewViewContainerView 预览视图容器
     * @param previewView              预览视图
     * @param resolution               分辨率
     * @param rotation                 旋转角度
     */
    public void setResolution(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull View previewViewContainerView, @NonNull PreviewView previewView, @NonNull Size resolution, @NonNull Integer rotation) {
        // 1. 捕获当前预览帧
        // 构建防黑屏静态遮罩
        Bitmap maskBitmap = previewView.getBitmap();
        ImageView maskImageView = null;
        if ((maskBitmap != null) && !maskBitmap.isRecycled() && (previewViewContainerView instanceof ViewGroup)) {
            ViewGroup container = (ViewGroup) previewViewContainerView;
            maskImageView = new ImageView(previewView.getContext());
            maskImageView.setImageBitmap(maskBitmap);
            maskImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            // 添加遮罩至容器
            container.addView(maskImageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // 强制将遮罩移至 View 树最顶层
            // 防止被 PreviewView 的 Surface / Texture 层穿透
            maskImageView.bringToFront();
        }
        final ImageView finalMaskImageView = maskImageView;
        // 2. 启动相机
        String currentCameraId = (currentCameraConfig != null) ? currentCameraConfig.getCameraId() : null;
        startCamera(context, lifecycleOwner, previewViewContainerView, previewView, currentCameraId, resolution, rotation, new CameraInitCallback() {
            @Override
            public void onCameraInitSuccess() {
                // 3. 延时掩盖硬件与算法收敛期
                if (finalMaskImageView != null) {
                    // 700ms 完整覆盖
                    // 1. UVC / Camera2 硬件管道重新配置 (Pipe Stream Config)
                    // 2. ISP 3A 算法 (自动对焦 / 曝光 / 白平衡) 收敛首帧
                    // 3. PreviewView 矩阵 (Matrix) 与新分辨率 Buffer 对齐
                    previewView.postDelayed(() -> finalMaskImageView.animate().alpha(0f).setDuration(150).withEndAction(() -> ((ViewGroup) previewViewContainerView).removeView(finalMaskImageView)).start(), 700);
                }
            }

            @Override
            public void onCameraInitError(Throwable throwable) {
                if (finalMaskImageView != null) {
                    ((ViewGroup) previewViewContainerView).removeView(finalMaskImageView);
                }
            }
        });
    }

    /**
     * 设置旋转角度
     *
     * @param previewView    预览视图
     * @param targetRotation 旋转角度
     *                       [Surface.ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270]
     */
    public void setRotation(@NonNull PreviewView previewView, int targetRotation) {
        // 1. 存储旋转角度
        String cameraId = (currentCameraConfig != null) ? currentCameraConfig.getCameraId() : null;
        CameraConfigKit.saveRotation(cameraId, targetRotation);
        // 2. 更新相机配置
        if (currentCameraConfig != null) {
            currentCameraConfig = new CameraConfig.Builder().setCameraId(currentCameraConfig.getCameraId()).setResolution(currentCameraConfig.getResolution()).setRotation(targetRotation).setEnhanceMode(currentCameraConfig.getEnhanceMode()).build();
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
        // 4. 重新链接 SurfaceProvider
        // 预览视图矩阵变换生效
        if (preview != null) {
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
        }
    }

    /**
     * 过滤相机
     * <p>
     * 优先级顺序
     * 1. 外接摄像头 - LENS_FACING_EXTERNAL
     * 适用标准 UVC 高拍仪
     * 2. 朝向未知摄像头 - LENS_FACING_UNKNOWN
     * 兼容未按标准实现驱动的定制硬件
     * 3. 相机列表最后一个摄像头
     * 作为异常硬件架构下兜底选项
     *
     * @param cameraInfos 相机信息列表
     * @return 过滤后的相机信息列表
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
        CaptureHelper.capture(context, imageCapture, previewView, isUvcCamera, executorService, currentCameraConfig.getEnhanceMode(), cameraCaptureCallback);
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
     * 释放
     *
     * @param previewView 预览视图
     */
    public void release(@Nullable PreviewView previewView) {
        // 1. 帧率追踪器
        if (fpsTracker != null) {
            fpsTracker.reset();
        }
        // 2. 释放
        cameraPreviewKit.release(previewView);
        // 3. 图像分析用例对象
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
            imageAnalysis = null;
        }
        // 4. 预览用例对象
        preview = null;
        // 5. 抓拍用例对象
        imageCapture = null;
        // 6. 生命周期绑定提供者
        if (processCameraProvider != null) {
            processCameraProvider.unbindAll();
            processCameraProvider = null;
        }
        // 7. 增强实现
        if ((executorService != null) && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        // 8. 清除帧缓存
        CaptureHelper.clearFrameCache();
    }
}