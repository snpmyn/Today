package com.zsp.amap.kit;

import android.app.Application;
import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.zsp.amap.listener.AmapLocationKitListener;

import java.lang.ref.WeakReference;
import java.util.Locale;

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
                    amapLocationKitListener.locationSuccessful(aMapLocation, getLocationInfo(aMapLocation));
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
     * <p>
     * 导航 scheme
     * androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=36.547901&lon=104.258354&dev=1&style=2
     * <p>
     * 步行导航
     * amapuri://openFeature?featureName=OnFootNavi&sourceApplication=aaa&lat=110&lon=36
     * <p>
     * 骑行导航
     * amapuri://openFeature?featureName=OnRideNavi&rideType=elebike&sourceApplication=appname&lat=36.547901&lon=104.258354&dev=0
     * <p>
     * 地图标注
     * androidamap://viewMap?sourceApplication=appname&poiname=abc&lat=36.2&lon=116.1&dev=0
     * <p>
     * 路径规划
     * amapuri://route/plan/?sid=&slat=39.92848272&slon=116.39560823&sname=A&did=&dlat=39.98848272&dlon=116.47560823&dname=B&dev=0&t=0&vian=2&vialons=116.8|116.5&vialats=39.5|39.7&vianames=途径点1|途径点2
     * <p>
     * 公交线路查询
     * androidamap://bus?sourceApplication=softname&busname=445&city=010
     * <p>
     * 关键词路线规划
     * androidamap://keywordNavi?sourceApplication=softname&keyword=方恒国际中心&style=2
     * <p>
     * 周边分类
     * androidamap://arroundpoi?sourceApplication=softname&keywords=银行|加油站|电影院&lat=36.2&lon=116.1&dev=0
     * androidamap://arroundpoi?sourceApplication=softname&keywords=银行|加油站|电影院&dev=0
     * <p>
     * 我的位置
     * androidamap://myLocation?sourceApplication=softname
     * <p>
     * 逆地理编码
     * androidamap://viewReGeo?sourceApplication=softname&lat=39.92&lon=116.46&dev=1
     * <p>
     * 搜索地点
     * androidamap://poi?sourceApplication=softname&keywords=银行|加油站|电影院&lat1=36.1&lon1=116.1&lat2=36.2&lon2=116.2&dev=0
     * androidamap://poi?sourceApplication=softname&keywords=银行|加油站|电影院&dev=0
     * <p>
     * 地铁图
     * androidamap://openFeature?featureName=Subway&adcode=310000&sourceApplication=softname&page=Subway
     * <p>
     * 地图主图
     * androidamap://rootmap?sourceApplication=applicationName
     *
     * @param aMapLocation BDLocation
     * @return 定位信息
     */
    public String getLocationInfo(AMapLocation aMapLocation) {
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
        return String.format(Locale.CHINA, "我在%1$s，%2$s。经度 %3$.2f，纬度 %4$.2f。附近有%5$s。", addrStr, locationDescribe, longitude, latitude, aoiName);
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