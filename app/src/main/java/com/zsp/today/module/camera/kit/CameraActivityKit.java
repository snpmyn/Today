package com.zsp.today.module.camera.kit;

import android.content.Context;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCaptureException;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zsp.today.R;
import com.zsp.today.databinding.ActivityCameraBinding;
import com.zsp.today.module.camera.LogKit;
import com.zsp.today.module.camera.function.CameraController;
import com.zsp.today.module.camera.function.CameraManagerKit;
import com.zsp.today.module.camera.function.callback.CameraCaptureCallback;
import com.zsp.today.module.camera.function.callback.CameraInitCallback;
import com.zsp.today.module.camera.function.config.CameraConfigKit;
import com.zsp.today.module.camera.function.device.CameraDeviceKit;
import com.zsp.today.module.camera.function.fps.FpsTracker;
import com.zsp.today.module.camera.function.value.CameraDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import util.list.ListUtils;
import widget.toast.ToastKt;

/**
 * Created on 2026/9/15.
 *
 * @author 郑少鹏
 * @desc 相机页配套原件
 */
public class CameraActivityKit {
    /**
     * 相机控制器
     */
    private final CameraController cameraController;
    /**
     * 相机描述列表
     */
    private List<CameraDescription> cameraDescriptionList = new ArrayList<>();
    /**
     * 已选相机 ID
     */
    private String selectedCameraId = null;
    /**
     * 底层支持分辨率列表
     */
    private List<Size> supportedResolutionList = new ArrayList<>();
    /**
     * 已选分辨率
     */
    private Size selectedResolution = null;
    /**
     * 底层支持旋转角度列表
     */
    private List<Integer> supportedRotationList = new ArrayList<>();
    /**
     * 旋转角度是否正在初始化
     */
    private boolean isRotationInitializing = false;
    /**
     * 已选旋转角度
     */
    private Integer selectedRotation = null;

    /**
     * constructor
     */
    public CameraActivityKit() {
        this.cameraController = new CameraController();
    }

    /**
     * 初始化相机配置
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void initCameraConfig(Context context, LifecycleOwner lifecycleOwner, ActivityCameraBinding activityCameraBinding) {
        // 帧率追踪器
        FpsTracker fpsTracker = new FpsTracker(fps -> activityCameraBinding.getRoot().post(() -> activityCameraBinding.cameraActivityTv.setText(String.format(Locale.getDefault(), context.getString(R.string.formatFpsWithValue), fps))));
        // 设置帧率追踪器
        cameraController.setFpsTracker(fpsTracker);
        // 获取相机描述列表
        cameraDescriptionList = CameraManagerKit.getCameraDescriptionList(context);
        if (ListUtils.listIsEmpty(cameraDescriptionList)) {
            ToastKt.showToast(R.string.cameraNotDetected);
            return;
        }
        if (cameraDescriptionList.size() == 1) {
            selectedCameraId = cameraDescriptionList.get(0).getCameraId();
            // 执行
            execute(context, lifecycleOwner, activityCameraBinding);
        } else {
            // 显示相机选择对话框
            showCameraSelectDialog(context, lifecycleOwner, activityCameraBinding);
        }
    }

    /**
     * 显示相机选择对话框
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void showCameraSelectDialog(Context context, LifecycleOwner lifecycleOwner, ActivityCameraBinding activityCameraBinding) {
        if (ListUtils.listIsEmpty(cameraDescriptionList)) {
            // 获取相机描述列表
            cameraDescriptionList = CameraManagerKit.getCameraDescriptionList(context);
        }
        if (ListUtils.listIsEmpty(cameraDescriptionList)) {
            ToastKt.showToast(R.string.cameraNotDetected);
            return;
        }
        String[] items = new String[cameraDescriptionList.size()];
        for (int i = 0; i < cameraDescriptionList.size(); i++) {
            items[i] = cameraDescriptionList.get(i).getDisplayName();
        }
        new MaterialAlertDialogBuilder(context).setTitle("选择要打开的摄像头").setItems(items, (dialog, which) -> {
            selectedCameraId = cameraDescriptionList.get(which).getCameraId();
            // 执行
            execute(context, lifecycleOwner, activityCameraBinding);
        }).setCancelable(false).show();
    }

    /**
     * 执行
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    private void execute(Context context, LifecycleOwner lifecycleOwner, ActivityCameraBinding activityCameraBinding) {
        // 初始化分辨率下拉选择框
        setupResolutionSpinner(context, lifecycleOwner, activityCameraBinding);
        // 初始化旋转角度下拉选择框
        setupRotationSpinner(context, activityCameraBinding);
        // 启动相机
        startCamera(context, lifecycleOwner, activityCameraBinding);
    }

    /**
     * 初始化分辨率下拉选择框
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void setupResolutionSpinner(Context context, LifecycleOwner lifecycleOwner, @NonNull ActivityCameraBinding activityCameraBinding) {
        if (selectedCameraId == null) {
            return;
        }
        // 1. 获取指定相机 ID 支持的原生分辨率列表
        supportedResolutionList = CameraManagerKit.getSupportedResolutions(context, selectedCameraId);
        if (ListUtils.listIsEmpty(supportedResolutionList)) {
            Timber.tag(LogKit.TAG).w("未查询到摄像头 CameraID: %s 支持的分辨率列表", selectedCameraId);
            return;
        }
        // 2. 格式化为可视化分辨率文本
        List<String> resolutionsStr = new ArrayList<>();
        for (Size size : supportedResolutionList) {
            resolutionsStr.add(size.getWidth() + " x " + size.getHeight());
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, resolutionsStr);
        // 3. 避免重新设置 Adapter 时触发旧监听器的误选择逻辑
        activityCameraBinding.cameraActivityMactvSwitchResolution.setOnItemClickListener(null);
        activityCameraBinding.cameraActivityMactvSwitchResolution.setAdapter(stringArrayAdapter);
        // 4. 默认选中最高分辨率
        activityCameraBinding.cameraActivityMactvSwitchResolution.setText(resolutionsStr.get(0), false);
        selectedResolution = supportedResolutionList.get(0);
        // 5. 监听下拉选择切换
        activityCameraBinding.cameraActivityMactvSwitchResolution.setOnItemClickListener((parent, view, position, id) -> {
            if ((position < 0) || (position >= supportedResolutionList.size())) {
                return;
            }
            Size newResolution = supportedResolutionList.get(position);
            if (!newResolution.equals(selectedResolution)) {
                selectedResolution = newResolution;
                // 设置分辨率
                cameraController.setResolution(context, lifecycleOwner, activityCameraBinding.cameraActivityMcv, activityCameraBinding.cameraActivityPv, selectedResolution, selectedRotation);
            }
        });
    }

    /**
     * 初始化旋转角度下拉选择框
     *
     * @param context               上下文
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void setupRotationSpinner(Context context, @NonNull ActivityCameraBinding activityCameraBinding) {
        if (selectedCameraId == null) {
            return;
        }
        // 标记开始初始化
        // 拦截 Spinner 设置 Adapter 阶段自动触发
        isRotationInitializing = true;
        // 1. 设置支持的旋转角度列表
        // 关联 Surface 角度常量值
        supportedRotationList = new ArrayList<>();
        supportedRotationList.add(Surface.ROTATION_0);
        supportedRotationList.add(Surface.ROTATION_90);
        supportedRotationList.add(Surface.ROTATION_180);
        supportedRotationList.add(Surface.ROTATION_270);
        // 2. 格式化为可视化旋转角度文本
        List<String> rotationsStr = new ArrayList<>();
        rotationsStr.add("0°");
        rotationsStr.add("90°");
        rotationsStr.add("180°");
        rotationsStr.add("270°");
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, rotationsStr);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 3. 避免重新设置 Adapter 时触发旧监听器的误选择逻辑
        activityCameraBinding.cameraActivitySpinnerSwitchRotation.setOnItemSelectedListener(null);
        activityCameraBinding.cameraActivitySpinnerSwitchRotation.setAdapter(stringArrayAdapter);
        // 4. 计算初始化选中的旋转角度
        int initialRotation = CameraConfigKit.resolveRotation(selectedCameraId, activityCameraBinding.cameraActivityPv, CameraDeviceKit.checkIsUvcCamera(context, null, selectedCameraId));
        int defaultIndex = supportedRotationList.indexOf(initialRotation);
        if (defaultIndex < 0) {
            defaultIndex = 0;
        }
        activityCameraBinding.cameraActivitySpinnerSwitchRotation.setSelection(defaultIndex, true);
        selectedRotation = supportedRotationList.get(defaultIndex);
        // 5. 延迟恢复监听防抖
        // 通过 View.post 将任务推入主线程 MessageQueue 末尾
        // 确保在 Spinner 内部 RequestLayout 与 View 树测量绘制引发的原生伪回调执行完毕后再重置标志位为 false
        activityCameraBinding.cameraActivitySpinnerSwitchRotation.post(() -> isRotationInitializing = false);
        // 6. 监听下拉选择切换
        activityCameraBinding.cameraActivitySpinnerSwitchRotation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isRotationInitializing) {
                    return;
                }
                if ((position < 0) || (position >= supportedRotationList.size())) {
                    return;
                }
                Integer newRotation = supportedRotationList.get(position);
                if (!newRotation.equals(selectedRotation)) {
                    selectedRotation = newRotation;
                    // 设置旋转角度
                    cameraController.setRotation(activityCameraBinding.cameraActivityPv, selectedRotation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 启动相机
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void startCamera(Context context, LifecycleOwner lifecycleOwner, @NonNull ActivityCameraBinding activityCameraBinding) {
        CameraInitCallback cameraInitCallback = new CameraInitCallback() {
            @Override
            public void onCameraInitSuccess() {
                Timber.tag(LogKit.TAG).i("相机启动成功 Camera ID: %s", selectedCameraId);
            }

            @Override
            public void onCameraInitError(@NonNull Throwable throwable) {
                Timber.tag(LogKit.TAG).e(throwable, "相机启动失败: %s", throwable.getMessage());
                activityCameraBinding.getRoot().post(() -> activityCameraBinding.cameraActivityTv.setText(String.format(Locale.getDefault(), context.getString(R.string.formatFpsNoValue), "--")));
            }
        };
        // 启动相机
        cameraController.startCamera(context, lifecycleOwner, activityCameraBinding.cameraActivityMcv, activityCameraBinding.cameraActivityPv, selectedCameraId, selectedResolution, selectedRotation, cameraInitCallback);
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void capture(Context context, @NonNull ActivityCameraBinding activityCameraBinding) {
        activityCameraBinding.cameraActivityMtCapture.setEnabled(false);
        cameraController.capture(context, activityCameraBinding.cameraActivityPv, new CameraCaptureCallback() {
            @Override
            public void onCameraCaptureSuccess(File photoFile) {
                Timber.tag(LogKit.TAG).i("拍照成功: %s", photoFile.getAbsolutePath());
                activityCameraBinding.getRoot().post(() -> {
                    activityCameraBinding.cameraActivityMtCapture.setEnabled(true);
                    ToastKt.showToast("拍照成功: " + photoFile.getName());
                });
            }

            @Override
            public void onCameraCaptureError(ImageCaptureException imageCaptureException) {
                Timber.tag(LogKit.TAG).e(imageCaptureException, "拍照失败: %s", imageCaptureException.getMessage());
                activityCameraBinding.getRoot().post(() -> {
                    activityCameraBinding.cameraActivityMtCapture.setEnabled(true);
                    ToastKt.showToast("拍照失败: " + imageCaptureException.getMessage());
                });
            }
        });
    }

    /**
     * 释放
     *
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void release(ActivityCameraBinding activityCameraBinding) {
        cameraController.release((activityCameraBinding != null) ? activityCameraBinding.cameraActivityPv : null);
    }
}