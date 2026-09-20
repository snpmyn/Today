package com.zsp.today.module.camera;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

import timber.log.Timber;

/**
 * @decs: 测试
 * @author: 郑少鹏
 * @date: 2026/9/20 19:54
 * @version: v 1.0
 */
public class SimpleCameraXId120 {
    private static final String TAG = "SimpleCameraX";
    private static final String TARGET_ID = "120";
    private ImageCapture imageCapture;

    /**
     * 一行代码：开启并绑定 ID 为 120 的摄像头
     * <p>
     * 只绑定拍照 UseCase
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public void startCamera(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // 1. 通过 ID 120 创建 Selector
                CameraSelector cameraSelector = new CameraSelector.Builder().addCameraFilter(cameraInfos -> {
                    for (var info : cameraInfos) {
                        String id = Camera2CameraInfo.from(info).getCameraId();
                        if (TARGET_ID.equals(id)) {
                            return java.util.Collections.singletonList(info);
                        }
                    }
                    return java.util.Collections.emptyList();
                }).build();
                // 2. 最简单初始化 ImageCapture
                imageCapture = new ImageCapture.Builder().build();
                // 3. 先解绑
                // 再绑定到生命周期
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture);
                Timber.tag(TAG).d("成功开启并绑定摄像头 ID: %s", TARGET_ID);
            } catch (Exception e) {
                Timber.tag(TAG).e(e, "开启摄像头失败 ID 120: ");
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * 触发官方拍照
     */
    public void takePhoto(@NonNull Context context) {
        if (imageCapture == null) {
            Timber.tag(TAG).e("拍照失败：ImageCapture 未绑定或未初始化");
            return;
        }
        // 保存文件到 cache 目录
        File photoFile = new File(context.getExternalCacheDir(), "test_120.jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        // 调用 CameraX 官方 takePicture 方法
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Timber.tag(TAG).d("拍照成功！文件保存于: %s", photoFile.getAbsolutePath());
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(TAG).e(exception, "拍照失败，错误代码: %s", exception.getImageCaptureError());
            }
        });
    }
}