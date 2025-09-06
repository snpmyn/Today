# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

################################################官方################################################

#【RxJava RxAndroid】
#【待求证准确否】
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#【retrofit】
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

#【okhttp】
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

#【okio】
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

#【gson】
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }
# 谷歌示例（如下替换）
#-keep class * implements base.BaseGsonBean {*;}

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#【glide】
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#【EventBus】
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode {*;}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

################################################二方################################################

#【Rotate3dAnimation】
#-keep public class com.zsp.library.dialog.sweetalertdialog.Rotate3dAnimation{*;}

#【banner】
#-keep class com.youth.banner.** {*;}

#【RxBus】
-dontwarn util.rxbus.**
-keep class util.rxbus.** {*;}
-keep class util.rxbus.finder.** {*;}
-keep class util.rxbus.thread.EventThread {*;}
-keepattributes *Annotation
-keepclassmembers class ** {
    @util.rxbus.annotation.Subscribe public *;
    @util.rxbus.annotation.Produce public *;
    public void onEvent(**);
    public void onEventMainThread(**);
}

#【LitePal】
-keep class litepal.** {*;}

-keep class * extends litepal.crud.LitePalSupport {*;}

#【matisse】
-dontwarn com.squareup.picasso.**

#【uCrop】
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** {*;}
-keep interface com.yalantis.ucrop** {*;}

#【MobSMS】
# SMSSDK 已混淆处理，再混淆致不可预期错误。混淆脚本如下配，跳过混淆 SMSSDK 操作。
#-keep class com.mob.** {*;}
#-keep class cn.smssdk.** {*;}
#-dontwarn com.mob.**

#【Bugly】
#-dontwarn com.tencent.bugly.**
#-keep public class com.tencent.bugly.** {*;}
#-keep class android.support.** {*;}

#【JPush】
# 下载 4.x 及以上版 proguard.jar 并替 Android SDK "tools\proguard\lib\proguard.jar"。
-dontoptimize
#-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** {*;}

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** {*;}

# 2.0.5 ~ 2.1.7 版有引入 gson 和 protobuf
# 增加混淆配置（2.1.8 版无需）
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#【JAnalytics】
# 混淆脚本如下配，防混淆 SDK 接口。
-keep class cn.jiguang.** {*;}
-keep class android.support.** {*;}
-keep class androidx.** {*;}
-keep class com.google.android.** {*;}

#【Bmob】
-ignorewarnings
-keepattributes Signature,*Annotation*

# keep BmobSDK
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** {*;}

# JavaBean 勿混淆（否则 gson 无法将数据解析成具体对象）
#-keep class * extends cn.bmob.v3.BmobObject {*;}

# keep BmobPush
-dontwarn  cn.bmob.push.**
-keep class cn.bmob.push.** {*;}

# keep okhttp3、okio
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }
-dontwarn okio.**

# keep rx
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# 兼容 6.0 勿混淆 org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}

#【TBS】
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**

-keep class com.tencent.smtt.**{*;}

-keep class com.tencent.tbs.**{*;}

#【pgyer】
-keep class com.pgyer.pgyersdk.**{*;}
-keep class com.pgyer.pgyersdk.**$*{*;}

################################################三方################################################

#【lottie】
-keep class com.airbnb.lottie.samples.** {*;}

#【MMKV 无】
#【timber 无】
#【LeakCanary 无】
#【ButterKnife 无】
#【DoraemonKit 无】
#【PermissionX 无】
#【CircleImageView 无】

################################################平台################################################

#【高德地图（定位）】
#-keep class com.amap.api.location.** {*;}
#-keep class com.amap.api.fence.** {*;}
#-keep class com.autonavi.aps.amapapi.model.** {*;}

################################################其它################################################

#【Serializable】
# Explicitly preserve all serialization members.
# The Serializable interface is only a marker interface, so it wouldn't save them.
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   !private <fields>;
   !private <methods>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}