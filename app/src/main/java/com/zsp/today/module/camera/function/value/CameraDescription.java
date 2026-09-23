package com.zsp.today.module.camera.function.value;

import android.hardware.camera2.CameraCharacteristics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 2026/9/22.
 *
 * @author 郑少鹏
 * @desc 相机描述
 */
@SuppressWarnings("unused")
public class CameraDescription {
    /**
     * 相机 ID
     */
    private final String cameraId;
    /**
     * 镜头朝向
     * <p>
     * -1 - 未知
     * <p>
     * 0 - 内置前置
     * {@link CameraCharacteristics#LENS_FACING_FRONT}
     * <p>
     * 1 - 内置后置
     * {@link CameraCharacteristics#LENS_FACING_BACK}
     * <p>
     * 2 - UVC 外接
     * {@link CameraCharacteristics#LENS_FACING_EXTERNAL}
     */
    private final int lensFacing;
    /**
     * 展示名称
     */
    private final String displayName;
    /**
     * USB 设备产品名称
     */
    private final String usbDeviceProductName;

    /**
     * constructor
     *
     * @param cameraId             相机 ID
     * @param lensFacing           镜头朝向
     * @param displayName          展示名称
     * @param usbDeviceProductName USB 设备产品名称
     */
    public CameraDescription(@NonNull String cameraId, int lensFacing, @NonNull String displayName, @Nullable String usbDeviceProductName) {
        this.cameraId = cameraId;
        this.lensFacing = lensFacing;
        this.displayName = displayName;
        this.usbDeviceProductName = usbDeviceProductName;
    }

    @NonNull
    public String getCameraId() {
        return cameraId;
    }

    public int getLensFacing() {
        return lensFacing;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getUsbDeviceProductName() {
        return usbDeviceProductName;
    }

    @NonNull
    @Override
    public String toString() {
        return "CameraDescription{" + "cameraId='" + cameraId + '\'' + ", lensFacing=" + lensFacing + ", displayName='" + displayName + '\'' + ", usbProductName='" + usbDeviceProductName + '\'' + '}';
    }
}