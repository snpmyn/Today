-keep class com.umeng.** {*;}
-keep class org.repackage.** {*;}
-keep class com.uyumao.** { *; }

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留 com.zsp.youmeng 命名空间下所有类
# 防止被混淆或裁剪
-keep class com.zsp.youmeng.** { *; }
-dontwarn com.zsp.youmeng.**