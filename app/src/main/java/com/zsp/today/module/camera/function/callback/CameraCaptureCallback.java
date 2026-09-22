package com.zsp.today.module.camera.function.callback;

import androidx.camera.core.ImageCaptureException;

import java.io.File;

/**
 * Created on 2026/9/22.
 *
 * @author 郑少鹏
 * @desc 相机拍照回调
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