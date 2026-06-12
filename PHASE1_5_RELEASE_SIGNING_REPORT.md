# PHASE 1.5 — Release Signing Report

**Proyecto:** GondolApp / Sancarlina (`com.sancarlina.app`)  
**Fecha:** 12 jun 2026  
**Alcance:** Verificación de firma release para AAB v59 — **sin modificar código fuente**  
**Versión en Gradle:** `versionCode = 59`, `versionName = "8.1.2"` (`app/build.gradle.kts:38-39`)

---

## 1. Estado general

| Verificación | Resultado |
|--------------|-----------|
| **¿Debug build OK?** | **Sí** — `./gradlew :app:assembleDebug` → `BUILD SUCCESSFUL` (5s) |
| **¿Lint OK?** | **Sí** — `./gradlew :app:lintDebug` → `BUILD SUCCESSFUL` (3s). Reporte: 45 warnings, 2 hints, **0 errores** |
| **¿Tests OK?** | **Sí** — `./gradlew :app:testDebugUnitTest` → `BUILD SUCCESSFUL` (18s) |
| **¿Bundle release generado?** | **No** |
| **Causa si no se generó** | No existe `keystore.properties` en la raíz del proyecto. El bloque `signingConfigs.release` en `app/build.gradle.kts` solo asigna `storeFile`, `storePassword`, `keyAlias` y `keyPassword` **si** ese archivo existe (`:49-55`). Al faltar, la config release queda sin `storeFile` (null). |

### Detalle del fallo `bundleRelease`

```
Execution failed for task ':app:signReleaseBundle'.
> java.lang.NullPointerException (no error message)
  at com.android.build.gradle.internal.tasks.FinalizeBundleTask$BundleToolRunnable.run
```

**Tareas release que sí completaron antes del fallo:** `minifyReleaseWithR8`, `lintVitalRelease`, `packageReleaseBundle`.  
**Falló en:** `signReleaseBundle` (firma del AAB).

**Referencia cruzada (`assembleRelease` en Fase 1):** mensaje más explícito del mismo problema de fondo:

```
SigningConfig "release" is missing required property "storeFile".
```

**Conclusión:** El AAB no se generó porque falta configuración local de firma; no por error de compilación, R8 ni lint vital.

### Estado git al inicio de esta verificación

```
git status --short
(vacío — working tree limpio)
```

---

## 2. Estado de archivos sensibles

| Archivo | Existe | Ignorado por git | Observación |
|---------|--------|------------------|-------------|
| `keystore.properties` | **No** | Sí (`.gitignore:24`) | **Bloqueante** para `bundleRelease` / `assembleRelease` firmados |
| `keystore.properties.example` | **Sí** | **No** (trackeado en repo) | Plantilla con placeholders; copiar y completar localmente |
| `local.properties` | **Sí** | Sí (`.gitignore:21`) | Contiene `sdk.dir` y `MAPS_API_KEY` (no inspeccionado ni mostrado) |
| `*.jks` / `*.keystore` | No detectado en raíz | Sí (`.gitignore:25-26`) | El usuario debe ubicar su upload key histórica fuera del repo |
| `secrets.properties` | No | Sí (`.gitignore:27`) | Patrón preventivo en `.gitignore` |

### Entradas confirmadas en `.gitignore`

- `keystore.properties` ✅
- `*.jks` ✅
- `*.keystore` ✅
- `secrets.properties` ✅

---

## 3. Signing config detectada

Fuente: `app/build.gradle.kts` líneas 45-71.

### Qué lee Gradle

El bloque `signingConfigs { create("release") { ... } }` busca el archivo:

```
<raíz-del-proyecto>/keystore.properties
```

(`project.rootProject.file("keystore.properties")` — línea 48)

**Solo si el archivo existe**, carga estas propiedades (nombres exactos, case-sensitive):

| Propiedad en `keystore.properties` | Asignación en Gradle |
|-----------------------------------|----------------------|
| `storeFile` | `storeFile = file(properties.getProperty("storeFile"))` |
| `storePassword` | `storePassword = properties.getProperty("storePassword")` |
| `keyAlias` | `keyAlias = properties.getProperty("keyAlias")` |
| `keyPassword` | `keyPassword = properties.getProperty("keyPassword")` |

### Qué pasa si falta `storeFile`

1. Si **no existe** `keystore.properties`: el bloque `if (propertiesFile.exists())` no ejecuta → `storeFile` queda **null**.
2. `buildTypes.release` **siempre** referencia `signingConfig = signingConfigs.getByName("release")` (línea 63).
3. Al empaquetar/firmar:
   - `assembleRelease` → error explícito: `missing required property "storeFile"`.
   - `bundleRelease` → falla en `signReleaseBundle` con `NullPointerException` (misma causa: signing config incompleta).

### ¿`bundleRelease` usa la misma `signingConfig` release?

**Sí.** `bundleRelease` es el build type `release`, que tiene `signingConfig = signingConfigs.getByName("release")`. No hay signing config separada para AAB vs APK.

### Salida esperada del AAB (cuando firma esté OK)

Ruta típica tras éxito:

```
app/build/outputs/bundle/release/app-release.aab
```

---

## 4. Acción manual requerida por el usuario

1. **Localizar la upload key histórica** usada en versiones anteriores de Play Console (archivo `.jks` o `.keystore` existente).  
   **No crear una key nueva** salvo que Play Console permita explícitamente resetear la upload key.

2. **Crear** en la raíz del proyecto (junto a `settings.gradle.kts`):

```properties
storeFile=/ruta/real/a/la/upload-key.jks
storePassword=NO_MOSTRAR
keyAlias=NO_MOSTRAR
keyPassword=NO_MOSTRAR
```

- `storeFile`: ruta absoluta o relativa al **módulo app** según cómo Gradle resuelva `file()` — en Windows usar barras o escapar backslashes (ej. `C:/ruta/upload-key.jks`).
- Los cuatro campos son **obligatorios** para firmar.

3. **Verificar en Play Console** (Setup → App signing):
   - Si usás **Play App Signing**, la upload key debe coincidir con la registrada.
   - El certificado de **app signing** lo gestiona Google; vos firmás el AAB con la **upload key**.

4. **No subir al repo:**
   - `keystore.properties`
   - archivos `.jks` / `.keystore`
   - passwords

5. **Referencia:** copiar desde `keystore.properties.example` y reemplazar placeholders.

---

## 5. Comandos para volver a probar

Tras crear `keystore.properties` con la upload key correcta:

```bash
./gradlew :app:bundleRelease
```

Verificación opcional adicional:

```bash
./gradlew :app:assembleRelease
```

Ambos requieren la misma firma release configurada.

---

## 6. Próximo paso recomendado

| Prioridad | Acción |
|-----------|--------|
| **1 (ahora)** | Resolver firma release: crear `keystore.properties` local con la **upload key histórica** → generar `app-release.aab` v59 → subir a track de prueba en Play Console. |
| **2 (después)** | Con AAB v59 en beta cerrada y smoke test en dispositivo, avanzar a **Fase 2** (offline wiring, re-auth delete account, auditoría Firebase rules, URL pública de privacidad). |

**No conviene avanzar a Fase 2 de código** antes de confirmar que el AAB v59 firma y sube correctamente a Play: la firma es prerequisito bloqueante para validar release real con R8 + Play pre-launch.

---

## Resumen ejecutivo

- **Fase 1 técnica:** OK (debug, lint, tests).
- **Firma release:** **pendiente acción manual del usuario** (`keystore.properties` + upload key existente).
- **Código:** no requiere cambios para desbloquear firma; solo configuración local.

---

*Fin del reporte Phase 1.5 — generado sin modificar código fuente ni crear keystores.*
