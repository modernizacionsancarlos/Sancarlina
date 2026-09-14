# PHASE 2-A — Implementation Report (GondolApp v59)

**Fecha:** 2026-06-12  
**Base:** Plan Fase 2-A (`fase_2-a_hardening_259760ff.plan.md`)  
**Versión app:** `versionCode` 59 / `versionName` 8.1.2 (sin cambio en esta fase)

---

## 1. Resumen ejecutivo

Se implementaron los 5 bloques de Fase 2-A con cambios acotados: offline automático, eliminación de cuenta con re-autenticación email/password, URL de privacidad parametrizada, branding visible unificado a **GondolApp**, e icono launcher adaptive vía manifest.

**Archivos tocados:** 13 modificados + 1 nuevo (`BrowserUtils.kt`).  
**Fuera de alcance respetado:** applicationId, namespace, keystore, Firebase rules, assets Stitch, AuthRepository.

---

## 2. Cambios por bloque

### 2A-O — Offline real

| ID | Archivo | Cambio |
|----|---------|--------|
| 2A-O1..O3 | `MainScaffold.kt` | `NetworkConnectivityObserver` con `remember` + `LaunchedEffect`; rutas excluidas (splash, login, register, onboarding, forgot_password, offline); `Lost`/`Unavailable` → `navigate(Offline)` con `launchSingleTop`; `Available` en offline → `popBackStack()` |
| 2A-O4..O5 | `NavGraph.kt` | `OfflineContent.onRetry` = `popBackStack()` (no navega a Home sin red) |

### 2A-D — Delete account con re-auth

| ID | Archivo | Cambio |
|----|---------|--------|
| 2A-D1 | `EditProfileUiState.kt` | `deletePassword`, `showDeletePasswordDialog`, `isDeletingAccount` |
| 2A-D2..D3 | `EditProfileViewModel.kt` | `deleteAccount(password)` con `EmailAuthProvider.getCredential` → `reauthenticate` → delete Firestore → `user.delete()`; manejo `FirebaseAuthRecentLoginRequiredException` e `FirebaseAuthInvalidCredentialsException` |
| 2A-D4 | `EditProfileContent.kt` | Flujo 2 pasos: advertencia → diálogo contraseña |

### 2A-P — Política de privacidad

| ID | Archivo | Cambio |
|----|---------|--------|
| 2A-P1 | `strings.xml` | `privacy_policy_url` placeholder, `legal_open_privacy_online`, `home_explore_section`, `app_name` = GondolApp |
| 2A-P2 | `LegalContent.kt` | Botón Custom Tab vía `BrowserUtils` + versión `BuildConfig.VERSION_NAME` |
| 2A-P3 | `SupportContent.kt` | `LegalMenuItem` con `onClick`; Política → Custom Tab; Términos → `Screen.Legal` |
| — | `NavGraph.kt` | Wire callbacks `onNavigateToLegal` / `onOpenPrivacyPolicy` |
| — | `BrowserUtils.kt` | **Nuevo** — helper `openCustomTab` reutilizable |

### 2A-B — Branding mínimo

| ID | Archivo | Cambio |
|----|---------|--------|
| 2A-B1..B2 | `strings.xml`, `MainScaffold.kt` | `app_name` GondolApp; drawer y top bar con `stringResource(R.string.app_name)` |
| 2A-B3 | `LegalContent.kt` | Versión desde `BuildConfig.VERSION_NAME` |
| 2A-B4 | `HomeContent.kt` | `home_explore_section` → "Explorar San Carlos" |
| 2A-B5 | `RegisterContent.kt`, `LoginContent.kt` | `contentDescription` → "GondolApp" |
| 2A-B6 | `ErrorDialog.kt` | "servidores de GondolApp" |

### 2A-I — Launcher manifest

| ID | Archivo | Cambio |
|----|---------|--------|
| 2A-I1 | `AndroidManifest.xml` | `icon` → `@mipmap/ic_launcher`, `roundIcon` → `@mipmap/ic_launcher_round` |
| 2A-I2 | — | `ic_sancarlina_logo` in-app sin cambios |

---

## 3. Diff estadístico

```
13 files changed, 263 insertions(+), 59 deletions(-)
+ app/src/main/java/com/sancarlina/app/utils/BrowserUtils.kt (nuevo)
```

---

## 4. Validación Gradle

| Comando | Resultado |
|---------|-----------|
| `./gradlew :app:assembleDebug` | **OK** |
| `./gradlew :app:lintDebug` | **OK** (0 errores; warnings preexistentes) |
| `./gradlew :app:testDebugUnitTest` | **OK** |

**Nota lint:** Se corrigió 1 error introducido (`LocalContextGetResourceValueCall` en `NavGraph.kt`) usando `stringResource` en lugar de `context.getString`.

---

## 5. Smoke tests manuales recomendados

- [ ] Home → modo avión ON → pantalla Offline; OFF → vuelve automáticamente
- [ ] Login con modo avión → **no** forzar Offline
- [ ] Eliminar cuenta: contraseña correcta / incorrecta / sesión antigua
- [ ] Tap "Política de Privacidad" (Legal y Soporte) → Custom Tab (reemplazar URL placeholder antes de Play)
- [ ] Launcher muestra label **GondolApp** e icono adaptive (API 26+)

---

## 6. Acción requerida del equipo

1. **Reemplazar** `privacy_policy_url` en `strings.xml` por la URL municipal oficial antes de publicar en Play Console.
2. **Validar visualmente** el adaptive icon en dispositivo/emulador.
3. **Release signing** sigue pendiente (`keystore.properties`) — ver `PHASE1_5_RELEASE_SIGNING_REPORT.md`.

---

## 7. Versión / deploy

| Campo | Valor |
|-------|-------|
| Cambio clasificación | **Sutil** (UX, legal links, branding; sin features nuevas mayores) |
| versionName propuesto | **8.1.2** (sin bump en 2-A; bump al publicar APK) |
| versionCode | **59** (sin cambio hasta nuevo APK) |
| Deploy | Solo APK/debug build para probar; **nuevo APK** recomendado tras smoke manual y URL real de privacidad |

---

*Generado tras implementación Fase 2-A. Sin commits en repo.*
