package com.zsp.today.module.camera;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.zsp.today.module.camera.storage.LogKit;
import com.zsp.today.module.camera.storage.MediaFileNameEngine;
import com.zsp.today.module.camera.storage.MediaStorageConfig;
import com.zsp.today.module.camera.storage.MediaStorageType;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(rawPhotoFile));
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(LogKit.TAG).w(exception, "硬件抓拍失败，自动降级至 PreviewView 截屏处理。");
                mainExecutor.execute(() -> captureFromPreviewView(context, previewView, rawPhotoFile, executorService, cameraCaptureCallback));
            }
        });
    }

    /**
     * 从预览视图拍照
     * <p>
     * 硬件抓拍失败时降级方案
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
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
}