import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.secrets)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.sancarlina.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sancarlina.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 81
        versionName = "8.9.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val properties = Properties()
            val propertiesFile = project.rootProject.file("keystore.properties")
            if (propertiesFile.exists()) {
                properties.load(propertiesFile.inputStream())
                storeFile = file(properties.getProperty("storeFile"))
                storePassword = properties.getProperty("storePassword")
                keyAlias = properties.getProperty("keyAlias")
                keyPassword = properties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
            // Los builds de desarrollo no suben símbolos ni contaminan las métricas de producción.
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
                nativeSymbolUploadEnabled = false
            }
            configure<com.google.firebase.perf.plugin.FirebasePerfExtension> {
                setInstrumentationEnabled(false)
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "FULL"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Sube el mapping de R8 para que los stack traces de release sean legibles.
            // La subida de símbolos nativos queda apagada: la app no tiene código
            // nativo propio y el paso solo agregaría un punto de fallo al build.
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
                nativeSymbolUploadEnabled = false
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
}

// Guarda de release: sin .env el plugin de secrets cae en .env.example y la app
// se compila con la clave de ejemplo. El build no falla, pero el mapa queda
// inutilizable y solo se descubre abriendo la app ya publicada. Esta tarea corta
// el build de release antes de que eso llegue a Play.
val verifyMapsApiKey = tasks.register("verifyMapsApiKey") {
    group = "verification"
    description = "Verifica que .env tenga una MAPS_API_KEY real antes de compilar release."
    doLast {
        val envFile = rootProject.file(".env")
        val problem = if (!envFile.exists()) {
            "Falta el archivo .env en la raíz del proyecto."
        } else {
            val envProperties = Properties()
            envFile.inputStream().use { envProperties.load(it) }
            val key = envProperties.getProperty("MAPS_API_KEY").orEmpty().trim()
            when {
                key.isEmpty() -> "MAPS_API_KEY está vacía en .env."
                key.contains("YOUR_MAPS_API_KEY") || key.startsWith("PEGAR_AQUI") ->
                    "MAPS_API_KEY todavía tiene el valor de ejemplo en .env."
                else -> null
            }
        }
        if (problem != null) {
            throw GradleException(
                "$problem El build de release quedaría con el mapa inutilizable. " +
                    "Copiá la clave real de local.properties a .env antes de generar el Bundle."
            )
        }
    }
}

// Guarda de versión: Play rechaza un Bundle cuyo versionCode ya fue subido, y lo
// hace recién al final de la subida. Comprobarlo acá convierte diez minutos de
// compilar, firmar y subir en un error inmediato.
val verifyReleaseVersionCode = tasks.register("verifyReleaseVersionCode") {
    group = "verification"
    description = "Verifica que el versionCode no haya sido subido antes a Play."
    val currentVersionCode = android.defaultConfig.versionCode
    val ledger = rootProject.file("docs/PLAY_VERSION_CODES_USADOS.txt")
    doLast {
        if (currentVersionCode == null || !ledger.exists()) return@doLast
        val usedCodes = ledger.readLines()
            .mapNotNull { it.substringBefore("#").trim().toIntOrNull() }
        if (currentVersionCode in usedCodes) {
            val next = (usedCodes.max() + 1)
            throw GradleException(
                "El versionCode $currentVersionCode ya fue subido a Play. " +
                    "Subilo a $next en app/build.gradle.kts antes de generar el Bundle. " +
                    "Los códigos ya usados están en docs/PLAY_VERSION_CODES_USADOS.txt."
            )
        }
    }
}

tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    dependsOn(verifyMapsApiKey)
    dependsOn(verifyReleaseVersionCode)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.browser)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.okhttp)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)

    // CameraX & ML Kit
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.barcode.scanning)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.config)

    implementation(libs.coil.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
