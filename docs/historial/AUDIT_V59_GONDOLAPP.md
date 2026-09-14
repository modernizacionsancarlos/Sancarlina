# AUDITORÍA V59 — GondolApp Android

**Fecha:** 12 jun 2026  
**Auditor:** Revisión estática + comandos Gradle (sin edición de código)  
**Repo:** `modernizacionsancarlos/Sancarlina`  
**Package:** `com.sancarlina.app`  
**Objetivo:** Versión **59** estable para beta cerrada / producción en Google Play  

**Leyenda de fuentes:**
- **[CÓDIGO]** — Confirmado en archivos del repo (archivo:línea).
- **[GRADLE]** — Confirmado por ejecución de comandos en esta auditoría.
- **[CONSOLA]** — No verificable sin Firebase / Google Cloud / Play Console.
- **[RECOM]** — Recomendación opcional basada en buenas prácticas oficiales.

---

## 1. Veredicto

| Campo | Valor |
|-------|-------|
| **Estado general** | Base técnica sólida (Compose, MVVM, Firebase, release con R8). Bloqueadores de permisos, firma release y políticas Play impiden publicación inmediata. |
| **¿Publicable hoy?** | **No** |
| **Bloqueadores reales (P0)** | `CAMERA` no declarada; permisos GPS declarados sin uso; API Key Maps en manifest; `keystore.properties` ausente → APK/AAB release no firmable; backup sin exclusiones de prefs cifradas. |
| **Riesgo mayor** | Rechazo o suspensión en Play por permisos no justificados (Data safety / política de permisos) + QR roto en dispositivos por `CAMERA` faltante + abuso de API Key si no está restringida en consola. |

**Nota versión 59:** En repo actual `versionCode = 57`, `versionName = "8.1.0"` (`app/build.gradle.kts:21-22`). Para V59 habrá que subir a `versionCode = 59` en la fase de release (no aplicado en esta auditoría).

---

## 2. Evidencia de comandos

| Comando | Resultado | Errores / warnings relevantes |
|---------|-----------|-------------------------------|
| `./gradlew clean` | **BUILD SUCCESSFUL** (21s) | — |
| `./gradlew :app:assembleDebug` | **BUILD SUCCESSFUL** (1m 34s) | 6 warnings Kotlin (APIs deprecadas, unchecked cast). Strip nativo: `libbarhopper_v3.so`, etc. empaquetados sin strip. |
| `./gradlew :app:lintDebug` | **BUILD SUCCESSFUL** (2m 19s) | **45 warnings, 2 hints**, 0 errores. Reporte: `app/build/reports/lint-results-debug.html` |
| `./gradlew :app:testDebugUnitTest` | **BUILD SUCCESSFUL** (1m 6s) | 1 test ejecutado (`ExampleUnitTest.addition_isCorrect`) |
| `./gradlew :app:assembleRelease` | **BUILD FAILED** (4m 2s) | R8/minify **OK**. Falla en `:app:packageRelease`: `SigningConfig "release" is missing required property "storeFile".` Causa: `keystore.properties` no existe en raíz del proyecto. |

---

## 3. Snapshot técnico (A)

### Versiones y SDK **[CÓDIGO]**

| Campo | Valor | Evidencia |
|-------|-------|-----------|
| applicationId | `com.sancarlina.app` | `app/build.gradle.kts:18` |
| namespace | `com.sancarlina.app` | `app/build.gradle.kts:10` |
| versionCode | `57` | `app/build.gradle.kts:21` |
| versionName | `8.1.0` | `app/build.gradle.kts:22` |
| minSdk | `24` | `app/build.gradle.kts:19` |
| targetSdk | `36` | `app/build.gradle.kts:20` |
| compileSdk | `36` (minor 1) | `app/build.gradle.kts:11-14` |

### Dependencias principales **[CÓDIGO]** `gradle/libs.versions.toml`

| Área | Versión | Notas |
|------|---------|-------|
| AGP | 9.2.1 | Actual |
| Kotlin | 2.2.10 | Actual |
| Compose BOM | 2026.02.01 | Actual |
| Firebase BOM | 33.10.0 | Actual |
| Maps Compose | 6.4.4 | — |
| Play Services Maps | 19.0.0 | — |
| CameraX | 1.4.1 | Nativo (ML Kit barcode) |
| ML Kit Barcode | 17.3.0 | Nativo |
| security-crypto | **1.1.0-alpha06** | **[RECOM]** Versión alpha; considerar estable cuando exista |
| core-ktx | 1.10.1 | Lint: **GradleDependency** obsoleto (7 avisos) |
| lifecycle | 2.6.1 | Lint: obsoleto vs líneas más nuevas en código (`2.8.7` inline en `build.gradle.kts:80`) |

### R8 / Release **[CÓDIGO]** + **[GRADLE]**

| Opción | Estado | Evidencia |
|--------|--------|-----------|
| `isMinifyEnabled` | `true` (release) | `app/build.gradle.kts:43` |
| `isShrinkResources` | `true` | `app/build.gradle.kts:44` |
| ProGuard | `proguard-android-optimize.txt` + `proguard-rules.pro` | `app/build.gradle.kts:49-51` |
| NDK debug symbols | `FULL` | `app/build.gradle.kts:46-47` |
| R8 en assembleRelease | Compiló y minificó | **[GRADLE]** task `minifyReleaseWithR8` OK antes del fallo de firma |
| `lintVitalRelease` | OK | **[GRADLE]** ejecutado en assembleRelease |

---

## 4. Bloqueadores P0

| ID | Problema | Evidencia | Impacto | Fix mínimo | Riesgo si no se hace |
|----|----------|-----------|---------|------------|----------------------|
| **P0-1** | Permiso `CAMERA` **no declarado** en manifest | Manifest: líneas 6-10 solo INTERNET, POST_NOTIFICATIONS, LOCATION×2, NETWORK_STATE. Uso cámara: `QrScannerContent.kt:50-61` (`Manifest.permission.CAMERA`) | QR scanner puede fallar; incumple declaración de permisos Android | Agregar `<uses-permission android:name="android.permission.CAMERA" />` | Función core de puntos inutilizable |
| **P0-2** | `ACCESS_FINE/COARSE_LOCATION` declarados **sin uso de GPS** | Manifest: `AndroidManifest.xml:8-9`. Mapa: `MapContent.kt:44` `myLocationButtonEnabled = false`. No hay `FusedLocationProvider`, `LocationServices`, `getLastLocation`. `MapViewModel.onPermissionResult` existe (`MapViewModel.kt:166-167`) pero **sin llamadas** en UI | Play Data safety / política de permisos; usuario ve permisos innecesarios | **Eliminar** ambos permisos del manifest (recomendado para V59) **o** implementar “mi ubicación” + runtime request | Rechazo en revisión Play o baja confianza |
| **P0-3** | Google Maps API Key **hardcodeada** en manifest | `AndroidManifest.xml:25-27` valor `AIzaSyDLi…PKeM` (enmascarada) | Abuso de cuota / costos si key sin restricción | Secrets Gradle Plugin o `local.properties` + `manifestPlaceholders`; añadir key patterns a `.gitignore` | Facturación / bloqueo de Maps |
| **P0-4** | **Release no firmable** | `keystore.properties` **no existe** (`Test-Path` → False). `app/build.gradle.kts:30-37` solo carga signing si existe. **[GRADLE]** `assembleRelease` → `storeFile` missing | No hay AAB/APK release para Play | Crear `keystore.properties` local (no commitear) + keystore; o CI secrets | Imposible subir a Play |
| **P0-5** | Backup activo sin exclusiones de datos locales | `allowBackup="true"`: `AndroidManifest.xml:14`. `backup_rules.xml:8-13` plantilla vacía. `data_extraction_rules.xml:6-12` sin include/exclude. Prefs: `PrefsManager.kt:15-16` `secure_sancarlina_prefs`, legacy `sancarlina_prefs:21` | Posible backup cloud/transfer de preferencias sensibles | Exclusiones XML concretas (ver sección D) | Incumplimiento privacidad / SC-6 |

---

## 5. Importantes P1

| ID | Problema | Evidencia | Impacto | Fix mínimo | Riesgo si no se hace |
|----|----------|-----------|---------|------------|----------------------|
| **P1-1** | Offline: UI existe, **sin detección de red** | `NetworkConnectivityObserver` en `ConnectivityObserver.kt:20-47`. **No referenciado** en `AppContainer.kt`, `MainScaffold.kt`, `NavGraph.kt`. Ruta `Screen.Offline` + `OfflineContent` en `NavGraph.kt:297-302` pero **ningún `navigate(Screen.Offline)`** en el proyecto | Usuario sin feedback automático ante pérdida de red | `LaunchedEffect` en `MainScaffold` observando `NetworkConnectivityObserver` → `navigate(Offline)` / pop al recuperar | UX rota en campo |
| **P1-2** | Eliminación de cuenta **sin re-autenticación** | `EditProfileViewModel.kt:94-110`: delete Firestore + `user.delete()`. Sin `reauthenticate`, `EmailAuthProvider`, ni manejo de `FirebaseAuthRecentLoginRequiredException` | Borrado falla en sesiones antiguas; requisito Play “borrar cuenta” incompleto | Diálogo password → `reauthenticate` → delete | Usuarios atrapados / incumplimiento política |
| **P1-3** | Reglas Firebase **no auditable en repo** | Búsqueda `**/*.rules` → 0 archivos | Seguridad backend desconocida desde CI | Publicar rules en repo o exportar desde consola y revisar | Escritura fraudulenta de puntos/rol |
| **P1-4** | Cliente escribe `role` y `points` al crear perfil | `UserRepository.kt:12-31` — `role: citizen`, `points`/`points_balance: 0` en `set(..., merge)` | Si rules permisivas, escalación de privilegios | Rules: prohibir que cliente modifique `role`, `points`, `points_balance` | Fraude de puntos |
| **P1-5** | Puntos en runtime: solo Cloud Function para **award** | Escritura puntos award: `PointsRepository.kt:19-21` → `awardPoints`. Lectura: `UserRepository.kt:34-37`, `ProfileViewModel.kt:39`. Perfil update **no** incluye puntos: `EditProfileViewModel.kt:69-73` | Diseño correcto para award; riesgo en rules | Validar en **[CONSOLA]** que cliente no puede `.update` puntos | Manipulación de saldo |
| **P1-6** | `keystore.properties` **no está en `.gitignore`** | `.gitignore` raíz: sin entrada keystore. `keystore.properties` ausente pero patrón no protegido | Riesgo de commit accidental de secretos | Añadir `keystore.properties`, `*.jks`, `*.keystore` | Filtración de firma Play |
| **P1-7** | `google-services.json` **trackeado en git** | `git ls-files` → `app/google-services.json` | API keys Firebase en historial git | Restringir en Firebase Console; **[RECOM]** no commitear en repos públicos | Abuso de proyecto Firebase |
| **P1-8** | Política de privacidad **solo in-app**, sin URL pública | `LegalContent.kt:64-65` texto estático. Sin `https://` política en código/recursos | Play exige URL pública en ficha | Publicar URL municipal y enlazarla | Rechazo ficha Play |
| **P1-9** | Icono launcher inconsistente | Manifest usa `@drawable/ic_sancarlina_logo` (`AndroidManifest.xml:17-19`). Existen adaptive icons `mipmap-anydpi-v26/ic_launcher.xml` no usados como icon de app. Lint: **IconLauncherShape** (2), **IconLocation** (7) | Calidad visual Play / adaptive icon | Unificar `@mipmap/ic_launcher` o adaptive icon oficial GondolApp | Ficha store inconsistente |

---

## 6. Pulido P2 / P3

| ID | Problema | Evidencia | Fix mínimo |
|----|----------|-----------|------------|
| **P2-1** | Accesibilidad: íconos interactivos sin `contentDescription` | Ver sección H (lista priorizada) | `stringResource` + descripciones en botones back, menú, tune, bottom nav |
| **P2-2** | Tests solo de plantilla | `ExampleUnitTest.kt:14-15` (2+2=4). `ExampleInstrumentedTest.kt:22` (package name) | 3-5 smoke tests instrumentados (sección J) |
| **P2-3** | Branding mixto | `strings.xml:2` `Gondolapp`; `MainScaffold.kt:66` `GONDOLAPP`; `LegalContent.kt:59` `GondolApp`; paquete `com.sancarlina.app`; `Logger.kt:7` tag `GondolApp` | Unificar nombre visible **sin cambiar** `applicationId` |
| **P2-4** | `UpdatesContent.kt` huérfana | Existe `ui/features/updates/UpdatesContent.kt:24` — **no** en `Screen.kt` ni `NavGraph.kt` | Integrar o eliminar antes de V59 |
| **P2-5** | Lint 45 warnings | `lint-results-debug.html` — ver tabla sección L | Limpiar antes de producción |
| **P2-6** | `LegalContent` versión desactualizada | `LegalContent.kt:74` dice `Versión 1.0.0 (2026)` vs `8.1.0` en Gradle | Alinear texto legal con versionName |
| **P3-1** | Assets Stitch HTML (35 archivos) empaquetados en APK | `app/src/main/assets/stitch/**/*.html` — no referenciados desde Kotlin | Excluir del release o mover fuera de `assets/` para reducir tamaño |
| **P3-2** | `security-crypto` alpha | `libs.versions.toml:24` | Monitorear release estable |
| **P3-3** | Mock data en producción | `MapViewModel.kt:83-118`, `CategoryListViewModel` mocks, `FavoritesContent.kt:33` | Asegurar que mocks solo son fallback con telemetría |

---

## 7. Detalle por área

### B) Manifest y permisos **[CÓDIGO]**

**Permisos declarados** (`AndroidManifest.xml:6-10`):
1. `INTERNET`
2. `POST_NOTIFICATIONS`
3. `ACCESS_FINE_LOCATION`
4. `ACCESS_COARSE_LOCATION`
5. `ACCESS_NETWORK_STATE`

**CAMERA:** **NO declarada** (confirmado: manifest líneas 6-10).

**Uso cámara:** `QrScannerContent.kt:48-62` — check + `RequestPermission` + CameraX (`ProcessCameraProvider`, líneas 8-9 imports).

**Ubicación:** Declarada pero **no hay API de ubicación del dispositivo**. “Location” en app = campo texto perfil (`EditProfileViewModel.kt:38,72`) y filtros por `locationName` de comercios (`MapViewModel.kt:128-131`). **Propuesta V59:** eliminar permisos GPS del manifest.

**Componentes exportados:** Solo `MainActivity` `android:exported="true"` (`AndroidManifest.xml:29-38`). Sin `service`, `receiver`, `provider`.

**Runtime permissions implementados:**
- Notificaciones: `MainActivity.kt:27-30`
- Cámara: `QrScannerContent.kt:54-61`
- Ubicación GPS: **no implementado**

---

### C) Google Maps API Key **[CÓDIGO]**

| Ubicación | ¿Key? |
|-----------|-------|
| `AndroidManifest.xml:25-27` | **Sí** (hardcoded) |
| `strings.xml` | No |
| `build.gradle.kts` | No |
| Código Kotlin | No |

**Solución segura repo [RECOM]:**
1. `local.properties`: `MAPS_API_KEY=...` (ya en `.gitignore:21`)
2. `app/build.gradle.kts`: `manifestPlaceholders["MAPS_API_KEY"] = ...`
3. Manifest: `android:value="${MAPS_API_KEY}"`
4. Alternativa: [Secrets Gradle Plugin](https://developer.android.com/studio/build/secrets)
5. `.gitignore`: añadir `secrets.properties`, `keystore.properties`, `*.jks`

**Restricción real [CONSOLA] — Google Cloud Console:**
- **APIs & Services → Credentials →** seleccionar key
- Application restrictions: **Android apps**
- Package: `com.sancarlina.app`
- SHA-1: certificado **debug**, **upload key** y/o **Play App Signing** (Play Console → Setup → App signing)
- API restrictions: solo **Maps SDK for Android**

También existe key en `app/google-services.json` (Firebase) — revisar en **[CONSOLA]** Firebase / GCP.

---

### D) Backup / privacidad local **[CÓDIGO]**

| Elemento | Estado |
|----------|--------|
| `allowBackup` | `true` — `AndroidManifest.xml:14` |
| `fullBackupContent` | `@xml/backup_rules` — plantilla vacía `backup_rules.xml:8-13` |
| `dataExtractionRules` | plantilla vacía `data_extraction_rules.xml:7-12` |

**Almacenamiento local detectado:**
- `EncryptedSharedPreferences` → `secure_sancarlina_prefs` (`PrefsManager.kt:13-16`)
- Legacy migrado: `sancarlina_prefs` (`PrefsManager.kt:21-40`)
- Claves: `onboarding_completed`, `guide_completed` (`PrefsManager.kt:44-57`)

**Exclusiones propuestas [RECOM] (aplicar en Fase 2):**

`res/xml/backup_rules.xml` (Android ≤11 full backup):
```xml
<full-backup-content>
    <exclude domain="sharedpref" path="secure_sancarlina_prefs.xml"/>
    <exclude domain="sharedpref" path="sancarlina_prefs.xml"/>
</full-backup-content>
```

`res/xml/data_extraction_rules.xml` (Android 12+):
```xml
<data-extraction-rules>
    <cloud-backup>
        <exclude domain="sharedpref" path="secure_sancarlina_prefs.xml"/>
        <exclude domain="sharedpref" path="sancarlina_prefs.xml"/>
    </cloud-backup>
    <device-transfer>
        <exclude domain="sharedpref" path="secure_sancarlina_prefs.xml"/>
        <exclude domain="sharedpref" path="sancarlina_prefs.xml"/>
    </device-transfer>
</data-extraction-rules>
```

**`allowBackup=false` vs selectivo:** Para app con auth municipal, **[RECOM]** backup **selectivo** (excluir prefs) mantiene restore de datos no sensibles. `allowBackup=false` es más drástico pero válido si no necesitás transferencia.

---

### E) Firebase / backend **[CÓDIGO]** + **[CONSOLA]**

| Ítem | Estado |
|------|--------|
| `firestore.rules` en repo | **No existe** |
| `storage.rules` en repo | **No existe** |
| Colección `users` en código Kotlin | **No** — solo `userProfiles` (`EditProfileViewModel.kt:32,77,101`; `FirestoreCollections.kt:9`) |
| Nota `firebase_security_audit.txt` sobre `users` | **Desactualizada** respecto al código actual |

**Escrituras cliente detectadas:**

| Operación | Archivo | Colección |
|-----------|---------|-----------|
| Crear/merge perfil | `UserRepository.kt:22-31` | `userProfiles` (+ role, points) |
| Update perfil | `EditProfileViewModel.kt:77` | `userProfiles` (nombre, tel, location) |
| Delete perfil | `EditProfileViewModel.kt:101` | `userProfiles` |
| Submit formulario | `SubmissionsRepository.kt:22-24` | `Submissions` |
| Award puntos | `PointsRepository.kt:19-21` | Cloud Function `awardPoints` |
| `AuditLogs` | Solo constante `FirestoreCollections.kt:8` | Sin escritura en app |

**Reglas mínimas sugeridas [CONSOLA] — validar en Firebase Console → Firestore → Rules:**

```
// REQUIERE VALIDACIÓN EN CONSOLA — no deploy desde este doc sin revisión
match /userProfiles/{userId} {
  allow read: if request.auth != null && request.auth.uid == userId;
  allow create: if request.auth != null && request.auth.uid == userId
    && !('role' in request.resource.data) || request.resource.data.role == 'citizen';
  allow update: if request.auth != null && request.auth.uid == userId
    && !request.resource.data.diff(resource.data).affectedKeys()
      .hasAny(['role', 'points', 'points_balance']);
  allow delete: if request.auth != null && request.auth.uid == userId;
}
match /Submissions/{id} {
  allow create: if request.auth != null;
  allow read, update, delete: if false; // solo admin vía custom claims
}
```

Pantallas consola: **Firebase Console → Firestore Database → Rules**; **Storage → Rules**; **Functions** → verificar `awardPoints` en región `southamerica-east1` (`FirestoreCollections.kt:14`).

---

### F) Eliminación de cuenta **[CÓDIGO]**

| Paso | ¿Implementado? | Evidencia |
|------|----------------|-----------|
| UI confirmación | Sí | `EditProfileContent.kt:148` |
| Delete Firestore | Sí | `EditProfileViewModel.kt:101` |
| `user.delete()` | Sí | `EditProfileViewModel.kt:104` |
| `reauthenticate` | **No** | Sin matches en `app/src/main` |
| Manejo `RecentLoginRequired` | **No** | Error genérico `EditProfileViewModel.kt:109` |

**Flujo correcto [RECOM]:**
1. Usuario confirma + ingresa contraseña actual
2. `EmailAuthProvider.getCredential(email, password)` → `user.reauthenticate(credential)`
3. Borrar `userProfiles/{uid}`
4. `user.delete()`
5. `catch (FirebaseAuthRecentLoginRequiredException)` → pedir re-login

---

### G) Offline / conectividad **[CÓDIGO]**

| Pieza | Conectada |
|-------|-----------|
| `NetworkConnectivityObserver` | Implementada, **no usada** |
| `Screen.Offline` | Definida `Screen.kt:22` |
| `OfflineContent` composable | `NavGraph.kt:297-302` |
| Navegación automática a Offline | **No** — cero `navigate.*Offline` |
| `MainScaffold` | Sin observer de red |

**Patch mínimo [RECOM] (Fase 2):** En `MainScaffold.kt`, `LaunchedEffect` + `NetworkConnectivityObserver(context).observe().collect` → si `Lost/Unavailable` y ruta ≠ Offline → `navigate(Screen.Offline)`; si `Available` y en Offline → `popBackStack`.

---

### H) Accesibilidad Compose **[CÓDIGO]**

**Clasificación:** Íconos en `IconButton` / navegación = **interactivos** (requieren descripción). Imágenes hero / ilustración = **decorativos** (`contentDescription = null` aceptable).

#### Prioridad alta — interactivos sin descripción

| Pantalla | Líneas | Elemento |
|----------|--------|----------|
| `MapContent.kt` | 73, 83 | Menú, Filtros |
| `MainScaffold.kt` | 76 | Drawer Home icon |
| `QrScannerContent.kt` | 102 | Volver |
| `CategoryListContent.kt` | 57, 67, 197 | Volver, Filtros, Favorito |
| `NotificationsContent.kt` | 47, 57 | Volver, Ajustes |
| `SearchContent.kt` | 53, 64, 68 | Volver, Buscar, Cerrar |
| `BenefitsContent.kt` | 78 | Abrir QR |
| `CommerceProfileContent.kt` | 124, 204 | Favorito, Volver |
| `HomeContent.kt` | 67 | Buscar |
| Bottom nav | `MainScaffold.kt` `GondolappBottomBar` | Íconos de tabs (verificar líneas ~140+) |

#### Decorativos (OK null) — ejemplos

| Pantalla | Líneas |
|----------|--------|
| `OnboardingContent.kt` | 62, 153 (ilustraciones) |
| `HomeContent.kt` | 155 (banner AsyncImage) |
| `OfflineContent.kt` | 43 (ilustración error) |
| `SuccessContent.kt` | 48 |

**Nota:** Muchos `Icon(ArrowBack, null)` en toolbars — patrón repetido en ~15 pantallas; conviene string `R.string.cd_back`.

---

### I) Branding y Play Store **[CÓDIGO]**

| Nombre | Ubicación |
|--------|-----------|
| `Gondolapp` | `strings.xml:2` (label launcher) |
| `GONDOLAPP` | `MainScaffold.kt:66` |
| `GondolApp` | `LegalContent.kt:59`, `SupportContent.kt:101`, `Logger.kt:7` |
| `Sancarlina` / `com.sancarlina.app` | Paquete, theme, clases |
| `Explorar Sancarlina` | `HomeContent.kt:116` |

**Unificación [RECOM]:** Mantener `applicationId` `com.sancarlina.app` (cambiar package rompe Play si app ya publicada). Unificar **nombre visible** a **GondolApp** en `strings.xml`, drawer, legal, logger tag.

**Privacy policy URL pública:** **No encontrada** en código/recursos (solo texto en `LegalContent.kt`).

**No verificable desde repo [CONSOLA]:**
- Ficha Play (título, descripción corta/larga)
- Screenshots / feature graphic (GP-2)
- Content rating (IARC)
- Data safety form (ubicación, cámara, email, identificadores)
- Política de privacidad URL en store listing
- Lista testers beta cerrada / track Internal vs Closed testing
- Play App Signing SHA-1
- Política de eliminación de cuenta declarada en store

---

### J) Testing **[CÓDIGO]** + **[GRADLE]**

**Existentes:**
- `app/src/test/.../ExampleUnitTest.kt` — placeholder
- `app/src/androidTest/.../ExampleInstrumentedTest.kt` — verifica package

**Smoke tests mínimos propuestos para V59 [RECOM] (no implementar aún):**

1. **Login:** `LoginContent` — campos visibles, botón habilitado con email/password válidos (Compose UI test)
2. **Home:** navegar a Home tras splash mock — no crash, `LazyColumn` presente
3. **Mapa:** `MapContent` — `GoogleMap` composable existe (test con fake/mock ViewModel)
4. **QR:** `QrScannerContent` — sin permiso muestra botón “CONCEDER PERMISO” (`QrScannerContent.kt:83-90`)
5. **Delete account:** `EditProfileViewModel.deleteAccount` — mock Auth, verificar que sin reauth emite error esperado

---

### K) Google Stitch / assets HTML **[CÓDIGO]**

- **35 prototipos HTML** en `app/src/main/assets/stitch/`
- **No integrados** vía WebView (búsqueda `stitch/` en `app/src/main/java` → solo comentarios en `HomeViewModel.kt:23`, `Color.kt:5`, `FavoritesContent.kt:33`)
- Aumentan tamaño APK sin uso runtime

**Mapeo Stitch → Compose (implementado en NavGraph):**

| Stitch (carpeta) | Compose | Estado |
|------------------|---------|--------|
| splash_screen | `SplashContent` | Hecho |
| onboarding_descubre | `OnboardingContent` | Hecho |
| login / registro / recuperar_contrase_a | Login / Register / ForgotPassword | Hecho |
| home | `HomeContent` | Hecho |
| mapa_interactivo / filtros / detalle bottom sheet | `MapContent` | Hecho (sin GPS usuario) |
| escáner_qr / panel_de_puntos / historial | QrScanner / Benefits / PointsHistory | Hecho |
| perfil_de_usuario / editar_perfil | Profile / EditProfile | Hecho |
| legal_y_privacidad / ayuda_y_soporte | Legal / Support | Hecho |
| offline_error | `OfflineContent` | UI sí; trigger no |
| favoritos | `FavoritesContent` | Parcial (mock `FavoritesContent.kt:33`) |
| explora_turismo | `TurismoContent` | Parcial (mock en ViewModel) |
| galería_de_fotos | — | **Pendiente** |
| artesanías | `CategoryListContent`? | Parcial |
| novedades / detalle_de_noticia | `NewsDetailContent` | Hecho |
| sumar_emprendimiento | `EmprendimientoContent` | Hecho |
| guía_rápida | `FeatureDiscovery.kt` | Parcial |
| notificaciones / ajustes | Notifications / NotificationSettings | Hecho |
| servicios_sello | `ServiciosSelloContent` | Hecho |

**Para V59 [RECOM]:** Ignorar HTML Stitch en APK; no bloquear release por brecha visual si Compose cumple flujos core.

---

### L) Release readiness

| Ítem | Estado | Evidencia |
|------|--------|-----------|
| `keystore.properties` en `.gitignore` | **No** | `.gitignore` sin entrada |
| `keystore.properties` local | **Ausente** | `Test-Path` False |
| `assembleRelease` | Falla firma | **[GRADLE]** |
| AAB (`bundleRelease`) | **No probado**; misma firma requerida | Inferido de signing config |
| `proguard-rules.pro` | Reglas Firebase, Maps, Coil, WorkManager | Archivo presente, 44 líneas |
| Launcher manifest | `@drawable/ic_sancarlina_logo.png` | `AndroidManifest.xml:17-19` |
| Adaptive icons | Existen `mipmap-*/ic_launcher*` no usados como app icon | `res/mipmap-*` |

**Lint warnings agrupados [GRADLE]:**

| Tipo | Cantidad |
|------|----------|
| UnusedResources | 17 |
| GradleDependency (obsoletas) | 7 |
| UseKtx | 7 |
| IconLocation | 7 |
| AutoboxingStateCreation | 2 |
| IconLauncherShape | 2 |
| DefaultLocale | 1 |
| OldTargetApi | 1 |
| RedundantLabel | 1 |
| IconDuplicates | 1 |
| UseTomlInstead | 1 |
| **Hints** | 2 |
| **Errores** | **0** |

---

## 8. No verificable desde repo

### Firebase Console (`sancarlina-99748` según `FIRESTORE-SCHEMA.md`)
- Firestore Security Rules efectivas
- Storage Rules
- Cloud Function `awardPoints` desplegada y validación server-side de puntos
- Custom claims `admin` / `role`
- App Check habilitado o no

### Google Cloud Console
- Restricciones API Key Maps
- Cuotas y facturación Maps
- SHA-1 registrados

### Play Console
- App signing certificate
- Tracks (internal / closed / production)
- Data safety, content rating, store listing
- Política eliminación de cuenta (declaración)
- Pre-launch report / pruebas en dispositivos reales
- Compatibilidad 16 KB page size (dispositivos Android 15+)

### Dispositivo / emulador
- QR con permiso cámara en release firmado
- Maps con key restringida
- Flujo delete account con sesión antigua
- TalkBack en pantallas principales

---

## 9. Plan de implementación por fases

### Fase 1 — Fixes P0 sin refactor
1. Declarar `CAMERA` en manifest
2. Eliminar permisos `ACCESS_FINE/COARSE_LOCATION` (o implementar GPS completo si negocio lo exige)
3. Externalizar Maps API Key (`manifestPlaceholders` + `local.properties`)
4. Crear `keystore.properties` local + entradas `.gitignore`
5. Exclusiones backup XML para prefs cifradas
6. Subir `versionCode` a **59** y `versionName` acorde

### Fase 2 — P1 seguridad / UX crítica
1. Wiring `NetworkConnectivityObserver` → `Screen.Offline`
2. Re-autenticación en `deleteAccount`
3. Auditar / desplegar Firestore rules en consola
4. Restringir API Keys en GCP + Firebase
5. URL pública política de privacidad
6. Unificar icono launcher (`@mipmap/ic_launcher`)

### Fase 3 — Accesibilidad / testing / branding
1. `contentDescription` en íconos interactivos (string `cd_*`)
2. 3-5 smoke tests instrumentados
3. Unificar nombre visible GondolApp
4. Limpiar lint warnings críticos
5. Quitar o excluir assets Stitch del APK release

### Fase 4 — Checklist Play Console
1. Subir AAB firmado (v59)
2. Completar Data safety (cámara, email, identificadores; **no** ubicación si se eliminó GPS)
3. Content rating IARC
4. Screenshots + feature graphic
5. Beta cerrada → testers → pre-launch report
6. Verificar delete account en política de la app

---

## 10. Prompt recomendado para implementar Fase 1

Copiar en Cursor (modo Agent):

```
Proyecto: GondolApp Android (com.sancarlina.app), repo Sancarlina.
Objetivo: implementar SOLO Fase 1 del archivo AUDIT_V59_GONDOLAPP.md, sin refactors grandes.

Cambios permitidos:
1. AndroidManifest.xml:
   - Agregar uses-permission CAMERA.
   - Eliminar ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION (no hay uso de GPS en código).
2. Maps API Key:
   - Quitar valor hardcodeado del manifest.
   - Leer desde local.properties (MAPS_API_KEY) vía manifestPlaceholders en app/build.gradle.kts.
   - Documentar en comentario que la key debe restringirse en Google Cloud Console.
3. .gitignore raíz:
   - Añadir keystore.properties, *.jks, *.keystore, secrets.properties
4. backup_rules.xml y data_extraction_rules.xml:
   - Excluir sharedpref secure_sancarlina_prefs.xml y sancarlina_prefs.xml (cloud-backup y device-transfer).
5. app/build.gradle.kts:
   - versionCode 59, versionName 8.1.2 (o el patch que corresponda).

NO hacer:
- Refactor de arquitectura, offline wiring, delete account, tests, branding global, ni tocar Firebase rules.
- No commitear local.properties ni keystore.properties.

Al terminar:
- Ejecutar ./gradlew :app:assembleDebug y :app:lintDebug.
- Reportar diff y resultados de build.
- Indicar que assembleRelease seguirá fallando hasta que el usuario cree keystore.properties localmente.
```

---

*Fin del reporte — generado sin modificación de código fuente de la app.*
