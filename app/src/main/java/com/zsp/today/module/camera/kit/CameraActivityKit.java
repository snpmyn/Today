package com.zsp.today.module.camera.kit;

import android.content.Context;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.ImageCaptureException;
import androidx.lifecycle.LifecycleOwner;

import com.zsp.today.R;
import com.zsp.today.databinding.ActivityCameraBinding;
import com.zsp.today.module.camera.CameraController;
import com.zsp.today.module.camera.FpsTracker;

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
    private static final String TAG = CameraActivityKit.class.getSimpleName();
    /**
     * 相机控制器
     */
    private final CameraController cameraController;
    /**
     * 相机 ID 列表
     */
    private List<String> cameraIdList = new ArrayList<>();
    /**
     * 已选相机 ID
     */
    private String selectedCameraId = null;
    /**
     * 底层支持分辨率列表
     */
    private List<Size> supportedResolutionList = new ArrayList<>();
    /**
     * 分辨率是否正在初始化
     */
    private boolean isResolutionInitializing = false;
    /**
     * 已选分辨率
     */
    private Size selectedResolution = null;

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
        // 获取系统底层注册的所有相机 ID 列表
        cameraIdList = cameraController.getAvailableCameraIds(context);
        if (ListUtils.listIsEmpty(cameraIdList)) {
            ToastKt.showToast("未检测到摄像头");
            return;
        }
        if (cameraIdList.size() == 1) {
            selectedCameraId = cameraIdList.get(0);
            // 初始化分辨率下拉选择框
            setupResolutionSpinner(context, lifecycleOwner, activityCameraBinding);
            // 启动相机
            startCamera(context, lifecycleOwner, activityCameraBinding);
        } else {
            // 显示相机选择对话框
            showCameraSelectDialog(context, lifecycleOwner, activityCameraBinding);
        }
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
        // 标记开始初始化
        // 拦截 Spinner 设置 Adapter 阶段的自动伪触发
        isResolutionInitializing = true;
        // 1. 动态查询选定摄像头支持的真实分辨率
        supportedResolutionList = cameraController.getSupportedResolutions(context, selectedCameraId);
        if (supportedResolutionList.isEmpty()) {
            Timber.tag(TAG).w("未查询到摄像头 CameraID: %s 支持的分辨率列表", selectedCameraId);
            isResolutionInitializing = false;
            return;
        }
        // 2. 格式化为可视化分辨率文本
        List<String> resolutionsStr = new ArrayList<>();
        for (Size size : supportedResolutionList) {
            resolutionsStr.add(size.getWidth() + " x " + size.getHeight());
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, resolutionsStr);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 3. 避免重新设置 Adapter 时触发旧监听器的误选择逻辑
        activityCameraBinding.cameraActivitySpinnerSwitchResolution.setOnItemSelectedListener(null);
        activityCameraBinding.cameraActivitySpinnerSwitchResolution.setAdapter(stringArrayAdapter);
        // 4. 默认选中最高分辨率
        activityCameraBinding.cameraActivitySpinnerSwitchResolution.setSelection(0, false);
        selectedResolution = supportedResolutionList.get(0);
        // 5. 延迟恢复监听防抖
        // 通过 View.post 将任务推入主线程 MessageQueue 末尾
        // 确保在 Spinner 内部 RequestLayout 与 View 树测量绘制引发的原生伪回调执行完毕后再重置标志位为 false
        activityCameraBinding.cameraActivitySpinnerSwitchResolution.post(() -> isResolutionInitializing = false);
        // 6. 监听下拉选择切换
        activityCameraBinding.cameraActivitySpinnerSwitchResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isResolutionInitializing) {
                    return;
                }
                if ((position < 0) || (position >= supportedResolutionList.size())) {
                    return;
                }
                Size newSize = supportedResolutionList.get(position);
                // 用户选中分辨率发生变化时重启相机更新流参数
                if (!newSize.equals(selectedResolution)) {
                    selectedResolution = newSize;
                    // 启动相机
                    startCamera(context, lifecycleOwner, activityCameraBinding);
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
        cameraController.startCamera(context, lifecycleOwner, activityCameraBinding.cameraActivityMcv, activityCameraBinding.cameraActivityPv, selectedCameraId, selectedResolution, new CameraController.CameraInitCallback() {
            @Override
            public void onCameraInitSuccess() {
                Timber.tag(TAG).i("相机绑定成功 Camera ID: %s", selectedCameraId);
            }

            @Override
            public void onCameraInitError(Throwable throwable) {
                activityCameraBinding.getRoot().post(() -> activityCameraBinding.cameraActivityTv.setText(String.format(Locale.getDefault(), context.getString(R.string.formatFpsNoValue), "--")));
                ToastKt.showToast("相机启动失败: " + throwable.getMessage());
            }
        });
    }

    /**
     * 显示相机选择对话框
     *
     * @param context               上下文
     * @param lifecycleOwner        生命周期拥有者
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void showCameraSelectDialog(Context context, LifecycleOwner lifecycleOwner, ActivityCameraBinding activityCameraBinding) {
        if (ListUtils.listIsEmpty(cameraIdList)) {
            // 获取系统底层注册的所有相机 ID 列表
            cameraIdList = cameraController.getAvailableCameraIds(context);
        }
        if (ListUtils.listIsEmpty(cameraIdList)) {
            ToastKt.showToast("未检测到摄像头");
            return;
        }
        String[] items = new String[cameraIdList.size()];
        for (int i = 0; i < cameraIdList.size(); i++) {
            items[i] = "Camera ID: " + cameraIdList.get(i);
        }
        new AlertDialog.Builder(context).setTitle("选择要打开的摄像头").setItems(items, (dialog, which) -> {
            selectedCameraId = cameraIdList.get(which);
            // 初始化分辨率下拉选择框
            setupResolutionSpinner(context, lifecycleOwner, activityCameraBinding);
            // 启动相机
            startCamera(context, lifecycleOwner, activityCameraBinding);
        }).setCancelable(false).show();
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param activityCameraBinding ActivityCameraBinding
     */
    public void capture(Context context, @NonNull ActivityCameraBinding activityCameraBinding) {
        activityCameraBinding.cameraActivityMtCapture.setEnabled(false);
        cameraController.capture(context, activityCameraBinding.cameraActivityPv, new CameraController.CameraCaptureCallback() {
            @Override
            public void onCameraCaptureSuccess(File photoFile) {
                Timber.tag(TAG).i("保存路径: %s", photoFile.getAbsolutePath());
                activityCameraBinding.getRoot().post(() -> {
                    activityCameraBinding.cameraActivityMtCapture.setEnabled(true);
                    ToastKt.showToast("抓抓成功: " + photoFile.getName());
                });
            }

            @Override
            public void onCameraCaptureError(ImageCaptureException imageCaptureException) {
                Timber.tag(TAG).e(imageCaptureException, "抓拍失败: %s", imageCaptureException.getMessage());
                activityCameraBinding.getRoot().post(() -> {
                    activityCameraBinding.cameraActivityMtCapture.setEnabled(true);
                    ToastKt.showToast("抓拍失败: " + imageCaptureException.getMessage());
                });
            }
        });
    }

    /**
     * 释放
     */
    public void release() {
        cameraController.release();
    }
}