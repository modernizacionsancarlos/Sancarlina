# PHASE 2-A — Minor Fixes Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Base:** `PHASE2A_POST_AUDIT_REPORT.md` (hallazgos H-D6, H-D8, H-D9, H-P4, H-B6)

---

## 1. Resumen

### Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `EditProfileViewModel.kt` | Diálogo abierto durante delete; errores visibles; `clearError()` |
| `EditProfileContent.kt` | Banner de error en pantalla principal; bloqueo dismiss/campo durante delete |
| `BrowserUtils.kt` | Fallback `ACTION_VIEW` + Toast si no hay navegador |
| `SplashContent.kt` | `contentDescription` a11y → GondolApp |

### Cambios aplicados

1. **Delete account — loading visible:** no se cierra el diálogo de contraseña al iniciar `isDeletingAccount`; el `CircularProgressIndicator` del botón confirmar permanece visible.
2. **Delete account — errores visibles:** sesión antigua y error genérico cierran el diálogo y muestran banner en pantalla principal con botón "Cerrar"; contraseña incorrecta mantiene diálogo abierto con mensaje.
3. **BrowserUtils:** Custom Tabs → fallback `Intent.ACTION_VIEW` → Toast si ambos fallan.
4. **Splash a11y:** "GondolApp Isologo" / "GondolApp Logotipo".

### Cambios NO aplicados y motivo

| Ítem | Motivo |
|------|--------|
| Orden Firestore → `user.delete()` | Fuera de alcance; riesgo documentado, no imprescindible para UX |
| Cloud Function delete atómico | Explícitamente excluido |
| `privacy_policy_url` real | Pendiente operativo municipal; placeholder intacto |
| Offline wiring | No requerido |
| versionCode / versionName | Regla estricta del encargo |
| keystore / AAB / Firebase rules | Fuera de alcance |

---

## 2. Delete account

### Feedback de loading

- Al confirmar eliminación, `showDeletePasswordDialog` **permanece `true`** (`EditProfileViewModel.kt:135-138`).
- El botón "ELIMINAR DEFINITIVAMENTE" muestra `CircularProgressIndicator` (`EditProfileContent.kt:200-208`).
- Campo contraseña deshabilitado durante delete (`EditProfileContent.kt:232`).
- No se puede cerrar el diálogo con back/outside tap mientras `isDeletingAccount` (`EditProfileContent.kt:193-197`).

### Cómo se muestran errores

| Caso | Comportamiento |
|------|----------------|
| Contraseña vacía / sin email | Mensaje en diálogo (validación previa al delete) |
| Contraseña incorrecta | Diálogo **reabierto** con error en rojo dentro del diálogo |
| Sesión antigua (`FirebaseAuthRecentLoginRequiredException`) | Diálogo cerrado; banner en pantalla principal |
| Error genérico | Diálogo cerrado; banner en pantalla principal |
| Éxito | `onLogout()` navega fuera de la pantalla |

Banner principal: `EditProfileContent.kt:76-99` — visible cuando `error != null` y `!showDeletePasswordDialog`.

### Contraseña incorrecta

Sin cambio de comportamiento deseado: `showDeletePasswordDialog = true` + `error = "Contraseña incorrecta."` (`EditProfileViewModel.kt:163-171`).

### Sesión antigua

Diálogo se cierra, contraseña se limpia, mensaje en banner principal (`EditProfileViewModel.kt:152-160`).

### Riesgos pendientes

| Riesgo | Estado |
|--------|--------|
| Orden **Firestore delete → user.delete()** | **Sin cambio.** Si `user.delete()` falla tras borrar Firestore, puede quedar cuenta Auth sin documento `userProfiles`. Mitigación futura: Cloud Function o invertir orden con compensación. |
| Usuarios OAuth (Google/Facebook) | Sin flujo de re-auth; solo email/password. Comentario en ViewModel línea 121-123. |
| Errores al guardar perfil en pantalla principal | Fuera de alcance (save error ya existía en state). |

---

## 3. BrowserUtils

### Flujo

1. **Custom Tabs** — intento principal con toolbar verde municipal (`BrowserUtils.kt:16-24`).
2. **Fallback `ACTION_VIEW`** — si Custom Tabs lanza excepción (`BrowserUtils.kt:25-28`, `37-49`).
3. **Toast** — si no hay actividad que resuelva el intent (`BrowserUtils.kt:26-31`).

### Logs

`Logger.e` solo escribe en **debug** (`Logger.kt:15-18` + `BuildConfig.DEBUG`).

### Sin crash

Todas las rutas envueltas en `try/catch`; Toast como último recurso.

---

## 4. Branding a11y Splash

**Corregido:**

- `SplashContent.kt:67` → `contentDescription = "GondolApp Isologo"`
- `SplashContent.kt:107` → `contentDescription = "GondolApp Logotipo"`

Imágenes y assets sin cambios.

---

## 5. Validación

| Comando | Resultado | Errores/warnings relevantes |
|---------|-----------|---------------------------|
| `./gradlew :app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `./gradlew :app:lintDebug` | **OK** | 0 errores, 44 warnings (preexistentes) |
| `./gradlew :app:testDebugUnitTest` | **OK** | Sin fallos |

---

## 6. Pendientes operativos

| Pendiente | Notas |
|-----------|-------|
| **privacy_policy_url** | Sigue placeholder `https://REEMPLAZAR-POR-URL-MUNICIPAL/privacidad` — reemplazar antes de Play |
| **Firma / AAB** | `keystore.properties` pendiente — ver `PHASE1_5_RELEASE_SIGNING_REPORT.md` |
| **Pruebas manuales offline** | Modo avión ON/OFF, REINTENTAR sin red |
| **Pruebas manuales delete** | Contraseña correcta/incorrecta, sesión antigua, loading visible durante delete |
| **Prueba icono launcher** | Visual en dispositivo API 26+ |
| **Prueba BrowserUtils** | Tap política de privacidad sin Chrome / sin navegador (Toast) |

---

*Sin commits. No se avanzó a Fase 2-B.*
