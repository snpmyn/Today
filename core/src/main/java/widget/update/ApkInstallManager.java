package widget.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zsp.core.R;

import java.io.File;

import util.intent.IntentOther;
import widget.permissionx.kit.PermissionKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.toast.ToastKit;

/**
 * @decs: APK 安装管理器
 * @author: 郑少鹏
 * @date: 2025/9/24 14:34
 * @version: v 1.0
 */
public class ApkInstallManager {
    private AppCompatActivity appCompatActivity;

    @NonNull
    public static ApkInstallManager getInstance(AppCompatActivity appCompatActivity) {
        ApkInstallManager apkInstallManager = ApkInstallManager.InstanceHolder.INSTANCE;
        apkInstallManager.appCompatActivity = appCompatActivity;
        return apkInstallManager;
    }

    /**
     * 执行
     *
     * @param apkFile APK 文件
     */
    public void execute(File apkFile) {
        PermissionxKit.execute(appCompatActivity, PermissionKit.install(), R.string.installApplicationAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
            @Override
            public void allGranted() {
                if ((null == apkFile) || !apkFile.exists()) {
                    return;
                }
                IntentOther.installApk(appCompatActivity, apkFile);
            }

            @Override
            public void allGrantedContrary() {
                ToastKit.showShort(appCompatActivity.getString(R.string.installApplicationAreBasedOnThePermission));
            }
        });
    }

    private static final class InstanceHolder {
        static final ApkInstallManager INSTANCE = new ApkInstallManager();
    }
}