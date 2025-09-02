## settings.gradle 添加

include ':amap'

## build.gradle 添加

implementation project(':amap')
/*AMap*/

## 动态申请权限

Manifest.permission.ACCESS_FINE_LOCATION
Manifest.permission.ACCESS_BACKGROUND_LOCATION

## 使用

// 高德地图定位配套原件
AmapLocationKit.getInstance().start(App.getAppInstance(),
AMapLocationClientOption.AMapLocationPurpose.Transport, true, new AmapLocationKitListener() {
@Override
public void locationSuccessful(AMapLocation aMapLocation, String locationInfo) {
MmkvKit.defaultMmkv().encode(AmapConstant.AMAP_$_LOCATION, locationInfo);
AmapLocationKit.getInstance().stop();
}

            @Override
            public void locationFail(AMapLocation aMapLocation) {

            }
        });

// 开始
AmapLocationKit.getInstance().start();

// 停止
AmapLocationKit.getInstance().stop();

// 销毁
AmapLocationKit.getInstance().destroy();

## 注意

定位 SDK 无需 so 库文件支持