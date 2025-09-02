package com.zsp.amap.listener;

import com.amap.api.location.AMapLocation;

/**
 * Created on 2025/9/1.
 *
 * @author 郑少鹏
 * @desc 高德地图定位配套原件监听
 */
public interface AmapLocationKitListener {
    /**
     * 定位成功
     *
     * @param aMapLocation AMapLocation
     * @param locationInfo 定位信息
     */
    void locationSuccessful(AMapLocation aMapLocation, String locationInfo);

    /**
     * 定位失败
     *
     * @param aMapLocation AMapLocation
     */
    void locationFail(AMapLocation aMapLocation);
}