-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keep public class de.markhaehnel.flux.utility.ChatInterface
-keep public class * implements de.markhaehnel.flux.utility.ChatInterface
-keepclassmembers class de.markhaehnel.flux.utility.ChatInterface {
    <methods>;
}

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable