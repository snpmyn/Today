package com.zsp.today.module.camera.function;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.zsp.today.module.camera.LogKit;
import com.zsp.today.module.camera.media.MediaScanKit;
import com.zsp.today.module.camera.storage.MediaFileNameEngine;
import com.zsp.today.module.camera.storage.MediaStorageConfig;
import com.zsp.today.module.camera.storage.MediaStorageType;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import timber.log.Timber;

/**
 * @decs: 拍照辅助者
 * @author: 郑少鹏
 * @date: 2026/9/18 15:08
 * @version: v 1.0
 */
public class CaptureHelper {
    private static final Object FRAME_LOCK = new Object();
    /**
     * 最新帧 YUV 内存数据缓存
     */
    private static byte[] latestYuvBytes;
    /**
     * 最新帧宽
     */
    private static int latestFrameWidth = 0;
    /**
     * 最新帧高
     */
    private static int latestFrameHeight = 0;
    /**
     * 最新帧旋转角度
     */
    private static int latestFrameRotationDegrees = 0;

    /**
     * 重置序号
     */
    public static void resetSequence() {
        MediaFileNameEngine.resetSequence();
    }

    /**
     * 生成保存路径
     *
     * @return 保存路径
     */
    public static @Nullable String generateSavePath() {
        File targetFile = MediaStorageConfig.getInstance().generateSaveFile(MediaStorageType.CAPTURE, null);
        if (targetFile == null) {
            return null;
        }
        File parentDir = targetFile.getParentFile();
        if ((parentDir != null) && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                Timber.tag(LogKit.TAG).e("创建照片存储目录失败 || %s", parentDir.getAbsolutePath());
                return null;
            }
        }
        return targetFile.getAbsolutePath();
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param imageCapture          抓拍用例对象
     * @param previewView           预览视图
     *                              硬件抓拍失败时降级方案
     * @param executorService       增强实现
     * @param cameraCaptureCallback 相机拍照回调
     */
    public static void capture(@NonNull Context context, ImageCapture imageCapture, @NonNull PreviewView previewView, @NonNull ExecutorService executorService, CameraController.CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        if (imageCapture == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_CAMERA_CLOSED, "相机尚未初始化成功", null)));
            }
            return;
        }
        String savePath = generateSavePath();
        if (savePath == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_FILE_IO, "创建照片保存文件失败", null)));
            }
            return;
        }
        File rawPhotoFile = new File(savePath);
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(rawPhotoFile).build();
        // 优先使用 CameraX 硬件传感器抓拍
        imageCapture.takePicture(outputOptions, executorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Timber.tag(LogKit.TAG).d("相机硬件传感器抓拍成功 || %s", rawPhotoFile.getAbsolutePath());
                // 扫描单个文件
                MediaScanKit.scanSingleFile(context, rawPhotoFile.getAbsolutePath(), "image/jpeg");
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(rawPhotoFile));
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(LogKit.TAG).w(exception, "硬件抓拍失败，自动降级至 ImageAnalysis 原始帧抓拍处理。");
                captureFromLatestFrame(context, previewView, rawPhotoFile, executorService, cameraCaptureCallback);
            }
        });
    }

    /**
     * 从最新帧拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param photoFile             照片文件
     * @param executorService       增强实现
     * @param cameraCaptureCallback 相机拍照回调
     */
    private static void captureFromLatestFrame(@NonNull Context context, @NonNull PreviewView previewView, File photoFile, @NonNull ExecutorService executorService, CameraController.CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        executorService.execute(() -> {
            byte[] yuvData;
            int width;
            int height;
            int rotation;
            synchronized (FRAME_LOCK) {
                yuvData = latestYuvBytes;
                width = latestFrameWidth;
                height = latestFrameHeight;
                rotation = latestFrameRotationDegrees;
            }
            if ((yuvData == null) || (width <= 0) || (height <= 0)) {
                Timber.tag(LogKit.TAG).w("帧数据缓存为空，降级至 PreviewView 截屏处理。");
                mainExecutor.execute(() -> captureFromPreviewView(context, previewView, photoFile, executorService, cameraCaptureCallback));
                return;
            }
            try {
                YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width, height), 95, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (rotation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotation);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                    Timber.tag(LogKit.TAG).d("ImageAnalysis 原始帧降级拍照成功 || %s", photoFile.getAbsolutePath());
                    // 扫描单个文件
                    MediaScanKit.scanSingleFile(context, photoFile.getAbsolutePath(), "image/jpeg");
                    if (cameraCaptureCallback != null) {
                        mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(photoFile));
                    }
                }
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "ImageAnalysis 原始帧保存失败，尝试 PreviewView 截屏兜底");
                mainExecutor.execute(() -> captureFromPreviewView(context, previewView, photoFile, executorService, cameraCaptureCallback));
            }
        });
    }

    /**
     * 从预览视图拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param photoFile             照片文件
     * @param executorService       增强实现
     * @param cameraCaptureCallback 相机拍照回调
     */
    private static void captureFromPreviewView(@NonNull Context context, @NonNull PreviewView previewView, File photoFile, @NonNull ExecutorService executorService, CameraController.CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "预览帧获取为空", null)));
            }
            return;
        }
        executorService.execute(() -> {
            try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                Timber.tag(LogKit.TAG).d("PreviewView 截屏降级拍照成功 || %s", photoFile.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                // 扫描单个文件
                MediaScanKit.scanSingleFile(context, photoFile.getAbsolutePath());
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(photoFile));
                }
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "预览截图保存失败");
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "截屏保存异常: " + e.getMessage(), e)));
                }
            }
        });
    }

    /**
     * 更新最新帧
     * <p>
     * 由 ImageAnalysis 实时回调
     *
     * @param imageProxy 图像代理
     */
    public static void updateLatestFrame(@NonNull ImageProxy imageProxy) {
        try (imageProxy) {
            if (imageProxy.getFormat() == ImageFormat.YUV_420_888) {
                synchronized (FRAME_LOCK) {
                    latestFrameWidth = imageProxy.getWidth();
                    latestFrameHeight = imageProxy.getHeight();
                    latestFrameRotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                    latestYuvBytes = yuv420888ToNv21(imageProxy);
                }
            }
        } catch (Exception e) {
            Timber.tag(LogKit.TAG).e(e, "更新帧数据缓存异常");
        }
    }

    /**
     * 将 YUV_420_888 格式的 ImageProxy 转为 NV21 字节数组
     *
     * @param imageProxy 图像代理
     * @return NV21 字节数组
     */
    @NonNull
    private static byte[] yuv420888ToNv21(@NonNull ImageProxy imageProxy) {
        ImageProxy.PlaneProxy[] planeProxies = imageProxy.getPlanes();
        ByteBuffer yBuffer = planeProxies[0].getBuffer();
        ByteBuffer uBuffer = planeProxies[1].getBuffer();
        ByteBuffer vBuffer = planeProxies[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }

    /**
     * 清除帧缓存
     */
    public static void clearFrameCache() {
        synchronized (FRAME_LOCK) {
            latestYuvBytes = null;
            latestFrameWidth = 0;
            latestFrameHeight = 0;
            latestFrameRotationDegrees = 0;
        }
    }
}