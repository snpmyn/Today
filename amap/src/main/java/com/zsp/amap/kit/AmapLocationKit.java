package com.zsp.amap.kit;

import android.app.Application;
import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.zsp.amap.R;
import com.zsp.amap.listener.AmapLocationKitListener;

import java.lang.ref.WeakReference;

/**
 * Created on 2025/9/1.
 *
 * @author 郑少鹏
 * @desc 高德地图定位配套原件
 */
public class AmapLocationKit {
    /**
     * 高德地图定位客户端
     */
    private AMapLocationClient aMapLocationClient;

    public static AmapLocationKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 开始
     *
     * @param application             应用
     * @param aMapLocationPurpose     高德地图定位客户端配置
     * @param start                   开始
     * @param amapLocationKitListener 高德地图定位配套原件监听
     */
    public void start(Application application, AMapLocationClientOption.AMapLocationPurpose aMapLocationPurpose, boolean start, AmapLocationKitListener amapLocationKitListener) {
        WeakReference<Context> weakReference = new WeakReference<>(application);
        // 隐私权政策包含高德开平台隐私权政策
        // 隐私权政策弹窗展示告知用户
        AMapLocationClient.updatePrivacyShow(weakReference.get(), true, true);
        // 隐私权政策征得用户同意
        AMapLocationClient.updatePrivacyAgree(weakReference.get(), true);
        // 高德地图定位客户端
        try {
            aMapLocationClient = new AMapLocationClient(weakReference.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 高德地图定位客户端配置
        AMapLocationClientOption aMapLocationClientOption = getAMapLocationClientOption(aMapLocationPurpose);
        // 给定位客户端对象设置定位参数
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        // 设置定位监听
        aMapLocationClient.setLocationListener(aMapLocation -> {
            if (null != aMapLocation) {
                if (aMapLocation.getErrorCode() == 0) {
                    amapLocationKitListener.locationSuccessful(aMapLocation, getLocationInfo(application, aMapLocation));
                } else {
                    amapLocationKitListener.locationFail(aMapLocation);
                }
            }
        });
        // 开始定位
        if (start) {
            aMapLocationClient.startLocation();
        }
    }

    /**
     * 获取高德地图定位客户端配置
     *
     * @param aMapLocationPurpose 高德地图定位客户端配置
     * @return 高德地图定位客户端配置
     */
    private static AMapLocationClientOption getAMapLocationClientOption(AMapLocationClientOption.AMapLocationPurpose aMapLocationPurpose) {
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        // 默认无场景
        // 目前支持三种场景（签到、出行、运动）
        // 该部分功能从定位 SDK v3.7.0 开始提供
        // 选了对应定位场景则不用自行设置 AMapLocationClientOption 中其它参数，SDK 会根据选择的场景自行定制 AMapLocationClientOption 参数的值。
        // 当然也可在基础上自行设置，实际按最后一次设置的参数值生效。
        aMapLocationClientOption.setLocationPurpose(aMapLocationPurpose);
        if (aMapLocationPurpose == AMapLocationClientOption.AMapLocationPurpose.SignIn) {
            // 单次定位
            // 获取一次定位结果
            // 该方法默认为 false
            /*aMapLocationClientOption.setOnceLocation(true);*/
            // 获取最近 3s 内精度最高的一次定位结果
            // 设置 setOnceLocationLatest(boolean b) 接口为 true，启动定位时 SDK 会返回最近 3s 内精度最高的一次定位结果。
            // 如果设置其为 true，setOnceLocation(boolean b) 接口也会被设置为 true，反之不会，默认为 false。
            aMapLocationClientOption.setOnceLocationLatest(true);
        } else {
            // 自定义连续定位
            // 设置定位间隔，单位毫秒，默认 2000ms，最低 1000ms。
            // SDK 默认采用连续定位模式，时间间隔 2000ms。
            aMapLocationClientOption.setInterval(2000);
        }
        // 设置是否允许模拟位置
        // 默认 true
        // 设置是否允许模拟软件 Mock 位置结果，多为模拟 GPS 定位结果，默认 true，允许模拟位置。
        aMapLocationClientOption.setMockEnable(true);
        // 设置是否返回地址信息
        // 默认返回地址信息
        aMapLocationClientOption.setNeedAddress(true);
        // 单位毫秒，默认 30000 毫秒，建议超时时间不要低于 8000 毫秒。
        aMapLocationClientOption.setHttpTimeOut(20000);
        // 缓存机制默认开启，可以通过以下接口进行关闭。
        // 当开启定位缓存功能，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存，不区分单次定位还是连续定位。
        // GPS 定位结果不会被缓存
        aMapLocationClientOption.setLocationCacheEnable(true);
        // 设置定位模式为 AMapLocationMode.Hight_Accuracy 高精度模式
        // 高德定位服务包含 GPS 和网络定位 (Wi-Fi 和基站定位) 两种能力
        // 定位 SDK 将 GPS、网络定位能力进行了封装，以三种定位模式对外开放，SDK 默认选择使用高精度定位模式。
        // 高精度定位模式：会同时使用网络定位和 GPS 定位，优先返回最高精度的定位结果，以及对应的地址描述信息。
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        return aMapLocationClientOption;
    }

    /**
     * 获取定位信息
     *
     * @param application  应用
     * @param aMapLocation BDLocation
     * @return 定位信息
     */
    public String getLocationInfo(Application application, AMapLocation aMapLocation) {
        // 地址
        String addrStr = aMapLocation.getAddress();
        // AOI 名
        String aoiName = aMapLocation.getAoiName();
        // 定位描述
        String locationDescribe = aMapLocation.getDescription();
        // 经度
        double longitude = aMapLocation.getLongitude();
        // 纬度
        double latitude = aMapLocation.getLatitude();
        // 定位信息
        return String.format(application.getString(R.string.formatAmapLocation), addrStr, locationDescribe, longitude, latitude, aoiName);
    }

    /**
     * 开始
     */
    public void start() {
        aMapLocationClient.startLocation();
    }

    /**
     * 停止
     * <p>
     * 停止定位后本地定位服务不会被销毁
     */
    public void stop() {
        aMapLocationClient.stopLocation();
    }

    /**
     * 销毁
     * <p>
     * 销毁定位客户端同时销毁本地定位服务
     * 销毁定位客户端后，重开定位需新 New 一个 AMapLocationClient 对象。
     */
    public void destroy() {
        aMapLocationClient.onDestroy();
    }

    private static final class InstanceHolder {
        static final AmapLocationKit INSTANCE = new AmapLocationKit();
    }
}