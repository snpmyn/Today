# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

# 保留 com.zsp.amap 命名空间下所有类
# 防止被混淆或裁剪
-keep class com.zsp.amap.** { *; }
-dontwarn com.zsp.amap.**