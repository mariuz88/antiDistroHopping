# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Project specific ProGuard / R8 rules.
# For more details, see: http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable



#   Regole Retrofit --> preserva ignorando warning falsi allaRmi
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, AnnotationDefault
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations

#Mantenere le interfacce retroofit e i metodi
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
 
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**




#mantenere classi GSON
-keepattributes *Annotation*
-dontwarn sun.misc.Unsafe
-keep class com.google.gson.stream.** { *; }

#non fare offuscare a r8 le classi co cui usiamo gson
-keep class com.mario.beta_antidh.modello.** { *; }
-keepclassmembers class com.mario.beta_antidh.modello.** { <fields>; }


#mantenere le interfacce di retee classi API
-keep interface com.mario.beta_antidh.rete.** { *; }
-keep class com.mario.beta_antidh.rete.** { *; }
