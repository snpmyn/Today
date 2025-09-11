## 今日

## USER

* [code](user/code)
* [Signature File](user/SignatureFile)
* [token](user/token)
* [动态申请权限](user/动态申请权限.jpg)

## 排查

```
TODO
assert
暂无引用
@RequiresPermission
@SuppressLint("NewApi")
@SuppressLint("RestrictedApi")
@RequiresApi(api = Build.VERSION_CODES.R)
tools:targetApi="29"
tools:ignore="UseCompatTextViewDrawableXml"
tools:ignore="AllFilesAccessPolicy,ScopedStorage"
android:fitsSystemWindows="true"
inputStream = new FileInputStream(file)
//noinspection TryFinallyCanBeTryWithResources
com.google.android.material.R.attr.colorSurface
```

## 样式

```
如果你的目标是支持更广泛的设备，尤其是低版本 Android，那么 MaterialComponents 更为合适。
如果你的目标是最新的设计理念，且你的应用目标是 Android 12 或更高版本，建议使用 Material3，它能带来更现代化的外观和交互体验。

MaterialComponents：兼容性更强，适用于更多旧设备和版本。
Material3：现代化，更符合 Android 12 及以上的设计理念，适合新项目和更新的设备。
```

#### MaterialButton

已使用 Material3
该主题在用

```
style="@style/Widget.Material3.Button.TextButton"
```

#### MaterialToolbar

无法用 Material3
该主题去除

```
style="@style/Widget.MaterialComponents.Toolbar.Primary"
```

该主题在用

```
style="@style/Widget.MaterialComponents.Toolbar.Surface"
```

#### TextInputLayout

已使用 Material3
该主题在用

```
style="@style/Widget.Material3.TextInputLayout.OutlinedBox.Dense"
style="@style/Widget.Material3.TextInputLayout.OutlinedBox.Dense.ExposedDropdownMenu"
```

#### BottomNavigationView

已使用 Material3
该主题去除

```
style="@style/Widget.Material3.BottomNavigationView.ActiveIndicator"
```

#### ExtendedFloatingActionButton

已使用 Material3
该主题在用

```
style="@style/Widget.Material3.ExtendedFloatingActionButton.Primary"
```

### 颜色

```
颜色 basic 使用场景

场景一
@color/basic
已全部替换为 ?attr/colorPrimary
但 ic_launcher_background 中仍用 @color/basic 引用方式以 basic 为背景。替换后背景透明，无法替换。

场景二
R.color.basic
替换为 R.attr.colorPrimary 报错，无法替换。

场景三
com.zsp.core.R.color.basic
替换为 R.attr.colorPrimary 报错，无法替换。
```

## 备用

```
Android 6.0 移除对 Apache 的 HTTP client 支持 (需添 org.apache.http.legacy.jar)
useLibrary 'org.apache.http.legacy'
```