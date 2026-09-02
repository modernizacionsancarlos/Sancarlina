# -----------------------------------------------------------------------------------------
# GondolApp - Reglas de ProGuard para Producción (Play Store)
# -----------------------------------------------------------------------------------------

# 1. Firebase & Firestore Models
# Esencial para que el mapeo de documentos a clases de Kotlin funcione en release.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.sancarlina.app.data.models.** { *; }
# Algunos DTO de Firestore viven en repository; conservarlos evita fallos exclusivos
# de Release si una pantalla o dependencia vuelve a usar deserialización reflectiva.
-keep class com.sancarlina.app.data.repository.Area { *; }
-keep class com.sancarlina.app.data.repository.Benefit { *; }

# 2. ViewModels y Estados
# Asegura que las data classes usadas para el estado de la UI no sean eliminadas.
-keep class com.sancarlina.app.viewmodel.** { *; }

# 3. Google Maps SDK
# Necesario para el correcto funcionamiento de los mapas en versiones firmadas.
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# 4. Coil (Carga de imágenes)
# Evita errores de carga de imágenes al optimizar recursos.
-keep class coil.** { *; }
-keep interface coil.** { *; }

# 5. BuildConfig
# Previene la ofuscación de constantes de entorno.
-keep class com.sancarlina.app.BuildConfig { *; }

# 6. Reglas Genéricas de Seguridad
# Evita que se eliminen metadatos necesarios para el cifrado (Security Crypto).
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# 7. WorkManager & Startup (FIX: Crash on launch)
# Evita que R8 elimine las clases necesarias para inicializar WorkManager.
-keep class androidx.work.** { *; }
-keep class androidx.startup.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# 8. Native Symbols Support
# Ayuda a que las herramientas de depuración identifiquen métodos nativos.
-keepattributes SourceFile,LineNumberTable
