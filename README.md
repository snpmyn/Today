## 知伴
## USER
* [code](user/code)
* [Signature File](user/SignatureFile)
* [permission](user/permission)
* [token](user/token)
## 排查
```
TODO
assert
暂无引用
@SuppressLint("NewApi")
@SuppressLint("RestrictedApi")
@SuppressLint("InflateParams")
@SuppressLint("DrawAllocation")
@SuppressLint("MissingPermission")
@SuppressLint("ClickableViewAccessibility")
@SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
tools:targetApi="29"
tools:ignore="RelativeOverlap"
tools:ignore="InefficientWeight"
tools:ignore="UseCompatTextViewDrawableXml"
tools:ignore="RequestInstallPackagesPolicy"
tools:ignore="AllFilesAccessPolicy,ScopedStorage"
android:fitsSystemWindows="true"
inputStream = new FileInputStream(file)
//noinspection TryFinallyCanBeTryWithResources
com.google.android.material.R.attr.colorSurface
```
## 样式
```
如果目标是支持更广泛设备，尤其是低版本 Android，那么 MaterialComponents (MDC / M2) 更为合适。
如果目标是最新的设计理念，且版本是 Android 12+，建议用 Material3 带来更现代化外观和交互体验。

MaterialComponents (MDC / M2)
兼容性强，适用更多旧设备和版本。

Material3
更现代化，更符合 Android 12+ 设计理念，适合新项目和更新的设备。
```
#### M2 + MaterialToolbar
```
style="@style/Widget.MaterialComponents.Toolbar.Surface"
```
#### M3 + MaterialToolbar
```
@style/Widget.Material3.Toolbar
android:layout_height="?actionBarSize"
android:background="?attr/colorSurface"
app:titleTextAppearance="@style/TextAppearance.Material3.TitleMedium.Emphasized"
返回箭头图标使用 M3 的 ⭐ OnSurface ⭐ 色
右侧菜单图标使用 M3 的 ⭐ OnSurface ⭐ 色
```
#### Button + MaterialButton
无法用 Material3
```
style="?attr/materialButtonTonalStyle"
style="?attr/materialIconButtonFilledTonalStyle"
style="@style/Widget.MaterialComponents.Button.TextButton.Dialog"
```
## 适配
```
XML - @android:color/white
代码 - Color.WHITE

XML - ?attr/colorPrimary
代码 - getColorFromAttrResIdWithTypedArray(context, androidx.appcompat.R.attr.colorPrimary)
Theme.Material3Expressive.DayNight.NoActionBar
这是 Material3 的 Expressive 子主题，它不是从 AppCompat 继承，但它依然定义了 colorPrimary 等一整套属性。
Material3 并没有把 colorPrimary 放到 com.google.android.material.R.attr 里，它继承用的就是 AppCompat 定义的 attr。
即使你的主题是 Material3，代码里仍然用 androidx.appcompat.R.attr.colorPrimary，因为这个属性的 ID 是在 appcompat 包里声明的。
```
## 切换
```
fragment_home_page_child.xml

轮播图
<widget.kotlin.banner.view.BannerView
android:visibility="gone"

首页滑动
<com.google.android.material.appbar.CollapsingToolbarLayout
app:layout_scrollFlags="scroll|exitUntilCollapsed"
app:layout_scrollFlags="noScroll"
```
## 备用
```
Android 6.0+ (API 23+) 废弃 Apache HttpClient (DefaultHttpClient、HttpGet、HttpPost)
老项目仍需则于 build.gradle 临时启用
useLibrary 'org.apache.http.legacy'
```
## 发版
```
数据库表更新需强制更新
应用设置更新需额外处理
```
## 研究
```
首页标题左间距
WebView 背景色
ToastKit 居中显示
ProtectionLayout
研究 LoadingIndicator

WindowKit.java
android.R.color.transparent

dialog_user_agreement_and_privacy_policy.xml
android:textColor="?attr/colorOnSurface"

Lottie 动画颜色适配
lottie_animation_status_empty_with_animation.json
lottie_animation_status_loading_with_animation.json

通过 MediaFileInfoHelper 获取不到下方文件名中时间
content://media/external/file/心声 ❤️ -2025-0928-01-55-08.mp3

ic_launcher_background 中仍用 @color/basic 引用方式以 basic 为背景。替换后背景透明，无法替换。
```