# MainActivity.java (TV, WebView): the page calls the bridge by name
-keepclassmembers class com.israeltv.plus.MainActivity$Bridge {
    @android.webkit.JavascriptInterface <methods>;
}
# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers @kotlinx.serialization.Serializable class com.israeltv.plus.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn org.slf4j.**
# Google Cast OptionsProvider loaded via reflection from AndroidManifest
-keep class com.israeltv.plus.data.CastOptionsProvider { *; }
