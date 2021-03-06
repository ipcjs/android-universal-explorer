# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Keep
-keep interface android.support.annotation.Keep
-keepclassmembers @android.support.annotation.Keep class * {*;}
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

# MenuItem
-keep interface com.github.ipcjs.explorer.menu.MenuCreator$MenuItem
-keepclassmembers class * {
    @com.github.ipcjs.explorer.menu.MenuCreator$MenuItem *;
}