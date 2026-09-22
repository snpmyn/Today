package com.zsp.today.module.camera.function;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Range;
import android.util.Size;

import androidx.annotation.NonNull;

import com.zsp.today.module.camera.LogKit;
import com.zsp.today.module.camera.function.value.CameraDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;
import util.list.ListUtils;

/**
 * @decs: 相机管理器配套原件
 * @author: 郑少鹏
 * @date: 2026/9/18 15:07
 * @version: v 1.0
 */
@SuppressWarnings("unused")
public class CameraManagerKit {
    /**
     * 获取相机描述列表
     *
     * @param context 上下文
     * @return 相机描述列表
     */
    @NonNull
    public static List<CameraDescription> getCameraDescriptionList(@NonNull Context context) {
        List<CameraDescription> cameraDescriptionList = new ArrayList<>();

        // 相机管理器
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager == null) {
            return cameraDescriptionList;
        }

        // 1. 获取系统底层注册的所有相机 ID 列表
        List<String> cameraIds = getAvailableCameraIds(context);
        if (ListUtils.listIsEmpty(cameraIds)) {
            return cameraDescriptionList;
        }

        // 2. 获取已连接 UVC 设备名列表
        List<String> connectedUsbCameraNames = getConnectedUvcDeviceNameList(context);
        int externalCameraIndex = 0;

        // 3. 遍历组装
        for (String cameraId : cameraIds) {
            try {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                int lensFacing = (facing != null) ? facing : -1;

                String displayName;
                String usbProductName = null;

                switch (lensFacing) {
                    case CameraCharacteristics.LENS_FACING_FRONT:
                        displayName = "内置前置摄像头 [ ID " + cameraId + " ]";
                        break;
                    case CameraCharacteristics.LENS_FACING_BACK:
                        displayName = "内置后置摄像头 [ ID " + cameraId + " ]";
                        break;
                    case CameraCharacteristics.LENS_FACING_EXTERNAL:
                        if (externalCameraIndex < connectedUsbCameraNames.size()) {
                            // USB 识别到的外置节点
                            usbProductName = connectedUsbCameraNames.get(externalCameraIndex);
                            // Camera2 识别到的外置节点 + USB 识别到的外置节点
                            displayName = usbProductName + " [ UVC ID " + cameraId + " ]";
                        } else {
                            // Camera2 识别到的外置节点
                            displayName = "UVC 外接摄像头 [ ID " + cameraId + " ]";
                        }
                        externalCameraIndex++;
                        break;
                    default:
                        displayName = "未知摄像头 [ ID " + cameraId + " ]";
                        break;
                }
                cameraDescriptionList.add(new CameraDescription(cameraId, lensFacing, displayName, usbProductName));
            } catch (CameraAccessException e) {
                Timber.tag(LogKit.TAG).e(e, "获取相机 CameraID: %s 详细属性失败", cameraId);
            }
        }
        return cameraDescriptionList;
    }

    /**
     * 获取已连接 UVC 设备名列表
     *
     * @param context 上下文
     * @return 已连接 UVC 设备名列表
     */
    @NonNull
    private static List<String> getConnectedUvcDeviceNameList(@NonNull Context context) {
        List<String> connectedUsbCameraNames = new ArrayList<>();
        // USB 管理器
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return connectedUsbCameraNames;
        }
        HashMap<String, UsbDevice> usbDeviceHashMap = usbManager.getDeviceList();
        for (UsbDevice usbDevice : usbDeviceHashMap.values()) {
            if (CameraDeviceKit.isUvcDevice(usbDevice)) {
                String productName = usbDevice.getProductName();
                if ((productName == null) || productName.trim().isEmpty()) {
                    String vendorIdStr = String.format("0x%04X", usbDevice.getVendorId());
                    String productIdStr = String.format("0x%04X", usbDevice.getProductId());
                    productName = ("USB Camera [ " + vendorIdStr + " : " + productIdStr + " ]");
                }
                connectedUsbCameraNames.add(productName);
            }
        }
        return connectedUsbCameraNames;
    }

    /**
     * 获取系统底层注册的所有相机 ID 列表
     *
     * @param context 上下文
     * @return 系统底层注册的所有相机 ID 列表
     */
    @NonNull
    private static List<String> getAvailableCameraIds(@NonNull Context context) {
        List<String> cameraIdList = new ArrayList<>();
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String[] cameraIds = cameraManager.getCameraIdList();
                Collections.addAll(cameraIdList, cameraIds);
            } catch (CameraAccessException e) {
                Timber.tag(LogKit.TAG).e(e, "获取系统 CameraId 失败");
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
                        // 转为 long 计算防止高分辨率像素乘积超 int 范围导致溢出
                        list.sort((s1, s2) -> Long.compare((long) s2.getWidth() * s2.getHeight(), (long) s1.getWidth() * s1.getHeight()));
                        resolutionList.addAll(list);
                    }
                }
            } catch (CameraAccessException e) {
                Timber.tag(LogKit.TAG).e(e, "获取相机 CameraID: %s 支持的分辨率失败", cameraId);
            }
        }
        return resolutionList;
    }

    /**
     * 获取指定相机 ID 支持的 Target FPS 范围列表
     *
     * @param context  上下文
     * @param cameraId 相机 ID
     * @return 指定相机 ID 支持的 Target FPS 范围列表
     */
    @NonNull
    public static List<Range<Integer>> getSupportedFpsRanges(@NonNull Context context, String cameraId) {
        List<Range<Integer>> fpsRangesList = new ArrayList<>();
        if ((cameraId == null) || cameraId.isEmpty()) {
            return fpsRangesList;
        }
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Range<Integer>[] fpsRanges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                if ((fpsRanges != null) && (fpsRanges.length > 0)) {
                    fpsRangesList.addAll(Arrays.asList(fpsRanges));
                }
            } catch (CameraAccessException e) {
                Timber.tag(LogKit.TAG).e(e, "获取相机 CameraID: %s 支持的 FPS 范围失败", cameraId);
            }
        }
        return fpsRangesList;
    }
}