package com.zsp.today.module.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String TAG = CameraController.class.getSimpleName();
    /**
     * 增强实现
     * <p>
     * 异步单线程池
     * 用于处理磁盘 IO 保存图片
     * 避免阻塞 UI 线程
     */
    private final ExecutorService executorService;
    /**
     * CameraX 抓拍用例
     */
    private ImageCapture imageCapture;
    /**
     * CameraX 生命周期绑定提供者
     */
    private ProcessCameraProvider processCameraProvider;
    /**
     * 当前相机 ID
     */
    private String currentCameraId = null;
    /**
     * 当前分辨率
     */
    private Size currentResolution = null;

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
        List<String> cameraIdList = new ArrayList<>();
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String[] cameraIds = cameraManager.getCameraIdList();
                Collections.addAll(cameraIdList, cameraIds);
            } catch (CameraAccessException e) {
                Timber.tag(TAG).e(e, "获取系统 CameraId 失败");
            }
        }
        return cameraIdList;
    }

    /**
     * 获取指定相机 ID 支持的原生分辨率列表
     * <p>
     * 按分辨率从大到小排序
     *
     * @param context  上下文
     * @param cameraId 相机 ID
     * @return 指定相机 ID 支持的原生分辨率列表
     */
    public List<Size> getSupportedResolutions(@NonNull Context context, String cameraId) {
        List<Size> resolutionList = new ArrayList<>();
        if ((cameraId == null) || cameraId.isEmpty()) {
            return resolutionList;
        }
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (streamConfigurationMap != null) {
                    Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                    if ((sizes != null) && (sizes.length > 0)) {
                        List<Size> list = Arrays.asList(sizes);
                        // 按总像素数从大到小降序排列
                        list.sort((s1, s2) -> Integer.compare(s2.getWidth() * s2.getHeight(), s1.getWidth() * s1.getHeight()));
                        resolutionList.addAll(list);
                    }
                }
            } catch (CameraAccessException e) {
                Timber.tag(TAG).e(e, "获取相机 CameraID: %s 支持的分辨率失败", cameraId);
            }
        }
        return resolutionList;
    }

    /**
     * 启动相机
     *
     * @param context                  上下文
     * @param lifecycleOwner           生命周期拥有者
     * @param previewViewContainerView 预览视图容器
     * @param previewView              预览视图控件
     * @param cameraId                 目标相机 ID
     * @param resolution               设定分辨率
     * @param cameraInitCallback       相机初始化结果回调
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull View previewViewContainerView, @NonNull PreviewView previewView, String cameraId, Size resolution, CameraInitCallback cameraInitCallback) {
        this.currentCameraId = cameraId;
        this.currentResolution = resolution;
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                // 1. 构建 ResolutionSelector 分辨率选择器
                ResolutionStrategy resolutionStrategy = (currentResolution != null) ? new ResolutionStrategy(currentResolution, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER) : ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY;
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder().setResolutionStrategy(resolutionStrategy).build();
                // 2. 配置 PreviewView
                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                /*previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);*/
                // 3. 构建 Preview 预览用例
                Preview preview = new Preview.Builder().setResolutionSelector(resolutionSelector).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // 4. 构建 ImageCapture 拍照用例
                imageCapture = new ImageCapture.Builder().setResolutionSelector(resolutionSelector).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
                // 5. 构建 CameraSelector 相机选择器
                CameraSelector cameraSelector;
                if ((currentCameraId != null) && !currentCameraId.isEmpty()) {
                    // 如已指定 CameraID 则根据 CameraID 过滤
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(cameraInfos -> {
                        List<CameraInfo> result = new ArrayList<>();
                        for (CameraInfo cameraInfo : cameraInfos) {
                            String id = Camera2CameraInfo.from(cameraInfo).getCameraId();
                            if (id.equals(currentCameraId)) {
                                result.add(cameraInfo);
                            }
                        }
                        return result;
                    }).build();
                } else {
                    // 否则使用自定义策略过滤
                    // 优先外接摄像头
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(this::filterCamera).build();
                }
                // 6. 解绑并重新绑定生命周期
                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
                // 根据当前切换的分辨率动态调整 MaterialCardView 容器比例
                // 确保在右侧可用范围内缩放且绝不超界
                if (resolution != null) {
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams layoutParams = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) previewViewContainerView.getLayoutParams();
                    layoutParams.dimensionRatio = "H," + resolution.getWidth() + ":" + resolution.getHeight();
                    previewViewContainerView.setLayoutParams(layoutParams);
                }
                Timber.tag(TAG).i("CameraX 启动成功");
                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitSuccess();
                }
            } catch (Exception e) {
                Timber.tag(TAG).e(e, "CameraX 初始化失败");
                if (cameraInitCallback != null) {
                    cameraInitCallback.onCameraInitError(e);
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * 过滤相机策略
     * <p>
     * 优先级说明
     * 1. 优先选择外接摄像头 (LENS_FACING_EXTERNAL) - 适合标准 USB 高拍仪 / UVC 外接设备
     * 2. 次选朝向未知的摄像头 (LENS_FACING_UNKNOWN) - 兼容驱动不规范的硬件设备
     * 3. 兜底选择列表中的最后一个摄像头 - 规避定制硬件架构读取异常的问题
     *
     * @param cameraInfos 可用的相机信息列表
     * @return 过滤出的目标相机信息列表
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    private List<CameraInfo> filterCamera(List<CameraInfo> cameraInfos) {
        if ((cameraInfos == null) || cameraInfos.isEmpty()) {
            return Collections.emptyList();
        }
        CameraInfo unknownCam = null;
        for (CameraInfo info : cameraInfos) {
            int lensFacing = info.getLensFacing();
            // 优先级 1
            // 发现标准外接设备，直接返回。
            if (lensFacing == CameraSelector.LENS_FACING_EXTERNAL) {
                return Collections.singletonList(info);
            }
            // 优先级 2
            // 记录第一个未明确朝向的设备作为备选
            else if ((lensFacing == CameraSelector.LENS_FACING_UNKNOWN) && (unknownCam == null)) {
                unknownCam = info;
            }
        }
        // 没有找到 EXTERNAL 则优先用 UNKNOWN
        if (unknownCam != null) {
            return Collections.singletonList(unknownCam);
        }
        // 优先级 3
        // 兜底返回最后一个摄像头
        return Collections.singletonList(cameraInfos.get(cameraInfos.size() - 1));
    }

    /**
     * 拍照并保存原图文件
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param cameraCaptureCallback 拍照结果回调
     */
    public void capture(@NonNull Context context, @NonNull PreviewView previewView, CameraCaptureCallback cameraCaptureCallback) {
        if (imageCapture == null) {
            if (cameraCaptureCallback != null) {
                cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_CAMERA_CLOSED, "相机尚未初始化成功", null));
            }
            return;
        }
        // 确定保存输出路径
        File outputDirectory = context.getExternalFilesDir(null);
        if (outputDirectory == null) {
            outputDirectory = context.getFilesDir();
        }
        File rawPhotoFile = new File(outputDirectory, "Scan_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(rawPhotoFile).build();
        // 优先使用 CameraX 硬件传感器抓拍
        imageCapture.takePicture(outputOptions, executorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                if (cameraCaptureCallback != null) {
                    cameraCaptureCallback.onCameraCaptureSuccess(rawPhotoFile);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(TAG).w(exception, "硬件抓拍失败，自动降级至 PreviewView 截屏处理。");
                ContextCompat.getMainExecutor(context).execute(() -> captureFromPreviewView(previewView, rawPhotoFile, cameraCaptureCallback));
            }
        });
    }

    /**
     * 截取 PreviewView 当前预览画面
     * <p>
     * 作为硬件抓拍失败时降级方案
     *
     * @param previewView           预览视图
     * @param photoFile             输出目标文件
     * @param cameraCaptureCallback 拍照结果回调
     */
    private void captureFromPreviewView(@NonNull PreviewView previewView, File photoFile, CameraCaptureCallback cameraCaptureCallback) {
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            if (cameraCaptureCallback != null) {
                cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "预览帧获取为空", null));
            }
            return;
        }
        // 异步保存截屏 Bitmap
        executorService.execute(() -> {
            try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                if (cameraCaptureCallback != null) {
                    cameraCaptureCallback.onCameraCaptureSuccess(photoFile);
                }
            } catch (Exception e) {
                Timber.tag(TAG).e(e, "预览截图保存失败");
                if (cameraCaptureCallback != null) {
                    cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "截屏保存异常: " + e.getMessage(), e));
                }
            } finally {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        });
    }

    /**
     * 释放
     */
    public void release() {
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
}