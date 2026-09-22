package com.zsp.today.module.camera.function;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalLensFacing;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.zsp.today.module.camera.LogKit;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created on 2026/9/21.
 *
 * @author 郑少鹏
 * @desc 相机设备配套原件
 */
public class CameraDeviceKit {
    /**
     * constructor
     * <p>
     * 私有构造函数 + 防止实例化
     */
    private CameraDeviceKit() {

    }

    /**
     * 检测是否为 UVC 高拍仪
     *
     * @param context               上下文
     * @param processCameraProvider 生命周期绑定提供者
     * @param cameraId              相机 ID
     * @return 是否为 UVC 高拍仪
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public static boolean checkIsUvcCamera(@NonNull Context context, @Nullable ProcessCameraProvider processCameraProvider, String cameraId) {
        if ((cameraId == null) || cameraId.isEmpty()) {
            return false;
        }
        ProcessCameraProvider provider = processCameraProvider;
        if (provider == null) {
            try {
                // 使用全局 Application Context -> 规避内存泄漏
                // 若当前代码运行在主线程，强烈建议由外部传入已初始化好的 processCameraProvider，避免在此调用 .get() 造成主线程死锁。
                provider = ProcessCameraProvider.getInstance(context.getApplicationContext()).get();
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).w(e, "获取 ProcessCameraProvider 失败 -> 无法精准检测 LENS_FACING");
                return false;
            }
        }
        // 构建精确匹配指定 cameraId 的选择器
        final ProcessCameraProvider finalProvider = provider;
        CameraSelector cameraSelector = new CameraSelector.Builder().addCameraFilter(new CameraFilter() {
            @Override
            public @org.jspecify.annotations.NonNull List<CameraInfo> filter(@org.jspecify.annotations.NonNull List<CameraInfo> cameraInfos) {
                List<CameraInfo> result = new ArrayList<>();
                for (CameraInfo cameraInfo : cameraInfos) {
                    try {
                        if (Camera2CameraInfo.from(cameraInfo).getCameraId().equals(cameraId)) {
                            result.add(cameraInfo);
                        }
                    } catch (Exception e) {
                        Timber.tag(LogKit.TAG).d(e, "获取 Camera2CameraInfo 或匹配相机 ID 失败 -> 跳过当前 CameraInfo");
                    }
                }
                return result;
            }
        }).build();
        return checkIsUvcCamera(finalProvider, cameraSelector);
    }

    /**
     * 检测是否为 UVC 高拍仪
     *
     * @param processCameraProvider 生命周期绑定提供者
     * @param cameraSelector        相机选择器
     * @return 是否为 UVC 高拍仪
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    public static boolean checkIsUvcCamera(@Nullable ProcessCameraProvider processCameraProvider, @Nullable CameraSelector cameraSelector) {
        if ((processCameraProvider == null) || (cameraSelector == null)) {
            return false;
        }
        try {
            List<CameraInfo> cameraInfos = cameraSelector.filter(processCameraProvider.getAvailableCameraInfos());
            if (!cameraInfos.isEmpty()) {
                int lensFacing = cameraInfos.get(0).getLensFacing();
                return ((lensFacing == CameraSelector.LENS_FACING_EXTERNAL) || (lensFacing == CameraSelector.LENS_FACING_UNKNOWN));
            }
        } catch (Exception e) {
            Timber.tag(LogKit.TAG).w(e, "判断 UVC 相机类型失败，默认按普通设备处理。");
        }
        return false;
    }
}