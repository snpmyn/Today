package com.zsp.today.module.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Rational;
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
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
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
 * @desc 相机管理器
 */
public class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();

    /**
     * 异步单线程池，用于处理磁盘 IO 保存图片，避免阻塞 UI 线程
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
     * 当前选中的相机 ID
     */
    private String currentCameraId = null;

    /**
     * 当前设定的预览/拍照分辨率
     */
    private Size currentResolution = null;

    /**
     * 构造方法，初始化单线程池
     */
    public CameraManager() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取系统底层注册的所有相机 ID
     *
     * @param context 上下文
     * @return 系统底层注册的所有相机 ID 列表
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
     * 获取指定相机 ID 支持的原生分辨率列表，按分辨率从大到小排序
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
     * 启动相机并绑定生命周期
     *
     * @param context            上下文
     * @param lifecycleOwner     生命周期拥有者
     * @param previewView        预览视图控件
     * @param cameraId           目标相机 ID
     * @param resolution         设定分辨率
     * @param cameraInitCallback 相机初始化结果回调
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull PreviewView previewView, String cameraId, Size resolution, CameraInitCallback cameraInitCallback) {
        this.currentCameraId = cameraId;
        this.currentResolution = resolution;
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();

                // 1. 强行指定 TargetRotation 为 ROTATION_0
                // 避免 CameraX 读取 UVC 设备错误的 SENSOR_ORIENTATION 导致内部二次旋转计算
                int targetRotation = Surface.ROTATION_0;

                // 2. 根据选定的分辨率计算宽高比策略 (16:9 或 4:3)
                AspectRatioStrategy aspectRatioStrategy = AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY;
                if (currentResolution != null) {
                    double ratio = (double) Math.max(currentResolution.getWidth(), currentResolution.getHeight()) / Math.min(currentResolution.getWidth(), currentResolution.getHeight());
                    if (Math.abs(ratio - (4.0 / 3.0)) < Math.abs(ratio - (16.0 / 9.0))) {
                        aspectRatioStrategy = AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY;
                    }
                }
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder()
                        .setAspectRatioStrategy(aspectRatioStrategy)
                        .setResolutionStrategy((currentResolution != null) ? new ResolutionStrategy(currentResolution, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER) : ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                        .build();

                // 3. 配置 PreviewView：强制使用 TextureView 模式 (COMPATIBLE)，因为 SurfaceView 不支持 View.setRotation() 矩阵变换
                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);

                // 4. 构建 Preview 预览用例
                Preview preview = new Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setTargetRotation(targetRotation)
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // 5. 构建 ImageCapture 拍照用例
                imageCapture = new ImageCapture.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(targetRotation)
                        .build();

                // 6. 构建 CameraSelector 相机选择器
                CameraSelector cameraSelector;
                if ((currentCameraId != null) && !currentCameraId.isEmpty()) {
                    // 如果指定了 CameraID，根据 CameraID 过滤
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
                    // 否则使用自定义策略过滤（优先外接摄像头）
                    cameraSelector = new CameraSelector.Builder().addCameraFilter(this::filterCamera).build();
                }

                // 7. 构建 ViewPort 强制画面输出比例，规避裁剪拉伸
                int width = (currentResolution != null) ? currentResolution.getWidth() : 16;
                int height = (currentResolution != null) ? currentResolution.getHeight() : 9;
                Rational aspectRatio = new Rational(width, height);
                ViewPort viewPort = new ViewPort.Builder(aspectRatio, targetRotation).setScaleType(ViewPort.FIT).build();

                // 8. 打包用例组并绑定生命周期
                UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                        .addUseCase(preview)
                        .addUseCase(imageCapture)
                        .setViewPort(viewPort)
                        .build();

                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup);

                // 9. 视图层手动纠偏：针对 UVC 外接设备固件角度误报，直接将 View 逆时针旋转 90 度抵消偏转
                previewView.post(() -> previewView.setRotation(-90f));

                Timber.tag(TAG).i("CameraX 启动成功，已应用 -90 度强制视角矫正");
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
     * 优先级说明：
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
            // 优先级 1: 发现标准外接设备，直接返回
            if (lensFacing == CameraSelector.LENS_FACING_EXTERNAL) {
                return Collections.singletonList(info);
            }
            // 优先级 2: 记录第一个未明确朝向的设备作为备选
            else if ((lensFacing == CameraSelector.LENS_FACING_UNKNOWN) && (unknownCam == null)) {
                unknownCam = info;
            }
        }
        // 若没有找到 EXTERNAL，则优先使用 UNKNOWN
        if (unknownCam != null) {
            return Collections.singletonList(unknownCam);
        }
        // 优先级 3: 兜底返回最后一个摄像头
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
                Timber.tag(TAG).w(exception, "硬件抓拍失败，自动降级至 PreviewView 截屏处理");
                ContextCompat.getMainExecutor(context).execute(() -> captureFromPreviewView(previewView, rawPhotoFile, cameraCaptureCallback));
            }
        });
    }

    /**
     * 截取 PreviewView 当前预览画面（作为硬件抓拍失败时的降级方案）
     *
     * @param previewView           预览视图
     * @param photoFile             输出目标文件
     * @param cameraCaptureCallback 拍照结果回调
     */
    private void captureFromPreviewView(@NonNull PreviewView previewView, File photoFile, CameraCaptureCallback cameraCaptureCallback) {
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            if (cameraCaptureCallback != null) {
                cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "Preview 预览帧获取为空", null));
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
     * 释放资源（如关闭单线程池）
     */
    public void release() {
        if ((executorService != null) && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 相机初始化结果回调接口
     */
    public interface CameraInitCallback {
        /**
         * 相机初始化成功回调
         */
        void onCameraInitSuccess();

        /**
         * 相机初始化失败回调
         *
         * @param throwable 异常信息
         */
        void onCameraInitError(Throwable throwable);
    }

    /**
     * 相机拍照结果回调接口
     */
    public interface CameraCaptureCallback {
        /**
         * 拍照成功回调
         *
         * @param photoFile 生成的照片文件
         */
        void onCameraCaptureSuccess(File photoFile);

        /**
         * 拍照异常回调
         *
         * @param imageCaptureException 拍照异常对象
         */
        void onCameraCaptureError(ImageCaptureException imageCaptureException);
    }
}