package com.zsp.today.module.camera;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.zsp.today.R;
import com.zsp.today.databinding.ActivityCameraBinding;
import com.zsp.today.module.camera.kit.CameraActivityKit;

import pool.base.BasePoolActivity;
import widget.permissionx.kit.PermissionKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;

/**
 * Created on 2026/9/15.
 *
 * @author 郑少鹏
 * @desc 相机页
 */
public class CameraActivity extends BasePoolActivity implements View.OnClickListener {
    /**
     * ActivityCameraBinding
     */
    private ActivityCameraBinding activityCameraBinding;
    /**
     * 相机页配套原件
     */
    private CameraActivityKit cameraActivityKit;

    /**
     * ViewBinding
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 viewBinding()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return ViewBinding
     */
    @Override
    protected ViewBinding viewBinding() {
        activityCameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        return activityCameraBinding;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {

    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        cameraActivityKit = new CameraActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        activityCameraBinding.cameraActivityMtSwitchCamera.setOnClickListener(this);
        activityCameraBinding.cameraActivityMtCapture.setOnClickListener(this);
        activityCameraBinding.cameraActivityMtLogOut.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        PermissionxKit.execute(this, true, PermissionKit.camera(), R.string.cameraAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                cameraActivityKit.initCameraConfig(CameraActivity.this, CameraActivity.this, activityCameraBinding);
            }

            @Override
            public void allGrantedContrary() {

            }
        });
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == activityCameraBinding.cameraActivityMtSwitchCamera.getId()) {
            // 切换摄像头
            cameraActivityKit.showCameraSelectDialog(this, this, activityCameraBinding);
        } else if (id == activityCameraBinding.cameraActivityMtCapture.getId()) {
            // 拍照
            cameraActivityKit.capture(this, activityCameraBinding);
        } else if (id == activityCameraBinding.cameraActivityMtLogOut.getId()) {
            // 退出
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraActivityKit != null) {
            cameraActivityKit.release();
        }
    }
}