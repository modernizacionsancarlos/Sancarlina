# ProGuard rules for Sancarlina / GondolApp

# Firebase Firestore Models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.sancarlina.app.data.models.** { *; }

# Also keep viewmodel states if they are ever used with toObject
-keep class com.sancarlina.app.viewmodel.** { *; }

# Prevent obfuscation of BuildConfig fields if needed
-keep class com.sancarlina.app.BuildConfig { *; }
