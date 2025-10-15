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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================ 基础配置 ============================
-keepattributes SourceFile,LineNumberTable,Signature,*Annotation*,EnclosingMethod
-renamesourcefileattribute SourceFile
-ignorewarnings

# ============================ 官方库 ============================

# -------- RxJava / RxAndroid --------
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

# -------- Retrofit --------
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,InnerClasses,EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# -------- OkHttp / Okio --------
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**

# -------- Gson --------
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# -------- Glide --------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** { **[] $VALUES; public *; }

# -------- EventBus --------
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode {*;}
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# ============================ 三方库 ============================

# -------- RxBus --------
-dontwarn util.rxbus.**
-keep class util.rxbus.** {*;}
-keep class util.rxbus.finder.** {*;}
-keep class util.rxbus.thread.EventThread {*;}
-keepclassmembers class ** {
    @util.rxbus.annotation.Subscribe public *;
    @util.rxbus.annotation.Produce public *;
    public void onEvent(**);
    public void onEventMainThread(**);
}

# -------- LitePal --------
-keep class litepal.** {*;}
-keep class * extends litepal.crud.LitePalSupport {*;}
-dontoptimize

# -------- Pgyer --------
-keep class com.pgyer.pgyersdk.**{*;}
-keep class com.pgyer.pgyersdk.**$*{*;}

# -------- Lottie --------
-keep class com.airbnb.lottie.samples.** {*;}

# ============================ 其它 ============================

# -------- Serializable --------
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

# -------- 保留项目 Model --------
-keep class com.zsp.** { *; }

# -------- 兼容 Android 6.0 HttpClient --------
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