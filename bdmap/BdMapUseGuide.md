## settings.gradle 添加

include ':bdmap'

## build.gradle 添加

implementation project(':bdmap')
/*BdMap*/

## strings.xml

<string name="formatBdMapLocation">我在%1$s，%2$s。经度 %3$.2f，纬度 %4$.2f。附近有%5$s。</string>

## 动态获取权限

Manifest.permission.ACCESS_FINE_LOCATION
Manifest.permission.MANAGE_EXTERNAL_STORAGE

## App 添加

// 百度地图定位配套原件
BdMapLocationKit.getInstance().execute(this, new BDAbstractLocationListener() {
@Override
public void onReceiveLocation(BDLocation bdLocation) {
appKit.saveBdMapLocation(getAppInstance(), bdLocation);
}
});

## AppKit 添加

/**

* 保存百度地图定位
*
* @param application 应用
* @param bdLocation BDLocation
  */
  public void saveBdMapLocation(Application application, @NonNull BDLocation bdLocation) {
  // 地址
  String addrStr = bdLocation.getAddrStr();
  // 定位描述
  String locationDescribe = bdLocation.getLocationDescribe();
  // 经度
  double longitude = bdLocation.getLongitude();
  // 纬度
  double latitude = bdLocation.getLatitude();
  // POI
  List<Poi> poiList = bdLocation.getPoiList();
  List<String> poiNameList = new ArrayList<>(poiList.size());
  for (Poi poi : poiList) {
  poiNameList.add(poi.getName());
  }
  String poiInfo = ListUtils.splicingEnumerationPunctuation(poiNameList);
  // 定位信息
  String locationInfo = String.format(application.getString(R.string.formatBdMapLocation), addrStr,
  locationDescribe, longitude, latitude, poiInfo);
  MmkvKit.defaultMmkv().encode(BdMapConstant.BD_MAP_$_LOCATION, locationInfo);
  }