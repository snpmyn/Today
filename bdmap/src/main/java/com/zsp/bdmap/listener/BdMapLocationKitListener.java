package com.zsp.bdmap.listener;

import com.baidu.location.BDLocation;

/**
 * Created on 2025/9/1.
 *
 * @author 郑少鹏
 * @desc 百度地图定位配套原件监听
 */
public interface BdMapLocationKitListener {
    /**
     * 接收定位
     *
     * @param bdLocation   BDLocation
     * @param locationInfo 定位信息
     */
    void onReceiveLocation(BDLocation bdLocation, String locationInfo);
}