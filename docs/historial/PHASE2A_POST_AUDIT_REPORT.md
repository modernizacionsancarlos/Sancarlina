# PHASE 2-A — Post Audit Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Commit auditado:** `73497b1` — *Conectar modo sin internet, pedir contraseña al borrar cuenta y unificar marca GondolApp*  
**Base:** `PHASE2A_IMPLEMENTATION_REPORT.md`, plan Fase 2-A  
**Alcance:** Solo lectura + comandos Gradle. Sin modificación de código.

---

## 1. Veredicto

| Pregunta | Respuesta |
|----------|-----------|
| **¿Implementación aceptable?** | **Sí** — cumple el alcance Fase 2-A del plan; build/lint/tests OK. |
| **¿Se puede seguir a Fase 2-B?** | **Sí** — con correcciones menores recomendadas antes de publicar en Play. |

### Bloqueadores (para Play Store / release público)

| ID | Bloqueador |
|----|------------|
| B1 | `privacy_policy_url` sigue siendo **placeholder** (`strings.xml:5`) — Play Data Safety exige URL pública real. |
| B2 | Firma release / `keystore.properties` pendiente (fuera de 2-A; ver `PHASE1_5_RELEASE_SIGNING_REPORT.md`). |

### Riesgos no bloqueantes (seguir desarrollo / Fase 2-B)

| ID | Riesgo |
|----|--------|
| R1 | Delete account: orden Firestore → `user.delete()` puede dejar cuenta Auth viva si el segundo paso falla (`EditProfileViewModel.kt:144-145`). |
| R2 | Delete account: errores genéricos / sesión antigua no visibles en UI porque el diálogo se cierra antes del error (`EditProfileViewModel.kt:135-136`, `EditProfileContent.kt:211-214`). |
| R3 | `BrowserUtils` sin fallback `Intent.ACTION_VIEW` ni feedback al usuario si Custom Tabs falla (`BrowserUtils.kt:12-23`). |
| R4 | Offline: comportamiento de back stack y `popBackStack()` sin red — **requiere prueba manual** en dispositivo. |
| R5 | Branding splash a11y aún dice "Sancarlina" (`SplashContent.kt:67,107`) — visible solo para lectores de pantalla. |
| R6 | Icono adaptive — **requiere prueba visual** en emulador/dispositivo API 26+. |

---

## 2. Estado de comandos

| Comando | Resultado | Warnings / notas relevantes |
|---------|-----------|------------------------------|
| `git status --short` | **Limpio** (sin cambios pendientes) | Fase 2-A ya commiteada en `73497b1`. |
| `./gradlew :app:assembleDebug` | **OK** | BUILD SUCCESSFUL. |
| `./gradlew :app:lintDebug` | **OK** | **0 errores**, 44 warnings, 2 hints (preexistentes; ej. `UseTomlInstead`, `DefaultLocale`, `OldTargetApi`). |
| `./gradlew :app:testDebugUnitTest` | **OK** | Sin fallos reportados. |

**Lint icono launcher:** No hay warnings `IconLauncherShape` en el reporte actual (`lint-results-debug.txt:312`). El cambio a `@mipmap/ic_launcher` eliminó el warning previo del manifest con PNG drawable.

---

## 3. Hallazgos por bloque

| ID | Bloque | Archivo:línea | Hallazgo | Severidad | Recomendación |
|----|--------|---------------|----------|-----------|---------------|
| H-O1 | Offline | `MainScaffold.kt:49` | `NetworkConnectivityObserver` en `remember { }` — no se recrea por recomposición. | — (OK) | Mantener. |
| H-O2 | Offline | `MainScaffold.kt:50-58` | Rutas excluidas: splash, login, register, onboarding, forgot_password, offline — alineado al plan. | — (OK) | Mantener. |
| H-O3 | Offline | `MainScaffold.kt:65-66` | Solo `Lost` y `Unavailable` disparan Offline; `Losing` ignorado — reduce parpadeo. | — (OK) | Mantener. |
| H-O4 | Offline | `MainScaffold.kt:68-71` | `launchSingleTop = true` al navegar a Offline. | — (OK) | Mantener. |
| H-O5 | Offline | `MainScaffold.kt:68` | No hay guard explícito `route != Screen.Offline.route`; se cubre vía exclusión en set (`MainScaffold.kt:57`). | Menor | Opcional: guard explícito para legibilidad. |
| H-O6 | Offline | `MainScaffold.kt:75` | `popBackStack()` sin comprobar valor de retorno; si no hay entrada previa, no crashea (API NavController devuelve `false`). | Menor | Prueba manual; opcional manejar `false` con `navigate(Home)`. |
| H-O7 | Offline | `NavGraph.kt:306-310` | `onRetry` = `popBackStack()`; no navega a Home sin red. Si sigue offline, `MainScaffold` re-navega a Offline. | — (OK) | **Requiere prueba manual** modo avión + REINTENTAR. |
| H-O8 | Offline | `ConnectivityObserver.kt:55` | `distinctUntilChanged()` evita spam de eventos. | — (OK) | Mantener. |
| H-D1 | Delete | `EditProfileContent.kt:144-165` | Diálogo advertencia (paso 1) antes de contraseña. | — (OK) | Mantener. |
| H-D2 | Delete | `EditProfileContent.kt:167-219` | Diálogo contraseña con `PasswordVisualTransformation` (paso 2). | — (OK) | Mantener. |
| H-D3 | Delete | `EditProfileUiState.kt:104-109` | Al cerrar diálogo, `deletePassword` se limpia (`if (!show) ""`). | — (OK) | Mantener. |
| H-D4 | Delete | `EditProfileViewModel.kt:130-133` | Valida contraseña vacía antes de llamar Firebase. | — (OK) | Mantener. |
| H-D5 | Delete | `EditProfileViewModel.kt:141-142` | `EmailAuthProvider.getCredential` + `reauthenticate` correctos para email/password. | — (OK) | Mantener. |
| H-D6 | Delete | `EditProfileViewModel.kt:149-156` | `FirebaseAuthRecentLoginRequiredException` con mensaje específico. | Importante | Reabrir diálogo o mostrar error en pantalla principal (hoy invisible si diálogo cerrado). |
| H-D7 | Delete | `EditProfileViewModel.kt:157-165` | `FirebaseAuthInvalidCredentialsException` reabre diálogo con error. | — (OK) | Mantener. |
| H-D8 | Delete | `EditProfileViewModel.kt:135-136` | Al iniciar delete cierra diálogo (`showDeletePasswordDialog = false`) pero el `CircularProgressIndicator` está dentro del diálogo (`EditProfileContent.kt:176-179`) — usuario sin feedback de carga. | Importante | Mantener diálogo abierto durante `isDeletingAccount` o overlay en pantalla. |
| H-D9 | Delete | `EditProfileViewModel.kt:166-174` | Error genérico: `error` en state pero diálogo cerrado — mensaje no visible en UI principal. | Importante | Mostrar `uiState.error` en `EditProfileContent` fuera del diálogo. |
| H-D10 | Delete | `EditProfileViewModel.kt:144-145` | Orden: borra Firestore **antes** que `user.delete()`. Si `user.delete()` falla, perfil Firestore ya eliminado, Auth activo. | Importante | Fase futura: invertir orden o compensar (recrear doc / Cloud Function) — **no implementar en esta auditoría**. |
| H-D11 | Delete | `EditProfileViewModel.kt:117-120` | Solo email/password; sin Google/Facebook en `AuthViewModel.kt:29-52`. Usuario OAuth no puede re-auth con contraseña. | Importante | Documentar en UI o bloquear delete para no-email; flujo OAuth en fase posterior. |
| H-P1 | Privacy | `strings.xml:4-5` | `privacy_policy_url` = placeholder `https://REEMPLAZAR-POR-URL-MUNICIPAL/privacidad`. | **Bloqueante Play** | Reemplazar por URL municipal oficial antes de publicar. |
| H-P2 | Privacy | `LegalContent.kt:27,81-82` | Abre URL vía `BrowserUtils.openCustomTab`. | — (OK) | Mantener. |
| H-P3 | Privacy | `NavGraph.kt:247-256` | Soporte: Política → Custom Tab; Términos → `Screen.Legal`. | — (OK) | Mantener. |
| H-P4 | Privacy | `BrowserUtils.kt:12-23` | `catch (Exception)` genérico; sin `ActivityNotFoundException` ni fallback `ACTION_VIEW`; sin Toast/Snackbar. | Menor | Añadir fallback browser en fase menor. |
| H-B1 | Branding | `strings.xml:2` | `app_name` = `GondolApp`. | — (OK) | Mantener. |
| H-B2 | Branding | `build.gradle.kts:27,35` | `namespace` y `applicationId` = `com.sancarlina.app` — sin cambio. | — (OK) | Mantener. |
| H-B3 | Branding | `MainScaffold.kt:100,133` | Drawer y top bar usan `stringResource(R.string.app_name)`. | — (OK) | Mantener. |
| H-B4 | Branding | `HomeContent.kt:117` | `home_explore_section` → "Explorar San Carlos". | — (OK) | Mantener. |
| H-B5 | Branding | `LoginContent.kt:62`, `RegisterContent.kt:66` | `contentDescription` = "GondolApp". | — (OK) | Mantener. |
| H-B6 | Branding | `SplashContent.kt:67,107` | a11y: "Sancarlina Isologo" / "Sancarlina Logotipo" — inconsistente con GondolApp. | Menor | Actualizar en fase branding extendida. |
| H-B7 | Branding | `MainScaffold.kt:171` | `GondolappBottomBar` — nombre interno de función, no visible al usuario. | — (aceptable) | Opcional rename interno. |
| H-I1 | Icono | `AndroidManifest.xml:19-21` | `icon` / `roundIcon` → `@mipmap/ic_launcher` / `@mipmap/ic_launcher_round`. | — (OK) | Mantener. |
| H-I2 | Icono | `mipmap-anydpi-v26/ic_launcher.xml:1-5` | Adaptive icons existen (background + foreground). | — (OK) | **Requiere prueba visual** en launcher. |
| H-I3 | Icono | `MainScaffold.kt:132` | Top bar in-app sigue `ic_sancarlina_logo` — según plan 2A-I2. | — (OK) | Mantener. |

---

## 4. Offline

### Qué quedó bien (verificado en código)

- Observer estable con `remember` (`MainScaffold.kt:49`).
- Anti-loop: rutas auth/splash/offline excluidas; `launchSingleTop`; `Losing` no navega; `distinctUntilChanged` en observer.
- Restauración: al volver `Available` en ruta `offline`, `popBackStack()` (`MainScaffold.kt:72-76`).
- Retry no fuerza Home: `NavGraph.kt:307-309` usa solo `popBackStack()`.

### Riesgos residuales

| Riesgo | Detalle | Evidencia |
|--------|---------|-----------|
| Back stack profundo | Offline se apila sobre la pantalla actual; al recuperar red, `pop` vuelve a esa pantalla (comportamiento esperado). | `MainScaffold.kt:68-76` |
| REINTENTAR sin red | Pop a pantalla anterior → observer detecta offline → vuelve a Offline (no loop infinito de `navigate` sobre misma ruta). | `NavGraph.kt:307-309` + `MainScaffold.kt:68-71` |
| `popBackStack()` fallido | Sin crash; usuario podría quedar en Offline si stack vacío — escenario improbable con `startDestination` splash. | `MainScaffold.kt:75` |

**Conclusión offline:** Diseño sólido y alineado al plan. **Riesgo de loops: bajo** (pendiente confirmación manual).

---

## 5. Delete account

### Email/password — estado

| Requisito | Estado |
|-----------|--------|
| UI dos pasos | OK (`EditProfileContent.kt:144-219`) |
| Contraseña enmascarada | OK (`EditProfileContent.kt:208`) |
| Validación vacía | OK (`EditProfileViewModel.kt:130-133`) |
| Re-auth Firebase | OK (`EditProfileViewModel.kt:141-142`) |
| Contraseña incorrecta | OK — reabre diálogo (`EditProfileViewModel.kt:157-165`) |
| Sesión antigua | Parcial — mensaje en state pero diálogo cerrado (`EditProfileViewModel.kt:149-156`) |
| Loading / limpieza estados | Parcial — `deletePassword` limpiado en éxito; loading en diálogo invisible al cerrarlo |

### Otros providers (Google/Facebook/etc.)

- **No hay** `GoogleAuthProvider` ni OAuth en el proyecto (`AuthViewModel.kt:29-52`; única mención en comentario `EditProfileViewModel.kt:119`).
- Usuario registrado solo con proveedor externo (si existiera en Firebase Console) **no podría** completar re-auth por contraseña — flujo no implementado.

### Orden Firestore vs Auth delete

```kotlin
// EditProfileViewModel.kt:144-145
firestore.collection("userProfiles").document(user.uid).delete().await()
user.delete().await()
```

Si `user.delete()` falla tras borrar Firestore, queda **cuenta Auth activa sin documento `userProfiles`** — inconsistencia de datos. **Recomendación (solo diseño, no implementar ahora):**

1. **Opción A (preferida):** `user.delete()` primero; si OK, borrar Firestore (o Cloud Function `onDelete` user).
2. **Opción B:** Transacción/compensación: si `user.delete()` falla, recrear documento o marcar `pendingDeletion`.
3. **Opción C:** Callable Cloud Function que borre ambos atómicamente con Admin SDK.

---

## 6. Política de privacidad

| Pregunta | Respuesta |
|----------|-----------|
| ¿Existe `privacy_policy_url`? | **Sí** — `strings.xml:5` |
| ¿URL real o placeholder? | **Placeholder:** `https://REEMPLAZAR-POR-URL-MUNICIPAL/privacidad` |
| ¿Legal abre link? | **Sí** — `LegalContent.kt:81-82` |
| ¿Soporte abre link? | **Sí** — `NavGraph.kt:254-255` |
| ¿BrowserUtils seguro? | Parcial — log en error, sin fallback UI (`BrowserUtils.kt:21-22`) |

**Antes de Play:** reemplazar URL y verificar que coincida con Play Console Data Safety.

---

## 7. Branding e icono

### Branding visible al usuario

| Ubicación | Texto | Estado |
|-----------|-------|--------|
| Launcher / label | `GondolApp` | OK (`strings.xml:2`, `AndroidManifest.xml:20,34`) |
| Drawer | `GondolApp` | OK (`MainScaffold.kt:100`) |
| Home sección | `Explorar San Carlos` | OK (`strings.xml:3`, `HomeContent.kt:117`) |
| Legal | `GondolApp` + versión dinámica | OK (`LegalContent.kt:67`, `95`) |
| Soporte | `Acerca de GondolApp` | OK (`SupportContent.kt:105`) |
| Splash a11y | `Sancarlina Isologo/Logotipo` | Menor inconsistencia (`SplashContent.kt:67,107`) |
| Top bar imagen | `ic_sancarlina_logo` | Intencional (marca municipal in-app) |

### Nombres internos (aceptables, no visibles)

- Package: `com.sancarlina.app`
- Clases: `SancarlinaApp`, `SancarlinaNavGraph`, theme `Theme.Sancarlina`
- Colores: `SancarlinaPrimary`, etc.

### Icono launcher

- Manifest: `@mipmap/ic_launcher` / `@mipmap/ic_launcher_round` (`AndroidManifest.xml:19-21`)
- Adaptive XML presente (`mipmap-anydpi-v26/ic_launcher.xml`)
- Lint sin errores de icono launcher
- **Prueba visual obligatoria** en dispositivo API 26+

---

## 8. Pruebas manuales obligatorias

- [ ] **Offline Home:** modo avión ON en Home → pantalla Offline; OFF → vuelve automáticamente a pantalla anterior.
- [ ] **Offline Login:** modo avión en Login → **no** debe forzar Offline.
- [ ] **Offline REINTENTAR:** en Offline sin red → REINTENTAR → permanece o vuelve a Offline sin crash.
- [ ] **Delete OK:** cuenta test email/password, contraseña correcta → logout y cuenta eliminada.
- [ ] **Delete contraseña mala:** mensaje "Contraseña incorrecta" en diálogo.
- [ ] **Delete sesión vieja:** (si reproducible) mensaje de re-login visible al usuario.
- [ ] **Privacidad Legal:** tap "Ver política completa en línea" → abre browser (URL placeholder o real).
- [ ] **Privacidad Soporte:** tap "Política de Privacidad" → Custom Tab.
- [ ] **Launcher:** icono adaptive y label `GondolApp` en drawer del sistema.
- [ ] **Back stack:** perder red en pantalla secundaria (ej. Map) → Offline → recuperar red → vuelve a Map.

---

## 9. Prompt recomendado siguiente

No hay bloqueadores de **código** para continuar desarrollo. Los bloqueadores son **operativos** (URL privacidad, firma release) antes de Play.

### Opción A — Correcciones menores pre-Play (Modo Agente, alcance acotado)

```
Proyecto: GondolApp Android (com.sancarlina.app).
Corregir SOLO hallazgos Importantes de PHASE2A_POST_AUDIT_REPORT.md (H-D6, H-D8, H-D9, H-P4).

Reglas:
- Cambios mínimos en EditProfileContent.kt y EditProfileViewModel.kt.
- NO tocar applicationId, keystore, Firebase rules, offline wiring.
- NO inventar privacy_policy_url (el equipo municipal la provee).

Tareas:
1. Durante isDeletingAccount: mantener feedback visible (diálogo abierto o overlay).
2. Mostrar uiState.error en pantalla principal EditProfile, no solo dentro del diálogo.
3. BrowserUtils: fallback Intent.ACTION_VIEW si Custom Tabs falla; Toast breve al usuario.

Validar: assembleDebug, lintDebug, testDebugUnitTest.
NO commitear salvo que se pida.
```

### Opción B — Fase 2-B (Modo Plan, sin bloqueadores de código)

```
Proyecto: GondolApp Android (com.sancarlina.app).
Elaborar plan Fase 2-B según AUDIT_V59_GONDOLAPP.md y PHASE2A_POST_AUDIT_REPORT.md.

Fase 2-A cerrada y aceptable. Excluir de 2-B: keystore/AAB, Firebase rules (salvo que se priorice).

Temas candidatos 2-B (priorizar con el equipo):
- AuthRepository / consolidación auth
- Accesibilidad masiva (contentDescription splash, etc.)
- Tests instrumentados offline + delete account
- Cloud Function delete account atómico
- URL privacidad real + validación Play Data Safety
- Assets launcher / Stitch si el adaptive no refleja marca oficial

Entregable: PHASE2B_PLAN_REPORT.md sin modificar código.
```

---

*Auditoría realizada sin modificar código fuente. Comandos Gradle ejecutados en workspace limpio (post-commit `73497b1`).*
