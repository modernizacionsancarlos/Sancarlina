# PHASE 2B-2 — Smoke Tests Compose (androidTest)

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Alcance:** 5 smoke tests instrumentados + hooks mínimos en producción. Sin Firebase rules, backend, versioning, keystore ni Stitch.

---

## 1. Resumen

Se implementaron **5 smoke tests** en `androidTest` con cambios acotados en **4 archivos de producción** (~30 líneas netas) para permitir pruebas estables sin CameraX, Firestore ni autenticación real.

| Test | Archivo | Qué valida |
|------|---------|------------|
| T1 | `OfflineContentTest.kt` | Mensaje "Sin conexión" + callback REINTENTAR |
| T2 | `LoginContentTest.kt` | "Bienvenido", placeholder email, botón Ingresar (sin login) |
| T3 | `QrScannerPermissionTest.kt` | Estado sin permiso + botón CONCEDER PERMISO |
| T4 | `EditProfileDeleteDialogTest.kt` | Flujo advertencia → contraseña (sin delete real) |
| T5 | `HomeContentTest.kt` | Sección "Explorar San Carlos" con estado fake |

Se mantiene `ExampleInstrumentedTest.kt` (plantilla existente).

---

## 2. Archivos producción modificados

| Archivo | Cambio | Riesgo |
|---------|--------|--------|
| `OfflineContent.kt` | `Modifier.testTag("offline_retry")` en botón REINTENTAR | Muy bajo |
| `QrScannerContent.kt` | Parámetro `forceNoCameraPermission: Boolean = false`; `testTag("qr_grant_permission")` | Bajo (default false) |
| `HomeContent.kt` | Parámetro `uiStateOverride: HomeUiState? = null` | Bajo (default null) |
| `AuthViewModel.kt` | Interfaz `LoginAuthViewModel` para inyección en tests | Bajo |
| `LoginContent.kt` | Tipo del parámetro `viewModel`: `LoginAuthViewModel` (default `viewModel<AuthViewModel>()`) | Bajo |

**Nota:** El plan original indicaba "LoginContent sin cambios". Se añadió la interfaz `LoginAuthViewModel` (1 línea en `LoginContent`) porque `FakeAuthViewModel` no puede extender `AuthViewModel` sin Firebase y `login()` no es overridable.

---

## 3. Archivos androidTest nuevos

| Archivo | Rol |
|---------|-----|
| `ui/OfflineContentTest.kt` | T1 |
| `ui/LoginContentTest.kt` | T2 |
| `ui/QrScannerPermissionTest.kt` | T3 |
| `ui/EditProfileDeleteDialogTest.kt` | T4 (ViewModel real, usuario no logueado) |
| `ui/HomeContentTest.kt` | T5 |
| `test/FakeAuthViewModel.kt` | Fake sin Firebase para login |

**No creado:** `FakeEditProfileViewModel.kt` — el plan adoptó ViewModel real sin usuario autenticado.

---

## 4. TestTags añadidos

| testTag | Ubicación |
|---------|-----------|
| `offline_retry` | `OfflineContent.kt` |
| `qr_grant_permission` | `QrScannerContent.kt` |

---

## 5. Validación Gradle

| Comando | Resultado |
|---------|-----------|
| `:app:assembleDebug` | OK |
| `:app:lintDebug` | OK (0 errores tras fix lint en tests) |
| `:app:testDebugUnitTest` | OK |
| `:app:compileDebugAndroidTestKotlin` | OK |
| `:app:connectedDebugAndroidTest` | **Pendiente** — no hay `adb` en PATH / sin emulador conectado |

### Corrección lint aplicada

Lint `ViewModelConstructorInComposable`: instanciar `FakeAuthViewModel` y `EditProfileViewModel` **fuera** del bloque `setContent { }`.

---

## 6. Cómo ejecutar tests instrumentados

Con emulador o dispositivo conectado y `adb` en PATH:

```bash
.\gradlew.bat :app:connectedDebugAndroidTest
```

Tests incluidos: `OfflineContentTest`, `LoginContentTest`, `QrScannerPermissionTest`, `EditProfileDeleteDialogTest`, `HomeContentTest`, `ExampleInstrumentedTest`.

---

## 7. Fuera de alcance (confirmado)

- Firebase rules / Cloud Functions / backend
- versionCode / versionName / keystore / AAB
- Stitch / mocks adicionales / lint baseline
- NavGraph / cambios de navegación global

---

## 8. Deploy

**No requiere nuevo APK** para usuarios finales: solo tests y hooks con defaults que no alteran comportamiento en producción. Basta deploy habitual si se commitea el código; la app instalada no cambia funcionalidad visible.

**Versión:** sin cambio (`versionCode` 59 / `versionName` 8.1.2).
