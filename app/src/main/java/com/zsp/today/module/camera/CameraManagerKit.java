package com.zsp.today.module.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * @decs: 相机管理器配套原件
 * @author: 郑少鹏
 * @date: 2026/9/18 15:07
 * @version: v 1.0
 */
public class CameraManagerKit {
    private static final String TAG = CameraManagerKit.class.getSimpleName();

    /**
     * 获取系统底层注册的所有相机 ID 列表
     *
     * @param context 上下文
     * @return 系统底层注册的所有相机 ID 列表
     */
    @NonNull
    public static List<String> getAvailableCameraIds(@NonNull Context context) {
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
     * 按总像素数从大到小降序排列
     *
     * @param context  上下文
     * @param cameraId 相机 ID
     * @return 指定相机 ID 支持的原生分辨率列表
     */
    @NonNull
    public static List<Size> getSupportedResolutions(@NonNull Context context, String cameraId) {
        List<Size> resolutionList = new ArrayList<>();
        if ((cameraId == null) || cameraId.isEmpty()) {
            return resolutionList;
        }
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
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
}