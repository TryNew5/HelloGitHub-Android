# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.hellogithub.app.**$$serializer { *; }
-keepclassmembers class com.hellogithub.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.hellogithub.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
