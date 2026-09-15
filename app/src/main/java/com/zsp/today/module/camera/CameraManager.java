package com.zsp.today.module.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;

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
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
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
 * @desc 极简相机管理器（仅保留切换摄像头、切换分辨率、预览与原图拍照功能）
 */
public class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();
    /**
     * 增强实现
     * <p>
     * 异步单线程池
     * 用于处理磁盘 IO 保存图片
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
    public CameraManager() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取系统底层注册的所有相机 ID
     *
     * @param context 上下文
     * @return 系统底层注册的所有相机 ID
     */
    public List<String> getAvailableCameraIds(@NonNull Context context) {
        List<String> cameraIdList = new ArrayList<>();
        android.hardware.camera2.CameraManager cameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
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
        android.hardware.camera2.CameraManager cameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (streamConfigurationMap != null) {
                    Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                    if ((sizes != null) && (sizes.length > 0)) {
                        List<Size> list = Arrays.asList(sizes);
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
     * @param context            上下文
     * @param lifecycleOwner     生命周期拥有者
     * @param previewView        预览视图
     * @param cameraId           相机 ID
     * @param resolution         分辨率
     * @param cameraInitCallback 相机初始回调
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull PreviewView previewView, String cameraId, Size resolution, CameraInitCallback cameraInitCallback) {
        this.currentCameraId = cameraId;
        this.currentResolution = resolution;
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                // 1. 获取选定摄像头的传感器物理角度
                int sensorOrientation = 0;
                if ((currentCameraId != null) && !currentCameraId.isEmpty()) {
                    android.hardware.camera2.CameraManager cameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    if (cameraManager != null) {
                        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(currentCameraId);
                        Integer orient = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        if (orient != null) {
                            sensorOrientation = orient;
                        }
                    }
                }
                // 2. 获取屏幕当前实际旋转方向
                int targetRotation = getTargetRotation(previewView, sensorOrientation);
                // 4. 根据分辨率配置 AspectRatio Strategy
                AspectRatioStrategy aspectRatioStrategy = AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY;
                if (currentResolution != null) {
                    double ratio = (double) Math.max(currentResolution.getWidth(), currentResolution.getHeight()) / Math.min(currentResolution.getWidth(), currentResolution.getHeight());
                    if (Math.abs(ratio - (4.0 / 3.0)) < Math.abs(ratio - (16.0 / 9.0))) {
                        aspectRatioStrategy = AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY;
                    }
                }
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder().setAspectRatioStrategy(aspectRatioStrategy).setResolutionStrategy((currentResolution != null) ? new ResolutionStrategy(currentResolution, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER) : ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY).build();
                // 5. PreviewView 兼容模式配置
                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
                Preview preview = new Preview.Builder().setResolutionSelector(resolutionSelector).setTargetRotation(targetRotation).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder().setResolutionSelector(resolutionSelector).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(targetRotation).build();
                // 绑定生命周期
                CameraSelector cameraSelector;
                if ((currentCameraId != null) && !currentCameraId.isEmpty()) {
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
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(this::filterCamera).build();
                }
                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
                Timber.tag(TAG).i("CameraX 启动成功，SensorOrientation: %d, TargetRotation: %d", sensorOrientation, targetRotation);
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
     * 获取目标角度
     *
     * @param previewView       预览视图
     * @param sensorOrientation 传感器方向
     * @return 目标角度
     */
    private int getTargetRotation(@NonNull PreviewView previewView, int sensorOrientation) {
        int displayRotation = Surface.ROTATION_0;
        if (previewView.getDisplay() != null) {
            displayRotation = previewView.getDisplay().getRotation();
        }
        // 动态纠偏计算目标角度
        // 如果传感器本身就是 0 度 (标准 UVC / 高拍仪物理方向)
        // 在竖屏 (ROTATION_0) 下设 [目标角度 = ROTATION_90] 可消除 CameraX 强制竖屏裁切
        int targetRotation;
        if ((sensorOrientation == 0) || (sensorOrientation == 180)) {
            // 外接横屏设备
            // 强制告诉 CameraX 当前是横屏视角以防止其主动做 90 度旋转
            targetRotation = Surface.ROTATION_90;
        } else {
            // 标准手机内嵌传感器
            // 使用 Display 方向
            targetRotation = displayRotation;
        }
        return targetRotation;
    }

    /**
     * 过滤相机
     * <p>
     * 按以下优先级选择最佳摄像头
     * 1. 优先选择外接摄像头 (LENS_FACING_EXTERNAL) - 适合标准 USB 高拍仪 / 外接设备
     * 2. 次选朝向未知的摄像头 (LENS_FACING_UNKNOWN) - 兼容驱动不规范的定制硬件
     * 3. 兜底选择列表中的最后一个摄像头 - 规避某些特定硬件架构识别异常的问题
     *
     * @param cameraInfos 相机信息列表
     * @return 相机信息列表
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    private List<CameraInfo> filterCamera(List<CameraInfo> cameraInfos) {
        if ((cameraInfos == null) || cameraInfos.isEmpty()) {
            return Collections.emptyList();
        }
        CameraInfo unknownCam = null;
        // 单次循环完成过滤
        // 优先查找 EXTERNAL 且同时记录第一个 UNKNOWN 作为备选
        for (CameraInfo info : cameraInfos) {
            int lensFacing = info.getLensFacing();
            // 优先级 1
            // 找到标准外接设备 -> 直接返回
            if (lensFacing == CameraSelector.LENS_FACING_EXTERNAL) {
                return Collections.singletonList(info);
            }
            // 优先级 2
            // 记录第一个未识别朝向的设备
            // 暂不直接返回
            // 防止后续有 EXTERNAL
            else if ((lensFacing == CameraSelector.LENS_FACING_UNKNOWN) && (unknownCam == null)) {
                unknownCam = info;
            }
        }
        // 匹配优先级 2
        // 返回发现的 UNKNOWN 设备
        if (unknownCam != null) {
            return Collections.singletonList(unknownCam);
        }
        // 优先级 3 (兜底)
        // 返回列表中最后一个摄像头
        // 通常为副摄或未排序的外接设备
        return Collections.singletonList(cameraInfos.get(cameraInfos.size() - 1));
    }

    /**
     * 拍照并保存原图
     */
    public void capture(@NonNull Context context, @NonNull PreviewView previewView, CameraCaptureCallback cameraCaptureCallback) {
        if (imageCapture == null) {
            if (cameraCaptureCallback != null) {
                cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_CAMERA_CLOSED, "相机尚未初始化成功", null));
            }
            return;
        }
        File outputDirectory = context.getExternalFilesDir(null);
        if (outputDirectory == null) {
            outputDirectory = context.getFilesDir();
        }
        File rawPhotoFile = new File(outputDirectory, "Scan_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(rawPhotoFile).build();
        // 优先使用硬件抓拍
        imageCapture.takePicture(outputOptions, executorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                if (cameraCaptureCallback != null) {
                    cameraCaptureCallback.onCameraCaptureSuccess(rawPhotoFile);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(TAG).w(exception, "硬件抓拍失败，自动降级至 PreviewView 截屏");
                ContextCompat.getMainExecutor(context).execute(() -> captureFromPreviewView(previewView, rawPhotoFile, cameraCaptureCallback));
            }
        });
    }

    /**
     * 预览拍照
     * <p>
     * 降级方案
     *
     * @param previewView           预览视图
     * @param photoFile             照片文件
     * @param cameraCaptureCallback 拍照回调
     */
    private void captureFromPreviewView(@NonNull PreviewView previewView, File photoFile, CameraCaptureCallback cameraCaptureCallback) {
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            if (cameraCaptureCallback != null) {
                cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "Preview 预览帧获取为空", null));
            }
            return;
        }
        executorService.execute(() -> {
            try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                if (cameraCaptureCallback != null) {
                    cameraCaptureCallback.onCameraCaptureSuccess(photoFile);
                }
            } catch (Exception e) {
                Timber.tag(TAG).e(e, "Preview 截图保存失败");
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
         * @param throwable Throwable
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
         * @param imageCaptureException ImageCaptureException
         */
        void onCameraCaptureError(ImageCaptureException imageCaptureException);
    }
}