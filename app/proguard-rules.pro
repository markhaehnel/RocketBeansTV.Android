-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepattributes Signature
