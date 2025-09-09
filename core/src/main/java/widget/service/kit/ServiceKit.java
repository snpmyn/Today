package widget.service.kit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import widget.service.base.BaseServiceConnection;

/**
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
 * @desc 服务配套原件
 */
public class ServiceKit {
    public static ServiceKit getInstance() {
        return ServiceKit.InstanceHolder.INSTANCE;
    }

    /**
     * 开始
     * <p>
     * 在 Activity 或 Application 中调用
     *
     * @param appCompatActivity     活动
     * @param baseServiceConnection 服务连接基类
     * @param cls                   The component class that is to be used for the intent.
     */
    public void start(AppCompatActivity appCompatActivity, BaseServiceConnection baseServiceConnection, Class<?> cls) {
        Intent serviceIntent = new Intent(appCompatActivity, cls);
        // 绑定服务
        appCompatActivity.bindService(serviceIntent, baseServiceConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 启动前台服务
            appCompatActivity.startForegroundService(serviceIntent);
        } else {
            // 启动服务
            appCompatActivity.startService(serviceIntent);
        }
    }

    /**
     * 结束
     *
     * @param appCompatActivity     活动
     * @param baseServiceConnection 服务连接基类
     */
    public void end(AppCompatActivity appCompatActivity, @NonNull BaseServiceConnection baseServiceConnection) {
        if (baseServiceConnection.areServiceConnect) {
            appCompatActivity.unbindService(baseServiceConnection);
            baseServiceConnection.areServiceConnect = false;
        }
    }

    private static final class InstanceHolder {
        static final ServiceKit INSTANCE = new ServiceKit();
    }
}