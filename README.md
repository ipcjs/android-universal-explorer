Universal Explorer
======

## 介绍

只是个用来快速写Demo的工具~~

## ToDo

- [x]当前生成菜单的步骤太麻烦了, 计划使fragment的方法可以生成菜单~~~

## 使用

### gradle配置
```groovy
dependencies {
     compile 'com.github.ipcjs:android-universal-explorer:1.0.0'
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
### 从0.*.*版升级到1.*.*版的方式
全局替换 `com\.ipcjs\.(explorer(sample)?)([;."])` 成 `com\.github\.ipcjs\.$1$3`
