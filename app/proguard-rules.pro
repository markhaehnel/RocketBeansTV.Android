-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keepattributes EnclosingMethod
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-dontwarn okio.**
