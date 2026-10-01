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

# Firestore deserializes model classes by reflection (field names, @PropertyName
# annotations, generic type signatures) - R8 can't see that usage, so without this
# it may rename/strip fields and Firestore reads/writes would silently return nulls
# instead of crashing.
-keepattributes Signature
-keepattributes *Annotation*

-keepclassmembers class com.palaksinghal.mysaarthi.domain.model.** {
    <fields>;
    <methods>;
}