# GondolApp (Sancarlina)

App Android municipal **GondolApp** — catálogo de comercios, puntos, mapa y servicios ciudadanos.

| | |
|---|---|
| **Package / applicationId** | `com.sancarlina.app` |
| **Firebase project** | `sancarlina-99748` |
| **Stack** | Kotlin, Jetpack Compose, Firebase (Auth, Firestore) |

## Documentación de desarrollo

Guía completa (build, ADB, emulador, logs, Firestore Rules, Play):

→ **[docs/DEV.md](docs/DEV.md)**

## Comandos rápidos

### Android

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:testDebugUnitTest
```

Instalar en dispositivo conectado:

```powershell
.\gradlew.bat :app:installDebug
adb shell monkey -p com.sancarlina.app -c android.intent.category.LAUNCHER 1
```

### Firestore Rules (local, sin producción)

```powershell
npm install
npm run compare:rules
npm run test:rules
```

Para `test:rules`, el emulador Firebase requiere **JDK 21+** (ver [docs/DEV.md](docs/DEV.md)).

## Importante

- **No** ejecutar `firebase deploy` completo sin aprobación.
- Deploy de reglas Firestore solo de forma controlada: `firebase deploy --only firestore:rules --project sancarlina-99748`
- Antes de Play: actualizar `privacy_policy_url` en `strings.xml` con la URL municipal oficial.

## Esquema de datos

Ver [FIRESTORE-SCHEMA.md](FIRESTORE-SCHEMA.md).

## Documentación

| Documento | Contenido |
|---|---|
| [docs/DEV.md](docs/DEV.md) | Build, ADB, emulador, logs, Firestore Rules, Play |
| [docs/DESIGN_SYSTEM.md](docs/DESIGN_SYSTEM.md) | Tokens de color, tipografía y componentes |
| [docs/SCREEN_MAPPING.md](docs/SCREEN_MAPPING.md) | Mapa de pantallas y rutas de navegación |
| [docs/FIREBASE_COST_GUARDRAILS.md](docs/FIREBASE_COST_GUARDRAILS.md) | Límites de lectura y control de costos |
| [docs/RELEASE_CHECKLIST_8.9.1.md](docs/RELEASE_CHECKLIST_8.9.1.md) | Checklist de publicación de la versión actual |
| [docs/historial/](docs/historial/) | Informes de fases ya cerradas (referencia histórica) |

## Integración continua

[`.github/workflows/android.yml`](.github/workflows/android.yml) ejecuta en cada push y pull request:
lint, tests unitarios y APK debug. Los tests instrumentados y los tests de reglas
Firestore corren al integrar a `main`.

## Telemetría

Crashlytics, Performance Monitoring y Remote Config quedan desactivados en los builds
debug (`Telemetry.install()`), de modo que el ruido de desarrollo no contamina las
métricas de producción. Los parámetros ajustables sin publicar una versión nueva están
en [`AppRemoteConfig`](app/src/main/java/com/sancarlina/app/data/remote/AppRemoteConfig.kt).
