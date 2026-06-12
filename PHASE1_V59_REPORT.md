# PHASE 1 V59 — Reporte de implementación

**Proyecto:** GondolApp / Sancarlina (`com.sancarlina.app`)  
**Fecha:** 12 jun 2026  
**Alcance:** Fase 1 hardening según `AUDIT_V59_GONDOLAPP.md`  
**Commits:** Ninguno (por instrucción explícita)

---

## Estado git antes de editar

```
git status --short
(vacío — working tree limpio)
```

---

## Archivos modificados / creados

| Archivo | Acción |
|---------|--------|
| `app/src/main/AndroidManifest.xml` | Modificado |
| `app/build.gradle.kts` | Modificado |
| `.gitignore` | Modificado |
| `app/src/main/res/xml/backup_rules.xml` | Modificado |
| `app/src/main/res/xml/data_extraction_rules.xml` | Modificado |
| `keystore.properties.example` | **Creado** |
| `local.properties` | Modificado **solo en disco local** (gitignored; no aparece en `git diff`) |
| `PHASE1_V59_REPORT.md` | Creado (este archivo) |

**No creado:** `keystore.properties`, `*.jks`, `*.keystore` reales.

---

## Diff resumido por archivo

### `AndroidManifest.xml`
- **+** `uses-permission CAMERA`
- **+** `uses-feature android.hardware.camera` con `required="false"` (corrige lint `PermissionImpliesUnsupportedChromeOsHardware` al declarar cámara; recomendación oficial Play/ChromeOS)
- **−** `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION`
- **~** Maps API Key: de valor literal → `${MAPS_API_KEY}` (placeholder)

### `app/build.gradle.kts`
- Lectura de `MAPS_API_KEY` desde `local.properties` con `GradleException` si falta o está vacía
- Comentario sobre restricción en Google Cloud Console
- `manifestPlaceholders["MAPS_API_KEY"]`
- `versionCode`: 57 → **59**
- `versionName`: `8.1.0` → **`8.1.2`**

### `.gitignore`
- Añadidos: `keystore.properties`, `*.jks`, `*.keystore`, `secrets.properties`

### `backup_rules.xml`
- Exclusión `secure_sancarlina_prefs.xml` y `sancarlina_prefs.xml` (full backup)

### `data_extraction_rules.xml`
- Mismas exclusiones en `cloud-backup` y `device-transfer`

### `keystore.properties.example`
- Plantilla con placeholders (sin secretos reales)

### `local.properties` (local, no commitear)
- Añadida línea `MAPS_API_KEY=...` para permitir build en esta máquina (valor migrado desde el manifest anterior; **no incluido en el repositorio**)

---

## Resultado de comandos

| Comando | Resultado |
|---------|-----------|
| `./gradlew :app:assembleDebug` | **BUILD SUCCESSFUL** (41s) |
| `./gradlew :app:lintDebug` | **BUILD SUCCESSFUL** (57s) — ver nota lint abajo |
| `./gradlew :app:testDebugUnitTest` | **BUILD SUCCESSFUL** (30s) |
| `./gradlew :app:assembleRelease` | **BUILD FAILED** (2m 2s) |

### Detalle `assembleRelease` (esperado, no corregido)

```
Execution failed for task ':app:packageRelease'.
SigningConfig "release" is missing required property "storeFile".
```

**Causa:** No existe `keystore.properties` en la raíz del proyecto. R8 (`minifyReleaseWithR8`) y `lintVitalRelease` **sí completaron** antes del fallo de empaquetado/firma.

**Para subir a Play:** Copiar `keystore.properties.example` → `keystore.properties` y completar con la **misma upload key** usada en versiones anteriores o la gestionada en **Play Console → Setup → App signing**. No se generó keystore nueva en esta fase.

---

## Lint: antes / después

| Momento | Errores | Warnings | Hints |
|---------|---------|----------|-------|
| Antes Fase 1 (auditoría V59) | 0 | 45 | 2 |
| Tras agregar CAMERA sin `uses-feature` | **1** | 45 | 2 |
| **Después Fase 1 (final)** | **0** | **45** | **2** |

Reporte HTML: `app/build/reports/lint-results-debug.html`  
El único error intermedio fue `PermissionImpliesUnsupportedChromeOsHardware`; resuelto con `uses-feature` `required="false"`.

---

## Checklist de confirmación

| Ítem | Estado |
|------|--------|
| `CAMERA` declarada en manifest | **Sí** (`AndroidManifest.xml` línea 8) |
| Ubicación GPS eliminada del manifest | **Sí** (sin `ACCESS_FINE/COARSE_LOCATION`) |
| API Key ya no hardcodeada en manifest | **Sí** (usa `${MAPS_API_KEY}`) |
| `backup_rules` excluyen prefs sensibles | **Sí** |
| `data_extraction_rules` excluyen prefs (cloud + device-transfer) | **Sí** |
| `versionCode` = 59 | **Sí** (`app/build.gradle.kts`) |
| `versionName` = 8.1.2 | **Sí** (`app/build.gradle.kts`) |
| Keystore real creada | **No** |
| `keystore.properties` real creado | **No** |
| `keystore.properties.example` creado | **Sí** |

---

## Notas para el equipo

1. **Nuevos clones / CI:** Agregar en `local.properties`:
   ```
   MAPS_API_KEY=<tu-key-restringida>
   ```
   Sin esa línea, Gradle falla con mensaje explícito (sin imprimir la key).

2. **Google Cloud Console** (seguridad real de Maps):
   - Credentials → restricción **Android apps**
   - Package: `com.sancarlina.app`
   - SHA-1: upload key, release y/o **Play App Signing**
   - API: solo **Maps SDK for Android**

3. **Próximo paso (fuera de Fase 1):** Configurar `keystore.properties` con la upload key histórica → `./gradlew :app:bundleRelease` → subir AAB v59 a Play.

---

*Fin del reporte Phase 1.*
