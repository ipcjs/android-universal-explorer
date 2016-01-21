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
     compile 'com.github.ipcjs:universal-explorer:0.2.2'
}

repositories {
    maven{
        url "https://raw.githubusercontent.com/ipcjs/snapshoot-repository/master"
    }
}
```

### ProGurad配置
```progurad
# MenuItem
-keep interface com.ipcjs.explorer.menu.MenuCreator$MenuItem
-keepclassmembers class * {
    @com.ipcjs.explorer.menu.MenuCreator$MenuItem *;
}
```


