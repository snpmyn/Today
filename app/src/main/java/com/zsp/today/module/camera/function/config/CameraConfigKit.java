package com.zsp.today.module.camera.function.config;

import android.view.Display;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;

import com.zsp.today.module.camera.function.value.CameraConstant;

import util.mmkv.MmkvKit;

/**
 * Created on 2026/9/15.
 *
 * @author 郑少鹏
 * @desc 相机配置配套原件
 */
public class CameraConfigKit {
    /**
     * 获取指定相机 ID 对应 MMKV 旋转角度存储 Key
     *
     * @param cameraId 相机 ID
     * @return 指定相机 ID 对应 MMKV 旋转角度存储 Key
     */
    public static String getRotationMmkvKey(String cameraId) {
        if ((cameraId == null) || cameraId.isEmpty()) {
            return CameraConstant.CAMERA_$_TARGET_ROTATION;
        }
        return (CameraConstant.CAMERA_$_TARGET_ROTATION + "_" + cameraId);
    }

    /**
     * 存储目标旋转角度
     *
     * @param cameraId       相机 ID
     * @param targetRotation 目标旋转角度
     */
    public static void saveTargetRotation(String cameraId, int targetRotation) {
        MmkvKit.defaultMmkv().encode(getRotationMmkvKey(cameraId), targetRotation);
    }

    /**
     * 决断指定相机 ID 最终生效目标旋转角度
     *
     * @param cameraId    相机 ID
     * @param previewView 预览视图
     * @param isUvcCamera 是否为 UVC 高拍仪设备
     * @return 指定相机 ID 最终生效目标旋转角度
     */
    public static int resolveTargetRotation(String cameraId, @NonNull PreviewView previewView, boolean isUvcCamera) {
        String mmkvKey = getRotationMmkvKey(cameraId);
        // 1. 优先读取 MMKV 中特定相机持久化配置
        if (MmkvKit.defaultMmkv().containsKey(mmkvKey)) {
            return MmkvKit.defaultMmkv().decodeInt(mmkvKey, Surface.ROTATION_0);
        }
        // 2. UVC 高拍仪设备默认 0°
        if (isUvcCamera) {
            return Surface.ROTATION_0;
        }
        // 3. 手机内置摄像头默认官方 Display 旋转角度
        Display display = previewView.getDisplay();
        return (display != null) ? display.getRotation() : Surface.ROTATION_0;
    }
}