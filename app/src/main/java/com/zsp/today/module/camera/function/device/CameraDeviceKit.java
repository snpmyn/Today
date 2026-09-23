package com.zsp.today.module.camera.function.device;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;

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
import java.util.Collections;
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
     * 是否是 UVC 设备
     * <p>
     * 通过 USB 硬件底层协议辨识
     * 基于 USB 设备描述符进行双重校验
     * 1. 检查设备全局类代码 (Device Class Code)
     * 标准 UVC 设备通常为 14 [0x0E - Video 视频类]
     * 2. 全局类代码未直接声明
     * 如复合设备或由接口定义
     * 遍历该 USB 设备所有接口类代码 (Interface Class Code)
     * 只要发现有任意接口类代码为 14
     * 即可准确判定该物理硬件是一个 UVC (USB Video Class) 视频设备
     *
     * @param usbDevice USB 设备
     * @return 是否是 UVC 设备
     */
    public static boolean isUvcDevice(@Nullable UsbDevice usbDevice) {
        if (usbDevice == null) {
            return false;
        }
        // 1. 检查 UsbDevice 级别 Class Code
        // UsbConstants.USB_CLASS_VIDEO 即 14 (0x0E)
        if (usbDevice.getDeviceClass() == UsbConstants.USB_CLASS_VIDEO) {
            return true;
        }
        // 2. 检查 Interface 级别 Class Code
        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            if (usbDevice.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_VIDEO) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否为 UVC 高拍仪
     * <p>
     * 通过 USB 硬件层与 Camera2 框架层差异化辨识
     * 侧重通过 CameraX 的 Camera2 互操作层
     * 根据指定相机 ID 动态过滤并映射到对应底层 CameraInfo
     * 进而交由后续逻辑判定该相机节点是否属于外部设备 (External / Unknown)
     * 以此精准识别 UVC 物理或虚拟外置相机
     *
     * @param context               上下文
     * @param processCameraProvider 生命周期绑定提供者
     * @param cameraId              相机 ID
     * @return 是否为 UVC 高拍仪
     */
    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    public static boolean checkIsUvcCamera(@NonNull Context context, @Nullable ProcessCameraProvider processCameraProvider, @Nullable String cameraId) {
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
            @NonNull
            @Override
            public List<CameraInfo> filter(@NonNull List<CameraInfo> cameraInfos) {
                List<CameraInfo> result = new ArrayList<>();
                for (CameraInfo cameraInfo : cameraInfos) {
                    try {
                        if (cameraId.equals(Camera2CameraInfo.from(cameraInfo).getCameraId())) {
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
     * <p>
     * 通过 CameraX 框架层辨识
     * 直接接收已配置好的生命周期提供者与相机选择器
     * 过滤并获取目标相机的硬件镜头朝向 (Lens Facing)
     * 若朝向被标记为外置 (EXTERNAL) 或未知 (UNKNOWN)
     * 则判定其为 UVC 外接高拍仪等设备
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

    /**
     * 构建相机选择器
     *
     * @param targetCameraId 目标相机 ID
     * @return 相机选择器
     */
    @NonNull
    @OptIn(markerClass = {ExperimentalCamera2Interop.class, ExperimentalLensFacing.class})
    public static CameraSelector buildCameraSelector(@Nullable String targetCameraId) {
        if ((targetCameraId != null) && !targetCameraId.isEmpty()) {
            return new CameraSelector.Builder().addCameraFilter(cameraInfos -> {
                List<CameraInfo> result = new ArrayList<>();
                for (CameraInfo cameraInfo : cameraInfos) {
                    try {
                        String id = Camera2CameraInfo.from(cameraInfo).getCameraId();
                        if (id.equals(targetCameraId)) {
                            result.add(cameraInfo);
                            break;
                        }
                    } catch (Exception e) {
                        Timber.tag(LogKit.TAG).w(e, "解析 CameraInfo 的 Camera2 ID 失败: %s", cameraInfo);
                    }
                }
                // 兜底校验
                // 指定 CameraID 匹配失败 (如设备拔出) 时降级走自定义优先级过滤，防止返回空列表抛出异常。
                if (result.isEmpty()) {
                    Timber.tag(LogKit.TAG).w("未匹配到 CameraID 为 [%s] 的摄像头，降级使用默认优先级匹配规则", targetCameraId);
                    return filterCameraByPriority(cameraInfos);
                }
                return result;
            }).build();
        } else {
            // 未指定 CameraID 则降级走自定义优先级过滤
            return new CameraSelector.Builder().addCameraFilter(CameraDeviceKit::filterCameraByPriority).build();
        }
    }

    /**
     * 通过优先级过滤相机
     * <p>
     * 优先级顺序
     * 1. 外接摄像头 - LENS_FACING_EXTERNAL
     * 适用标准 UVC 高拍仪
     * 2. 朝向未知摄像头 - LENS_FACING_UNKNOWN
     * 兼容未按标准实现驱动的定制硬件
     * 3. 相机列表最后一个摄像头
     * 作为异常硬件架构下兜底选项
     *
     * @param cameraInfos 相机信息列表
     * @return 过滤后的相机信息列表
     */
    @OptIn(markerClass = ExperimentalLensFacing.class)
    private static List<CameraInfo> filterCameraByPriority(List<CameraInfo> cameraInfos) {
        if ((cameraInfos == null) || cameraInfos.isEmpty()) {
            return Collections.emptyList();
        }
        CameraInfo unknownCameraInfo = null;
        for (CameraInfo cameraInfo : cameraInfos) {
            int lensFacing = cameraInfo.getLensFacing();
            if (lensFacing == CameraSelector.LENS_FACING_EXTERNAL) {
                return Collections.singletonList(cameraInfo);
            } else if ((lensFacing == CameraSelector.LENS_FACING_UNKNOWN) && (unknownCameraInfo == null)) {
                unknownCameraInfo = cameraInfo;
            }
        }
        if (unknownCameraInfo != null) {
            return Collections.singletonList(unknownCameraInfo);
        }
        CameraInfo fallbackCameraInfo = cameraInfos.get(cameraInfos.size() - 1);
        Timber.tag(LogKit.TAG).w("未匹配到标准 EXTERNAL 或 UNKNOWN 类型的摄像头，使用摄像头列表中最后一个节点兜底匹配: CameraInfo = %s", fallbackCameraInfo);
        return Collections.singletonList(fallbackCameraInfo);
    }
}