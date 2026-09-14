# PHASE 2-B — Minor Fixes Report (2B-1 Accesibilidad)

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Alcance:** Solo Fase 2B-1 — strings `cd_*` + `contentDescription` en íconos interactivos P0/P1.

---

## 1. Resumen

### Archivos modificados (24)

| Archivo | Cambio |
|---------|--------|
| `strings.xml` | 8 strings `cd_*` nuevos |
| `MainScaffold.kt` | Menú + bottom navigation |
| `HomeContent.kt` | Búsqueda |
| `SearchContent.kt` | Volver + cerrar |
| `MapContent.kt` | Menú + filtros |
| `CategoryListContent.kt` | Volver + filtros + favorito |
| `CommerceProfileContent.kt` | Volver + favorito |
| `ProductDetailContent.kt` | Volver + favorito |
| `BenefitsContent.kt` | QR |
| `NotificationsContent.kt` | Volver + ajustes |
| `NotificationSettingsContent.kt` | Volver |
| `QrScannerContent.kt` | Volver |
| `EditProfileContent.kt` | Volver |
| `LegalContent.kt` | Volver |
| `SupportContent.kt` | Volver |
| `FavoritesContent.kt` | Volver |
| `PointsHistoryContent.kt` | Volver |
| `ForgotPasswordContent.kt` | Volver |
| `RegisterContent.kt` | Volver (`cd_back`) |
| `EmprendimientoContent.kt` | Volver |
| `ServiciosSelloContent.kt` | Volver |
| `RateCommerceContent.kt` | Volver |
| `NewsDetailContent.kt` | Volver |
| `UpdatesContent.kt` | Volver |

### Cambios aplicados

1. Strings reutilizables: `cd_back`, `cd_search`, `cd_close`, `cd_filters`, `cd_favorite`, `cd_open_qr`, `cd_settings`, `cd_menu`.
2. `contentDescription` con `stringResource(R.string.cd_*)` o `screen.title` (bottom nav) en controles interactivos P0/P1.
3. Elementos decorativos sin cambio (`contentDescription = null`).

### Cambios NO aplicados (según aprobación)

| Ítem | Motivo |
|------|--------|
| 2B-2 tests | Excluido explícitamente |
| 2B-3 Firebase rules / Auth trigger | Excluido |
| 2B-4 mocks / Stitch / lint | Excluido |
| versionCode / versionName / keystore / AAB | Regla estricta |
| Turismo búsqueda (`TurismoContent.kt:76`) | Barra no clickable — decorativo |
| Leading icons en TextFields login/register | Decorativos con label visible |
| Drawer `ic_gondolapp_symbol` (`MainScaffold.kt:95`) | Decorativo |
| Avatar / Offline / imágenes hero | Decorativos |

---

## 2. Tabla archivo:línea (interactivos actualizados)

| Pantalla | Archivo:línea | Elemento | String usado |
|----------|---------------|----------|--------------|
| Main | `MainScaffold.kt:139-143` | Menú top bar | `cd_menu` |
| Main | `MainScaffold.kt:207-211` | Bottom nav icon | `screen.title` |
| Home | `HomeContent.kt:69-74` | Búsqueda (barra clickable) | `cd_search` |
| Búsqueda | `SearchContent.kt:53` | Volver | `cd_back` |
| Búsqueda | `SearchContent.kt:68` | Cerrar query | `cd_close` |
| Mapa | `MapContent.kt:73` | Menú drawer | `cd_menu` |
| Mapa | `MapContent.kt:83` | Filtros | `cd_filters` |
| Categoría | `CategoryListContent.kt:57` | Volver | `cd_back` |
| Categoría | `CategoryListContent.kt:67` | Filtros | `cd_filters` |
| Categoría | `CategoryListContent.kt:197` | Favorito | `cd_favorite` |
| Comercio | `CommerceProfileContent.kt:124-128` | Favorito | `cd_favorite` |
| Comercio | `CommerceProfileContent.kt:204` | Volver overlay | `cd_back` |
| Producto | `ProductDetailContent.kt:112-115` | Favorito | `cd_favorite` |
| Producto | `ProductDetailContent.kt:218` | Volver overlay | `cd_back` |
| Puntos | `BenefitsContent.kt:78` | QR en botón | `cd_open_qr` |
| Notificaciones | `NotificationsContent.kt:47` | Volver | `cd_back` |
| Notificaciones | `NotificationsContent.kt:57` | Ajustes | `cd_settings` |
| QR scanner | `QrScannerContent.kt:102` | Volver | `cd_back` |
| Perfil editar | `EditProfileContent.kt:60` | Volver | `cd_back` |
| Legal | `LegalContent.kt:40` | Volver | `cd_back` |
| Soporte | `SupportContent.kt:42` | Volver | `cd_back` |
| Favoritos | `FavoritesContent.kt:69` | Volver | `cd_back` |
| Historial puntos | `PointsHistoryContent.kt:43` | Volver | `cd_back` |
| Registro | `RegisterContent.kt:62` | Volver | `cd_back` |
| Recuperar clave | `ForgotPasswordContent.kt:42` | Volver | `cd_back` |
| Emprendimiento | `EmprendimientoContent.kt:41` | Volver | `cd_back` |
| Servicios sello | `ServiciosSelloContent.kt:39` | Volver | `cd_back` |
| Calificar | `RateCommerceContent.kt:39` | Volver | `cd_back` |
| Noticia | `NewsDetailContent.kt:154` | Volver overlay | `cd_back` |
| Updates | `UpdatesContent.kt:36` | Volver | `cd_back` |
| Ajustes alertas | `NotificationSettingsContent.kt:34` | Volver | `cd_back` |

### Strings nuevos (`strings.xml:8-16`)

```xml
cd_back, cd_search, cd_close, cd_filters, cd_favorite,
cd_open_qr, cd_settings, cd_menu
```

---

## 3. Validación

| Comando | Resultado | Errores/warnings relevantes |
|---------|-----------|------------------------------|
| `./gradlew :app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `./gradlew :app:lintDebug` | **OK** | 0 errores, 44 warnings preexistentes |
| `./gradlew :app:testDebugUnitTest` | **OK** | Sin fallos |

**Diff:** 24 archivos, +96 / −33 líneas.

---

## 4. Pendientes operativos

| Pendiente | Notas |
|-----------|-------|
| `privacy_policy_url` | Sigue placeholder — reemplazar antes de Play |
| Firma / AAB | Manual desde Android Studio |
| A11y P2/P3 | Chevron, leading icons, más pantallas — backlog 2B futuro |
| Prueba manual TalkBack | Verificar lectura de `cd_*` en dispositivo |

---

## 5. Próximos pasos (no ejecutados)

- 2B-2: smoke tests Compose  
- 2B-3: Firebase rules draft + delete trigger  
- 2B-4: mocks, assets Stitch, lint cleanup  

---

*Sin commits. Solo Fase 2B-1 implementada.*
