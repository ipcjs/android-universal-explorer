Universal Explorer
======

[![Release](https://jitpack.io/v/ipcjs/android-universal-explorer.svg)](https://jitpack.io/#ipcjs/android-universal-explorer)

## 介绍

只是个用来快速写Demo的工具~~

## 使用

### gradle配置
```groovy
dependencies {
     implementation 'com.github.ipcjs:android-universal-explorer:1.1.4'
}

repositories {
    maven { url "https://jitpack.io" }
}
```

### ProGurad配置
防止用于生成菜单的方法被shinking~~
```progurad
# MenuItem
-keep interface com.github.ipcjs.explorer.menu.MenuCreator$MenuItem
-keepclassmembers class * {
    @com.github.ipcjs.explorer.menu.MenuCreator$MenuItem *;
}
```
### 从`0.*.*`版升级到`1.*.*`版的方式
全局替换 `com\.ipcjs\.(explorer(sample)?)([;."])` 成 `com\.github\.ipcjs\.$1$3`
