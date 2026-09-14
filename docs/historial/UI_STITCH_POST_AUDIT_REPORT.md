# UI-STITCH — General Post Audit Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Tipo:** Post-auditoría estática (código + Gradle). **Sin modificación de código.** Sin emulador.

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿UI-STITCH 1–6 aceptable?** | **Sí** (con reservas documentadas abajo) |
| **¿Se puede pasar a prueba APK en celular?** | **Sí** |
| **Bloqueadores** | 1 hallazgo de contenido demo en release: [`NewsDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/NewsDetailContent.kt) (navegable desde Home). No impide compilar ni instalar APK debug; sí afecta criterio “sin datos demo en release” en esa ruta. |
| **Riesgos menores** | Pantallas secundarias sin fase Stitch dedicada (`SearchContent`, `RateCommerceContent`, `UpdatesContent`); varios CTAs sin acción; muchas listas vacías hasta datos Firebase; `privacy_policy_url` placeholder; tipografía con fallback `SansSerif` (sin TTF Be Vietnam Pro/Manrope en repo). |
| **Riesgo general** | **Bajo–medio** — UI Stitch aplicada en Compose de forma real; riesgo principal es contenido estático/demo en rutas puntuales y UX incompleta (botones pendientes), no arquitectura ni build. |

---

## 2. Estado de comandos

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `.\gradlew.bat :app:lintDebug` | **OK** | Sin errores bloqueantes en esta corrida |
| `.\gradlew.bat :app:testDebugUnitTest` | **OK** | Tests unitarios pasaron |
| Emulador | **No ejecutado** | Según alcance del usuario |
| AAB / deploy | **No ejecutado** | Según alcance |

---

## 3. Cobertura visual por pantalla

| Pantalla | Archivo principal | Fase UI-STITCH | Estado visual | Riesgo | Recomendación |
|----------|-------------------|----------------|---------------|--------|---------------|
| Splash | [`SplashContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/splash/SplashContent.kt) | 6 | Cream, spinner olive, strings | Bajo | Probar animación en dispositivo |
| Onboarding | [`OnboardingContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/OnboardingContent.kt) | 6 | Pager + CTA `SancarlinaPrimaryButton` | Bajo | Contenido estático UX (3 slides); no es mock de backend |
| Login | [`LoginContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/LoginContent.kt) | 6 | `AuthLogoHeader`, `SancarlinaElevatedCard`, `SancarlinaTextField` | Bajo | Google Sign-In sigue sin implementar (`onClick` vacío) |
| Registro | [`RegisterContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/RegisterContent.kt) | 6 | Igual línea login | Bajo | — |
| Recuperar contraseña | [`ForgotPasswordContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/ForgotPasswordContent.kt) | 6 | Top bar + card Stitch | Bajo | Lógica `FirebaseAuth.sendPasswordResetEmail` intacta |
| Home | [`HomeContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/HomeContent.kt) + `home/components/*` | 2 | Componentes Stitch, empty sections reales | Medio | Vacío si Firestore sin banners/categorías |
| Categorías | [`CategoryListContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListContent.kt) | 3 | Header, filtros, cards 24dp | Bajo | Empty/error states con strings |
| Detalle producto | [`ProductDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailContent.kt) | 3 | Hero, `SancarlinaCard`, CTA | Bajo | Mock producto solo `BuildConfig.DEBUG` en VM |
| Perfil comercio | [`CommerceProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/CommerceProfileContent.kt) | 3 | Cards Stitch, sin placeholder URL | Bajo | Datos desde repositorio |
| Perfil usuario | [`ProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/ProfileContent.kt) | 4 | `ProfileHeroCard`, action cards | Bajo | — |
| Editar perfil | [`EditProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileContent.kt) | 4 | `SancarlinaTextField`, avatar iniciales | Bajo | Delete account + reauth intactos |
| Puntos | [`BenefitsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/BenefitsContent.kt) | 4 | Balance card, QR CTA | Bajo | Canje beneficio pendiente (`onClick` vacío) |
| Historial | [`PointsHistoryContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/PointsHistoryContent.kt) | 4 | Top bar + empty Stitch | Bajo | `movements = emptyList()` — empty honesto |
| Favoritos | [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | 4 | Top bar + empty | Bajo | `emptyList()` — sin integración backend aún |
| QR | [`QrScannerContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/QrScannerContent.kt) | 4 + 5 | Marco olive, card permiso/éxito | Bajo | Cámara/ML Kit sin cambios de lógica |
| Turismo | [`TurismoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/turismo/TurismoContent.kt) | 5 | Hero, empty, cards; chips solo con datos | Bajo | Release: empty “Próximamente” |
| Mapa | [`MapContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/MapContent.kt) | 5 | Top bar flotante, chips, bottom sheet | Bajo | `GoogleMap` + markers VM intactos |
| Offline | [`OfflineContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/common/OfflineContent.kt) | 6 | `StitchStatusScreen` | Bajo | Observer en `MainScaffold.kt` intacto |
| Legal | [`LegalContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/legal/LegalContent.kt) | 6 | Top bar + card; `BrowserUtils` | Medio | Texto legal largo hardcodeado; URL privacidad placeholder |
| Soporte | [`SupportContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/support/SupportContent.kt) | 6 | Cards canal + menú institucional | Medio | Cards FAQ/chat/email sin `onClick` funcional |
| Notificaciones | [`NotificationsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/notifications/NotificationsContent.kt) | 6 | Empty state real | Bajo | Lista vacía en runtime (mocks eliminados en fase 6) |
| Ajustes alertas | [`NotificationSettingsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/notifications/NotificationSettingsContent.kt) | 6 | `FormToggleRow` | Bajo | Estado local UI; sin backend FCM |
| Emprendimiento | [`EmprendimientoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/emprendimiento/EmprendimientoContent.kt) | 6 | Form Stitch; enviar → `onBack` | Bajo | Sin envío backend (comportamiento previo) |
| Sello calidad | [`ServiciosSelloContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/servicios/ServiciosSelloContent.kt) | 6 | Hero + cards | Bajo | Contenido institucional estático (no mock de usuario) |

### Pantallas fuera del listado obligatorio pero relevantes

| Pantalla | Archivo | Estado | Riesgo |
|----------|---------|--------|--------|
| Detalle noticia | [`NewsDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/NewsDetailContent.kt) | **No rework Stitch completo**; texto e imagen **hardcodeados** | **Alto** en release — accesible desde Home → “Ver todo” / novedades |
| Búsqueda | [`SearchContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/SearchContent.kt) | Theme parcial; no componentes Stitch-2 dedicados | Medio |
| Calificar comercio | [`RateCommerceContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/RateCommerceContent.kt) | M3 colores; sin fase Stitch | Bajo |
| Historial versiones | [`UpdatesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/updates/UpdatesContent.kt) | No referenciado en `NavGraph.kt` | Bajo (código muerto o futuro) |

### Reportes de fase verificados

| Reporte | Estado |
|---------|--------|
| [`UI_STITCH_1_THEME_REPORT.md`](UI_STITCH_1_THEME_REPORT.md) | Presente |
| [`UI_STITCH_2_HOME_REPORT.md`](UI_STITCH_2_HOME_REPORT.md) | Presente |
| [`UI_STITCH_3_CATALOG_PRODUCT_REPORT.md`](UI_STITCH_3_CATALOG_PRODUCT_REPORT.md) | Presente |
| [`UI_STITCH_4_PROFILE_POINTS_FAVORITES_REPORT.md`](UI_STITCH_4_PROFILE_POINTS_FAVORITES_REPORT.md) | Presente |
| [`UI_STITCH_5_TURISMO_MAP_QR_REPORT.md`](UI_STITCH_5_TURISMO_MAP_QR_REPORT.md) | Presente |
| [`UI_STITCH_6_FINAL_POLISH_REPORT.md`](UI_STITCH_6_FINAL_POLISH_REPORT.md) | Presente |

---

## 4. Mocks / datos demo

| Archivo | Mock o dato demo detectado | Protegido por DEBUG/Preview | Visible en release | Estado |
|---------|---------------------------|----------------------------|-------------------|--------|
| [`TurismoViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/TurismoViewModel.kt) | `loadDebugMockPoints()` | **Sí** (`BuildConfig.DEBUG`) | **No** | OK |
| [`MapViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/MapViewModel.kt) | `loadMockMarkers()` “(Demo)” | **Sí** | **No** | OK |
| [`CategoryListViewModel.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListViewModel.kt) | `loadMockData()` | **Sí** | **No** | OK |
| [`ProductDetailViewModel.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailViewModel.kt) | `loadMockProduct()` | **Sí** | **No** | OK |
| [`HomeViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/HomeViewModel.kt) | `seedFirestore()` | **Sí** (`if (!BuildConfig.DEBUG) return`) | **No** | OK |
| [`NotificationsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/notifications/NotificationsContent.kt) | Lista vacía | N/A | **No** | OK (fase 6) |
| [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | `emptyList()` | N/A | **No** | OK |
| [`PointsHistoryContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/PointsHistoryContent.kt) | `emptyList()` | N/A | **No** | OK |
| [`NewsDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/NewsDetailContent.kt) | Artículo fijo “Feria de Productores…” + URL imagen | **No** | **Sí** | **Pendiente** — fuera de limpieza 2B-4.1 en esta ruta |
| [`OnboardingContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/OnboardingContent.kt) | 3 slides con URLs imagen | N/A (UX onboarding) | **Sí** | Aceptable como contenido de onboarding |
| [`ServiciosSelloContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/servicios/ServiciosSelloContent.kt) | Hero URL + rubros estáticos | N/A (institucional) | **Sí** | Aceptable — copy institucional |
| `@Preview` en varios componentes | Datos fake en previews | **Sí** (solo Preview) | **No** | OK |

---

## 5. Lógica sensible

| Verificación | Estado | Evidencia |
|--------------|--------|-----------|
| Firebase Auth intacto | **Confirmado** | `AuthViewModel` usado por login/registro; `ForgotPasswordContent` conserva `FirebaseAuth.sendPasswordResetEmail` |
| Delete account intacto | **Confirmado** | [`EditProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileContent.kt) — diálogos + `viewModel.deleteAccount` |
| Firestore Rules no tocadas | **Confirmado** | Auditoría solo UI; sin cambios en rules en este trabajo |
| Google Maps intacto | **Confirmado** | [`MapContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/MapContent.kt) — `GoogleMap`, `Marker`, `CameraPosition` |
| QR/cámara intacto | **Confirmado** | [`QrScannerContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/QrScannerContent.kt) — permisos, `CameraPreviewWrapper`, ML Kit |
| Offline observer intacto | **Confirmado** | [`MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) L48–72 + [`ConnectivityObserver.kt`](app/src/main/java/com/sancarlina/app/utils/ConnectivityObserver.kt) |
| NavGraph intacto | **Confirmado** | Rutas en [`Screen.kt`](app/src/main/java/com/sancarlina/app/navigation/Screen.kt) coherentes con reportes; sin evidencia de cambios de rutas en fases Stitch |

---

## 6. Stitch runtime

| Verificación | Estado | Evidencia |
|--------------|--------|-----------|
| No WebView | **Confirmado** | `rg WebView|android.webkit` en `app/` → **0 coincidencias** |
| No HTML runtime | **Confirmado** | Sin `loadUrl` HTML ni assets HTML en Kotlin |
| Assets Stitch no usados en runtime | **Confirmado** | `rg assets/stitch|android_asset` en `app/` → **0 coincidencias** en código; solo comentario en `Color.kt` |
| Diseño trasladado a Compose real | **Confirmado** | Theme [`ui/theme/*`](app/src/main/java/com/sancarlina/app/ui/theme/), componentes `Sancarlina*`, features con `SancarlinaBackground` / cards 24dp en pantallas auditadas |

---

## 7. Cambios fuera de alcance

Confirmado por alcance de las fases UI-STITCH y corrida actual (auditoría sin edición):

| Restricción | Cumplida |
|-------------|----------|
| Firebase deploy | **Sí** — no ejecutado |
| Storage / Functions | **Sí** — no tocados |
| Gradle versions | **Sí** — `assembleDebug` sin cambios de versión en esta auditoría |
| Dependencies | **Sí** — no actualizadas en auditoría |
| versionCode/versionName | **Sí** — [`app/build.gradle.kts`](app/build.gradle.kts) L38–39: `59` / `8.1.2` (sin modificación en esta tarea) |
| AAB/keystore | **Sí** — no generado |
| applicationId/package | **Sí** — `com.sancarlina.app` |
| Assets Stitch borrados/movidos | **Sí** — no evidencia de borrado |
| Commit | **Sí** — auditoría sin commit |

---

## 8. Riesgos visuales probables (checklist)

| Riesgo | Hallazgo | Archivo(s) |
|--------|----------|------------|
| Listas vacías | Home, categorías, turismo, mapa, favoritos, historial, notificaciones pueden mostrar empty | Varios ViewModels + Content |
| Botones sin acción | Google login, canje beneficio, favorito en card, filtros categoría vacíos, soporte cards | `LoginContent.kt`, `BenefitsContent.kt`, `CommerceListCard.kt`, `SupportContent.kt`, `CategoryListContent.kt` |
| Textos hardcodeados | Legal body, NewsDetail, PointsHistory “Actividad reciente”, RateCommerce título | Ver sección 3 |
| Previews con fake | Solo `@Preview` (5 archivos con anotación) | No entran en runtime |
| Pantallas sin Stitch pleno | Search, NewsDetail, RateCommerce, Updates | Ver sección 3 |
| Privacy URL | Placeholder municipal | [`strings.xml`](app/src/main/res/values/strings.xml) L13 |

---

## 9. Próximo paso recomendado

### Si se acepta pasar a APK (recomendado)

1. Generar APK debug: `.\gradlew.bat :app:assembleDebug`  
   Salida: `app/build/outputs/apk/debug/app-debug.apk`
2. Instalar en celular físico (`adb install -r` o copia manual).
3. **Prueba visual rápida (15–20 min):** splash → onboarding (si aplica) → login → home → mapa → puntos/QR → perfil → offline (modo avión) → legal/soporte.
4. Resolver `privacy_policy_url` antes de Play Console.
5. Hotfix opcional previo a release store: **`NewsDetailContent`** enlazado a banner real o empty state (único mock de contenido en release detectado).
6. Preparar AAB firmado cuando URL legal y pruebas físicas estén OK.

### Si se prioriza hotfix antes de APK

| Prioridad | Pantalla | Acción mínima sugerida |
|-----------|----------|------------------------|
| Alta | NewsDetail | Quitar copy demo o cargar desde `BannerItem` / Firestore |
| Media | Search | Alinear top bar/cards a Stitch (cosmético) |
| Baja | Support cards | Wire a intents (tel/mail) cuando existan datos reales |

---

*Auditoría realizada por revisión de código fuente, grep estático y Gradle. No se usó emulador ni instrumentación runtime.*
