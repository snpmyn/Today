## settings.gradle 添加

include ':bdmap'

## build.gradle 添加

implementation project(':bdmap')
/*BdMap*/

## 动态申请权限

Manifest.permission.ACCESS_FINE_LOCATION
Manifest.permission.MANAGE_EXTERNAL_STORAGE

## 使用

// 百度地图定位配套原件
BdMapLocationKit.getInstance().start(this, false, new BdMapLocationKitListener() {
@Override
public void onReceiveLocation(BDLocation bdLocation, String locationInfo) {
MmkvKit.defaultMmkv().encode(BdMapConstant.BD_MAP_$_LOCATION, locationInfo);
}
});