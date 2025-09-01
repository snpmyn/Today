package com.zsp.bdmap.kit;

import android.app.Application;
import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.lang.ref.WeakReference;

/**
 * Created on 2025/8/30.
 *
 * @author 郑少鹏
 * @desc 百度地图定位配套原件
 */
public class BdMapLocationKit {
    public static BdMapLocationKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 执行
     *
     * @param application                应用
     * @param bdAbstractLocationListener 百度地图定位配套原件监听
     */
    public void execute(Application application, BDAbstractLocationListener bdAbstractLocationListener) {
        // 百度 Android 定位 SDK 自 v9.2.9 版本增加了隐私合规接口，使用方式发生了改变， 与旧版本不兼容。
        // 请务必确保用户同意隐私政策后调用 setAgreePrivacy 接口以进行 SDK 初始化之前的准备工作
        // setAgreePrivacy 接口需要在 LocationClient 实例化之前调用
        // setAgreePrivacy 接口参数设 false 时定位功能不会实现
        // true 表示用户同意隐私合规政策
        // false 表示用户不同意隐私合规政策
        LocationClient.setAgreePrivacy(true);
        // 定位服务客户端
        // 宿主程序在客户端声明此类并调用，目前只支持在主线程中启动。
        LocationClient locationClient;
        try {
            WeakReference<Context> weakReference = new WeakReference<>(application);
            locationClient = new LocationClient(weakReference.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 声明 LocationClientOption 类实例并配置定位参数
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选
        // 默认 gcj02，设置返回的定位结果坐标系。
        // 如果配合百度地图使用，建议设为 bd09ll。
        locationClientOption.setCoorType("bd09ll");
        // 可选
        // 默认高精度，设置定位模式。
        // 高精度，低功耗，仅设备。
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选
        // 设置是否需要地址信息，默认不需要。
        locationClientOption.setIsNeedAddress(true);
        // 可选
        // 默认 false，设置定位时是否需要海拔信息。
        // 默认不需要，除基础定位版本都可用。
        locationClientOption.setIsNeedAltitude(false);
        // 可选
        // 设置是否需要设备方向结果
        locationClientOption.setNeedDeviceDirect(false);
        // 可选
        // 默认 false，设置是否需要 POI 结果。
        // 可在 BDLocation.getPoiList 里得到。
        locationClientOption.setIsNeedLocationPoiList(true);
        // 可选
        // 默认 false，设置是否需要地址描述，是否需要位置语义化结果。
        // 可在 BDLocation.getLocationDescribe 里得到，结果类似于“在北京天安门附近”。
        locationClientOption.setIsNeedLocationDescribe(true);
        // 可选
        // 默认 0，即仅定位一次，设置发起连续定位请求的间隔需大于等于 1000ms 才有效。
        locationClientOption.setScanSpan(1000);
        // 可选
        // 默认 false，设置是否开启卫星定位。
        locationClientOption.setOpenGnss(true);
        // 可选
        // 默认 false，设置当卫星定位有效时是否按照 1S 一次频率输出卫星定位结果。
        locationClientOption.setLocationNotify(true);
        // 设置打开自动回调位置模式，该开关打开后，开发者无需再关心定位间隔是多少，期间只要定位 SDK 检测到位置变化就会主动回调给开发者。
        // minTimeInterval - 最短定位时间间隔，单位毫秒，最小值 0，开发者可以在设置希望的位置回调最短时间间隔。
        // minDistance - 最短定位距离间隔，单位米，最小值 0，开发者可以设置希望的位置回调距离间隔。
        // locSensitivity - 定位变化敏感程度，LOC_SENSITIVITY_HIGHT、LOC_SENSITIVITY_MIDDLE、LOC_SENSITIVITY_LOW。
        locationClientOption.setOpenAutoNotifyMode(1000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        // 可选
        // 默认 true，定位 SDK 内部是一个 SERVICE，并放到了独立进程。
        // 设置是否在 stop 的时候杀死这个进程，默认不杀死。
        locationClientOption.setIgnoreKillProcess(true);
        // 可选
        // 默认 false，设置是否忽略 CRASH 信息。
        locationClientOption.SetIgnoreCacheException(false);
        // 需将配置好的 LocationClientOption 对象，通过 setLocOption 方法传递给 LocationClient 对象。
        locationClient.setLocOption(locationClientOption);
        // 注册监听
        locationClient.registerLocationListener(bdAbstractLocationListener);
        // 开始定位
        locationClient.start();
    }

    private static final class InstanceHolder {
        static final BdMapLocationKit INSTANCE = new BdMapLocationKit();
    }
}