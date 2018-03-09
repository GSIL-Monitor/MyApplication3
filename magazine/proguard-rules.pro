# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Software\AndroidSDK/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature
-dontwarn android.support.**
-dontwarn com.squareup.**
-dontwarn okio.**
#这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
#-keep class com.yuwen.receiver.MessageReceiver {*;}

#66支付
-keep class com.ut.*
-keep class com.alipay.** { *; }
-keep class com.eagle.pay66.** {*;}
-dontwarn com.alipay.**
-dontwarn com.eagle.pay66.**



#AndFix
-keep class * extends java.lang.annotation.Annotation
-keepclasseswithmembernames class * {
    native <methods>;
}
