# PHASE 2B-2 — Post Audit Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Commit auditado:** `8ab3e3d` — *Agregar 5 pruebas automáticas de pantallas clave (sin conexión, login, QR, borrar cuenta y inicio)*  
**Modo:** Solo lectura — sin modificaciones de código.

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Implementación aceptable?** | **Sí** |
| **¿Se puede cerrar 2B-2?** | **Sí**, con la salvedad de ejecutar `connectedDebugAndroidTest` cuando haya emulador/dispositivo |
| **Bloqueadores** | Ninguno en código ni en build |
| **Riesgos menores** | (1) `PHASE2B2_IMPLEMENTATION_REPORT.md` línea 11 dice “4 archivos” pero la tabla lista **5** — error documental. (2) `HomeContentTest` sigue instanciando `HomeViewModel` real en background (`HomeViewModel.kt:62-63`) aunque la UI use `uiStateOverride` (`HomeContent.kt:47-48`) — posible ruido de red/Firestore en CI, no afecta prod. (3) `EditProfileDeleteDialogTest` usa `EditProfileViewModel` real con SDK Firebase inicializado (`EditProfileViewModel.kt:18-20`) sin usuario logueado — sin delete ni red en el flujo probado. (4) Tests instrumentados no ejecutados en dispositivo en esta auditoría. (5) Desviación menor vs plan: `LoginContent.kt` y `AuthViewModel.kt` cambiaron (interfaz `LoginAuthViewModel`) cuando el plan original decía “LoginContent sin cambios”. |

---

## 2. Estado de comandos

| Comando | Resultado | Errores/warnings relevantes |
|---------|-----------|----------------------------|
| `git status --short` | OK (working tree clean) | Sin cambios pendientes |
| `:app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `:app:lintDebug` | **OK** | 0 errores; warnings preexistentes del proyecto (ej. `DefaultLocale` en `PointsViewModel.kt:74`, `OldTargetApi` en `build.gradle.kts:37`) — no introducidos por 2B-2 |
| `:app:testDebugUnitTest` | **OK** | Sin fallos |
| `:app:compileDebugAndroidTestKotlin` | **OK** | Compila los 5 tests nuevos + plantilla |
| `:app:connectedDebugAndroidTest` | **No ejecutado** | `adb` disponible en `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe` pero **0 dispositivos** conectados |

---

## 3. Cambios de producción auditados

**Conteo real:** **5 archivos** en `app/src/main` (no 4 como dice el resumen del reporte de implementación).

| Archivo | Cambio detectado | ¿Altera comportamiento real? | Riesgo | Recomendación |
|---------|------------------|------------------------------|--------|---------------|
| `OfflineContent.kt:12,78` | `import testTag` + `Modifier.testTag("offline_retry")` en botón REINTENTAR | **No** — solo semántica de test; sin cambio visual ni de click | Muy bajo | Mantener |
| `QrScannerContent.kt:47,54-55,68,95` | Parámetro `forceNoCameraPermission = false`; lógica condicional en permiso; `testTag("qr_grant_permission")` | **No** con default — `NavGraph.kt:188` no pasa el parámetro; prod sigue pidiendo permiso y abriendo cámara | Bajo | Mantener; no exponer `forceNoCameraPermission` en NavGraph |
| `HomeContent.kt:39,47-48` | `uiStateOverride: HomeUiState? = null`; `uiState = uiStateOverride ?: collectedState` | **No** con default — `NavGraph.kt:82-100` no pasa override | Bajo | Mantener |
| `AuthViewModel.kt:13-17,22,25,27` | Interfaz `LoginAuthViewModel`; `AuthViewModel` la implementa con `override` en `uiState` y `login` | **No** — misma implementación; solo contrato explícito | Bajo | Aceptable para inyección en tests |
| `LoginContent.kt:24-25,38` | Tipo `LoginAuthViewModel`; default `viewModel<AuthViewModel>()` | **No** — prod sigue resolviendo `AuthViewModel` real vía factory Hilt/Compose default | Bajo | Documentado como desviación mínima vs plan |

**Verificaciones adicionales:**

| Verificación | Resultado |
|--------------|-----------|
| testTags alteran UI visible | **No** |
| `forceNoCameraPermission` default `false` | **Confirmado** (`QrScannerContent.kt:47`) |
| `uiStateOverride` default `null` | **Confirmado** (`HomeContent.kt:39`) |
| `LoginContent` default `viewModel<AuthViewModel>()` | **Confirmado** (`LoginContent.kt:38`) |
| Dependencias androidTest en `main` | **No** — grep sin coincidencias |
| Imports `androidTest` en producción | **No** |

---

## 4. Tests auditados

| Test | Archivo | Qué valida | Dependencias reales evitadas | Riesgo de fragilidad | Estado |
|------|---------|------------|------------------------------|----------------------|--------|
| T1 Offline | `OfflineContentTest.kt:23-34` | Texto "Sin conexión", click `offline_retry`, callback | Ninguna (composable puro) | **Bajo** — testTag + texto estable | Compila; ejecución en device pendiente |
| T2 Login | `LoginContentTest.kt:21-37` | "Bienvenido", placeholder `tu@email.com`, "Ingresar" | Firebase evitado vía `FakeAuthViewModel.kt:11-18` (login no-op); **no** hace click en Ingresar | **Medio** — strings hardcodeados en español (`LoginContent.kt:79,113,184`) | Compila; ejecución pendiente |
| T3 QR permiso | `QrScannerPermissionTest.kt:21-36` | Mensaje permiso + "CONCEDER PERMISO" + tag | Cámara evitada con `forceNoCameraPermission = true`; no llama `processQrCode` | **Bajo-Medio** — `QrScannerViewModel` se crea igual (`QrScannerContent.kt:44`) pero sin rama cámara | Compila; ejecución pendiente |
| T4 Delete dialog | `EditProfileDeleteDialogTest.kt:23-43` | Flujo Eliminar → ¿Eliminar cuenta? → CONTINUAR → Confirmá contraseña | **No** confirma delete ni ingresa password; Firebase delete no invocado | **Medio** — `EditProfileViewModel` real (`EditProfileViewModel.kt:18-20`); requiere scroll (`:36-38`); strings UI | Compila; ejecución pendiente |
| T5 Home | `HomeContentTest.kt:22-43` | "Explorar San Carlos" + categoría BODEGAS | UI usa `uiStateOverride` (`HomeContentTest.kt:31`); **pero** `HomeViewModel` default sigue activo en composición | **Medio** — string hardcodeado vs `strings.xml:3`; VM real puede tocar Firestore en background (`HomeViewModel.kt:66-76`) | Compila; ejecución pendiente |
| Fake | `FakeAuthViewModel.kt:11-18` | Stub de login para T2 | Sin Firebase Auth | Bajo | OK |

**Calidad general:**

| Criterio | Resultado |
|----------|-----------|
| Sin Firebase Auth real en mayoría de tests | **Sí** — excepto init SDK en T4/T5 (sin operaciones de red en assertions) |
| Sin cámara real en T3 | **Sí** — hook `forceNoCameraPermission` |
| Sin Firestore en assertions T5 | **Sí** — override de UI; VM en background es riesgo CI, no prod |
| Sin red obligatoria para pasar asserts | **Mayormente sí** — T5 podría ser flaky si VM falla ruidosamente (no debería afectar UI override) |
| Sin sleeps/delays | **Sí** — grep sin `sleep`, `delay`, `waitUntil`, `Thread` en androidTest |
| Nombres claros | **Sí** — patrón `feature_scenario` |
| Fakes no ocultan errores críticos | **Aceptable** — smoke tests de render/flujo UI; T4 no valida delete Firebase (explícito en plan) |

**No creado (correcto vs plan adoptado):** `FakeEditProfileViewModel.kt` — se usó VM real sin usuario.

---

## 5. Instrumented tests

| Pregunta | Respuesta |
|----------|-----------|
| ¿`compileDebugAndroidTestKotlin` OK? | **Sí** |
| ¿`connectedDebugAndroidTest` ejecutado? | **No** |
| Motivo | Emulador/dispositivo no conectado (`adb devices` → lista vacía) |
| Comando para ejecutar luego | Desde Android Studio: *Run > Run 'All Tests'* en módulo `app` androidTest, o en terminal con device conectado: `.\gradlew.bat :app:connectedDebugAndroidTest` |
| Tests esperados (6 clases) | `OfflineContentTest`, `LoginContentTest`, `QrScannerPermissionTest`, `EditProfileDeleteDialogTest`, `HomeContentTest`, `ExampleInstrumentedTest` |

---

## 6. Cambios fuera de alcance

Confirmado en commit `8ab3e3d` — **ningún cambio** en:

| Área | ¿Modificado? |
|------|--------------|
| Firebase rules | **No** |
| Backend / Cloud Functions | **No** |
| `versionCode` / `versionName` | **No** — siguen `59` / `8.1.2` (`build.gradle.kts:38-39`) |
| Keystore / AAB / firma | **No** |
| Assets Stitch | **No** |
| Navegación global (`NavGraph.kt`) | **No** — sin cambios en commit 2B-2 |

---

## 7. Próximo paso recomendado

**Todo OK para cerrar 2B-2** con estas acciones opcionales antes o después del cierre:

1. Ejecutar `.\gradlew.bat :app:connectedDebugAndroidTest` con emulador API 35+ conectado (validación final de los 5 smoke tests).
2. Corregir en documentación el conteo “4 archivos” → **5 archivos** en `PHASE2B2_IMPLEMENTATION_REPORT.md:11` (solo doc, no bloqueante).
3. (Opcional, fuera de 2B-2) En futuro refactor de tests, evitar `HomeViewModel` en `HomeContentTest` inyectando VM stub o desactivando `loadHomeData` en test — no urgente.

**Siguiente fase:** pasar a **2B-3 en Modo Plan** (Firebase rules / Auth trigger según roadmap), **no** en Modo Agente directo.

---

## Anexo — `git status --short`

```
(vacío — working tree clean, branch main up to date with origin/main)
```

## Anexo — Archivos en commit `8ab3e3d`

- `PHASE2B2_IMPLEMENTATION_REPORT.md`
- 6 archivos `androidTest` (5 tests + `FakeAuthViewModel.kt`)
- 5 archivos `main` (listados en sección 3)
