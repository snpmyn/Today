package com.zsp.today.basic.service;

import android.content.ComponentName;
import android.os.IBinder;

import widget.service.base.BaseServiceConnection;

/**
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
 * @desc 周期服务连接
 */
public class PeriodicServiceConnection extends BaseServiceConnection {
    /**
     * 周期服务连接监听
     */
    private final PeriodicServiceConnectionListener periodicServiceConnectionListener;

    /**
     * constructor
     *
     * @param periodicServiceConnectionListener 周期服务连接监听
     */
    public PeriodicServiceConnection(PeriodicServiceConnectionListener periodicServiceConnectionListener) {
        this.periodicServiceConnectionListener = periodicServiceConnectionListener;
    }

    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * <p class="note"><b>Note:</b> If the system has started to bind your
     * client app to a service, it's possible that your app will never receive
     * this callback. Your app won't receive a callback if there's an issue with
     * the service, such as the service crashing while being created.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param service The IBinder of the Service's communication channel,
     *                which you can now make calls on.
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        areServiceConnect = true;
        PeriodicService.LocalBinder localBinder = (PeriodicService.LocalBinder) service;
        PeriodicService periodicService = localBinder.getService();
        periodicService.setPeriodicServiceListener(count -> {
            if (null != periodicServiceConnectionListener) {
                periodicServiceConnectionListener.execute(count);
            }
        });
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        areServiceConnect = false;
    }

    /**
     * 周期服务连接监听
     */
    public interface PeriodicServiceConnectionListener {
        /**
         * 执行
         *
         * @param count 数量
         */
        void execute(int count);
    }
}