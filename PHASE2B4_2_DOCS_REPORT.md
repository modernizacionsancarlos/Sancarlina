# PHASE 2B-4.2 — Technical Docs Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Alcance:** Solo documentación técnica

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Docs creadas?** | **Sí** |
| **¿README creado/actualizado?** | **Sí** (creado — no existía) |
| **¿Se tocó código Android?** | **No** |
| **Riesgo** | **Ninguno** — solo archivos Markdown |

---

## 2. Archivos creados/modificados

| Archivo | Acción | Motivo |
|---------|--------|--------|
| [`README.md`](README.md) | Creado | Punto de entrada del repo |
| [`docs/DEV.md`](docs/DEV.md) | Creado | Guía desarrollo, ADB, rules, Play |
| [`PHASE2B4_2_DOCS_REPORT.md`](PHASE2B4_2_DOCS_REPORT.md) | Creado | Este reporte |

**No creado:** `docs/TESTING.md` — el contenido de pruebas Android y Firestore quedó centralizado en `docs/DEV.md` para evitar duplicación.

---

## 3. Contenido documentado

| Tema | Archivo / sección | Estado |
|------|-------------------|--------|
| Requisitos JDK/Android/Node/Firebase | `docs/DEV.md` § Requisitos | Documentado |
| Comandos Gradle Android | `docs/DEV.md` § Comandos Android | Documentado |
| Instalar app con ADB (físico) | `docs/DEV.md` § Celular físico | Documentado |
| APK debug manual | `docs/DEV.md` § APK debug manual | Documentado |
| Emulador sin Android Studio | `docs/DEV.md` § Emulador Android | Documentado |
| Logs / crashes | `docs/DEV.md` § Logs y crashes | Documentado |
| `npm run compare:rules` | `docs/DEV.md` § Firestore Rules | Documentado |
| Release `cloud.firestore` vs `estudiantina` | `docs/DEV.md` § Comparar reglas | Documentado |
| `npm run test:rules` + JDK 21+ | `docs/DEV.md` § Ejecutar tests | Documentado |
| Advertencia no `firebase deploy` completo | `docs/DEV.md` § Deploy Firebase | Documentado |
| Pendientes Play (privacy, AAB, Data Safety) | `docs/DEV.md` § Play / Release | Documentado |
| README mínimo + enlaces | `README.md` | Documentado |

---

## 4. Comandos documentados

| Comando | Propósito | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | APK debug | Raíz del repo |
| `.\gradlew.bat :app:lintDebug` | Lint estático | |
| `.\gradlew.bat :app:testDebugUnitTest` | Unit tests JVM | |
| `adb devices` | Ver dispositivo | USB debugging |
| `.\gradlew.bat :app:installDebug` | Instalar en device/emulador | |
| `adb shell monkey -p com.sancarlina.app ...` | Abrir launcher | |
| `adb install -r app\build\outputs\apk\debug\app-debug.apk` | Instalar APK manual | |
| `emulator.exe -list-avds` / `-avd` | Emulador desde terminal | Ruta `%LOCALAPPDATA%\Android\Sdk` |
| `adb logcat \| findstr ...` | Logs filtrados | Windows |
| `npm run compare:rules` | Paridad rules local/remoto | Release default only |
| `npm run test:rules` | Tests T1–T15 emulador | JDK 21+ |
| `firebase deploy --only firestore:rules ...` | Deploy rules controlado | Solo con aprobación |

---

## 5. Confirmación de alcance

| Restricción | Cumplido |
|-------------|----------|
| No Kotlin | **Sí** |
| No Firebase rules | **Sí** |
| No Firebase deploy | **Sí** (no ejecutado) |
| No Storage | **Sí** |
| No Functions | **Sí** |
| No Gradle versions | **Sí** |
| No versionCode/versionName | **Sí** |
| No AAB/keystore | **Sí** |
| No assets Stitch | **Sí** |
| No commit | **Sí** |

**Verificación:** `git status --short` tras crear docs — solo archivos nuevos de documentación.

---

## 6. Próximo paso recomendado

1. **2B-4.3** — lint quick wins (`DefaultLocale`, `RedundantLabel`, `colors.xml` plantilla).
2. Microfix opcional — placeholder avatar (`EditProfileContent.kt:113`) o mensaje en mapa vacío.
3. **Antes de Play** — `privacy_policy_url` real en `strings.xml:5`.
4. Commit sugerido cuando el usuario lo pida: documentación dev + README.

---

*Fase 2B-4.2 cerrada. Sin builds Android ejecutados (no requeridos para documentación).*
