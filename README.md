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
