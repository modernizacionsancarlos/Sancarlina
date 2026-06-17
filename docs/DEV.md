# Desarrollo GondolApp

Guía técnica para desarrollar, probar e instalar **GondolApp** (`com.sancarlina.app`) desde Cursor o terminal, sin depender de Android Studio para el día a día.

**Proyecto Firebase:** `sancarlina-99748`

---

## Requisitos

| Herramienta | Uso |
|-------------|-----|
| **JDK** | Android/Gradle: JDK **17** LTS recomendado (Temurin). El proyecto compila con `JavaVersion.VERSION_11` en bytecode (`app/build.gradle.kts`). |
| **JDK 21+** | Obligatorio solo para **Firebase Emulator** al ejecutar `npm run test:rules` (firebase-tools ya no soporta Java &lt; 21 para emuladores). |
| **Node.js + npm** | Tests automáticos de Firestore Rules (`package.json` en la raíz). |
| **Firebase CLI** | Comparar reglas, emulador, deploy controlado de rules (con aprobación). |
| **Android SDK + platform-tools** | `adb`, emulador, compilación APK/AAB. Variable `ANDROID_HOME` apuntando al SDK. |
| **local.properties`** | `sdk.dir` y `MAPS_API_KEY` (no commitear). |

En Windows, si fallan comandos Node/Firebase por certificados SSL en Cursor:

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
```

---

## Comandos Android

Desde la raíz del repositorio:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:testDebugUnitTest
```

- **assembleDebug** — genera `app\build\outputs\apk\debug\app-debug.apk`
- **lintDebug** — análisis estático (0 errores = objetivo)
- **testDebugUnitTest** — tests unitarios JVM

Tests instrumentados Compose (smoke, fase 2B-2) requieren dispositivo/emulador:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

---

## Instalar y abrir app sin Android Studio

### Celular físico (USB + depuración USB)

1. Conectar el teléfono y autorizar depuración USB.
2. Verificar que `adb` ve el dispositivo:

```powershell
adb devices
```

3. Compilar e instalar:

```powershell
.\gradlew.bat :app:installDebug
```

4. Abrir la app (launcher):

```powershell
adb shell monkey -p com.sancarlina.app -c android.intent.category.LAUNCHER 1
```

### APK debug manual (sin `installDebug`)

```powershell
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell monkey -p com.sancarlina.app -c android.intent.category.LAUNCHER 1
```

### Emulador Android (sin abrir Android Studio cada vez)

Listar AVDs disponibles:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -list-avds
```

Iniciar emulador (reemplazar `NOMBRE_DEL_EMULADOR`):

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd NOMBRE_DEL_EMULADOR
```

En otra terminal, instalar y lanzar:

```powershell
.\gradlew.bat :app:installDebug
adb shell monkey -p com.sancarlina.app -c android.intent.category.LAUNCHER 1
```

**Si no hay ningún AVD:** abrir Android Studio **una vez** → Device Manager → Create Device. Después el emulador se puede arrancar solo desde terminal.

**Android Studio** sigue siendo útil para: crear el primer emulador, depuración visual y **generar el AAB firmado** para Play Console.

---

## Logs y crashes

Filtrar logcat por paquete, runtime y Firebase:

```powershell
adb logcat | findstr /i "sancarlina GondolApp AndroidRuntime Firebase"
```

Para limpiar buffer antes de reproducir un fallo:

```powershell
adb logcat -c
```

---

## Firestore Rules (tests locales)

Infraestructura en la raíz del repo: `package.json`, `rules-tests/`, `scripts/compare-firestore-rules.js`, `firebase.json` (emulador puerto 8080).

### Instalación (primera vez)

```powershell
npm install
```

### Comparar reglas locales vs activas en Firebase

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
npm run compare:rules
```

- Compara [`firestore.rules`](firestore.rules) local con el release **exacto** `projects/sancarlina-99748/releases/cloud.firestore` (base default).
- **No** debe confundirse con releases de DB nombrada, p. ej. `cloud.firestore/estudiantina` (otra app/reglas).
- Salida esperada: `COMPARE_STATUS=match`
- Exit codes: `0` = coinciden, `1` = difieren, `2` = no verificable (auth/red)

### Ejecutar tests T1–T15 (emulador local)

El emulador Firestore **no** usa datos de producción. Project ID de tests: ficticio `demo-sancarlina-rules`.

**JDK 21+** solo para esta tarea (Gradle/Android puede seguir con JDK 17):

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
npm run test:rules
```

Equivalente interno: `firebase emulators:exec --only firestore` + Jest en `rules-tests/`.

Documentación de fases previas: `PHASE2B3_5_RULES_COMPARE_AND_TEST_REPORT.md`, `PHASE2B4_1_MOCKS_CLEANUP_REPORT.md`.

---

## Deploy Firebase

**Advertencia:** no ejecutar deploy completo del proyecto.

| Comando | ¿Permitido? |
|---------|-------------|
| `firebase deploy` | **No** (despliega todo) |
| `firebase deploy --only storage` | **No** sin fase aprobada |
| `firebase deploy --only functions` | **No** sin fase aprobada |
| `firebase deploy --only firestore:rules --project sancarlina-99748` | **Solo con aprobación explícita** |

Ejemplo controlado (solo reglas Firestore):

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
firebase deploy --only firestore:rules --project sancarlina-99748
```

Siempre ejecutar antes `npm run compare:rules` y validar que local y remoto coinciden.

---

## Play / Release

Pendientes antes de publicar en Google Play:

1. **Política de privacidad** — reemplazar placeholder en `app/src/main/res/values/strings.xml` (`privacy_policy_url`). URL real la provee el municipio; no inventar.
2. **AAB firmado** — generar con keystore de release (Android Studio o Gradle `bundleRelease` con `keystore.properties` local).
3. **Play Console** — Data Safety, content rating, store listing alineados con la URL de privacidad.
4. **Prueba en dispositivo real** — flujos login, mapa, QR, puntos, offline.
5. **Firestore Rules** — `npm run compare:rules` + `npm run test:rules` en CI o local antes de cada release de rules.

---

## Referencias en el repo

| Documento | Contenido |
|-----------|-----------|
| [`FIRESTORE-SCHEMA.md`](FIRESTORE-SCHEMA.md) | Colecciones y campos compartidos web/app |
| [`firestore.rules`](firestore.rules) | Reglas Firestore GondolApp |
| Reportes `PHASE*.md` | Historial de fases (auditorías, deploys, tests) |
