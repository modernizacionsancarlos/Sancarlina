# UI-STITCH-5 — Turismo, Map and QR Final UI Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Fase:** UI-STITCH-5 — Turismo / mapa / QR final

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Turismo rework aplicado?** | **Sí** |
| **¿Mapa rework aplicado?** | **Sí** |
| **¿QR revisado/pulido?** | **Sí** |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo** | **Bajo** — solo UI Compose; Google Maps, cámara y ViewModels intactos |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`TurismoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/turismo/TurismoContent.kt) | Refactor con componentes Stitch; chips solo con datos reales; filtro UI por categoría | Pantalla turismo |
| [`MapContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/MapContent.kt) | Top bar flotante, chips, empty hint, bottom sheet Stitch; diálogo filtros con `SancarlinaFilterChip` | Pantalla mapa |
| [`QrScannerContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/QrScannerContent.kt) | `SuccessOverlay` en `SancarlinaCard` + strings | Pulido visual QR |
| [`strings.xml`](app/src/main/res/values/strings.xml) | Textos turismo, mapa y éxito QR | Copy centralizado |
| **Nuevos** `turismo/components/*` (4) | Hero, search, empty, point card | Modularización turismo |
| **Nuevos** `map/components/*` (4) | Top bar, chips, empty hint, bottom sheet | Modularización mapa |

---

## 3. Diseño Stitch aplicado

| Pantalla | Referencia Stitch | Implementación Compose | Estado |
|----------|-------------------|------------------------|--------|
| Turismo | `explora_turismo/` | `TurismoHeroSection`, `TurismoSearchBar`, `TurismoPointCard`, `TurismoEmptyState` | Aplicado |
| Mapa | `mapa_interactivo/` | `MapFloatingTopBar`, `MapFilterChips`, fondo mapa nativo | Aplicado |
| Detalle mapa | `detalle_en_mapa_bottom_sheet/` | `MapTenantBottomSheetCard` + `SancarlinaSheetShape` | Aplicado |
| QR éxito | `esc_ner_qr/` | `SuccessOverlay` con card cream + icono check olive | Pulido |

---

## 4. Componentes creados/reutilizados

| Componente | Archivo | Uso | Estado |
|------------|---------|-----|--------|
| `TurismoHeroSection` | `turismo/components/TurismoHeroSection.kt` | Título y subtítulo | Nuevo |
| `TurismoSearchBar` | `turismo/components/TurismoSearchBar.kt` | Barra búsqueda decorativa | Nuevo |
| `TurismoEmptyState` | `turismo/components/TurismoEmptyState.kt` | “Próximamente” con card e icono | Nuevo |
| `TurismoPointCard` | `turismo/components/TurismoPointCard.kt` | Card punto turístico 240dp | Nuevo |
| `MapFloatingTopBar` | `map/components/MapFloatingTopBar.kt` | Header flotante menú + filtros | Nuevo |
| `MapFilterChips` | `map/components/MapFilterChips.kt` | Chips categoría sobre mapa | Nuevo |
| `MapEmptyHint` | `map/components/MapEmptyHint.kt` | Aviso sin marcadores | Nuevo |
| `MapTenantBottomSheetCard` | `map/components/MapTenantBottomSheetCard.kt` | Bottom sheet comercio | Nuevo |
| `SancarlinaFilterChip` | `ui/components/SancarlinaChips.kt` | Turismo, mapa, diálogo filtros | Reutilizado |
| `SancarlinaCard` | `ui/components/SancarlinaCard.kt` | Empty turismo, hint mapa, QR éxito | Reutilizado |
| `SancarlinaPrimaryButton` | `ui/components/SancarlinaButtons.kt` | Bottom sheet mapa, QR éxito | Reutilizado |
| `SancarlinaSheetShape` | `ui/theme/Shape.kt` | Bottom sheet mapa | Reutilizado |

---

## 5. Datos y estados

| Pantalla/sección | Fuente de datos | Estado loading | Estado vacío/error | ¿Mock visible en release? |
|------------------|-----------------|----------------|---------------------|---------------------------|
| Turismo puntos | `TurismoViewModel.uiState.points` | `CircularProgressIndicator` | `TurismoEmptyState` (“Próximamente”) | **No** — release `points = emptyList()` |
| Turismo chips | Derivados de `points` reales en UI | N/A | Ocultos si `points` vacío | **No** |
| Turismo categorías | `selectedCategory` + filtro local UI | N/A | Sin chips sin datos | **No** |
| Mapa markers | `MapViewModel` / tenants + áreas | N/A (carga en init) | `MapEmptyHint` si `markers` vacío | **No** — release `clearMarkers()` |
| Mapa filtros | `categories` / `locations` del ViewModel | N/A | Diálogo con chips reales | **No** |
| Bottom sheet | `selectedMarker` real al tap | N/A | Solo si marker seleccionado | **No** |
| QR éxito | `uiState.successPoints` del scan | Overlay loading | `SuccessOverlay` con puntos reales | **No** |

---

## 6. Mapa / cámara / permisos

| Verificación | Estado |
|--------------|--------|
| No se cambió lógica de Google Maps | **Confirmado** — `GoogleMap`, `Marker`, `CameraPosition` intactos |
| No se cambió lógica de markers reales | **Confirmado** — `MapViewModel.loadMarkers()` sin cambios |
| No se cambió lógica de permisos de cámara | **Confirmado** — `QrScannerContent` launcher intacto |
| No se cambió ML Kit / QR scanning | **Confirmado** — `CameraPreviewWrapper` / `processImageProxy` intactos |
| No se inventaron markers ni QR results | **Confirmado** |

---

## 7. Navegación

| Verificación | Estado |
|--------------|--------|
| No se cambiaron rutas NavGraph | **Confirmado** |
| Mapa mantiene navegación existente | **Confirmado** — `onNavigateToCommerce(marker.id)` |
| Bottom sheet / perfil comercio mantiene flujo | **Confirmado** |
| QR sigue accesible desde puntos | **Confirmado** — sin cambios de ruta |
| Bottom nav sigue funcionando | **Confirmado** — sin tocar `MainScaffold` |
| Offline route intacta | **Confirmado** |

---

## 8. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `.\gradlew.bat :app:lintDebug` | **OK** | Reporte HTML generado |
| `.\gradlew.bat :app:testDebugUnitTest` | **OK** | Tests unitarios pasaron |

---

## 9. Confirmación de alcance

| Restricción | Cumplida |
|-------------|----------|
| No Firebase | **Sí** |
| No deploy | **Sí** |
| No Firestore Rules | **Sí** |
| No Storage | **Sí** |
| No Functions | **Sí** |
| No Gradle versions | **Sí** |
| No dependencies | **Sí** |
| No versionCode/versionName | **Sí** |
| No AAB/keystore | **Sí** |
| No applicationId/package | **Sí** |
| No WebView | **Sí** |
| No HTML runtime | **Sí** |
| No assets Stitch borrados/movidos | **Sí** |
| No mocks visibles en release | **Sí** |
| No commit | **Sí** |
| No NavGraph modificado | **Sí** |
| No ViewModels modificados | **Sí** — `TurismoViewModel` y `MapViewModel` sin cambios |

---

## 10. Próximo paso recomendado

**UI-STITCH-6 — pulido final:** auth, onboarding, offline, legal, success, notificaciones.

Validar en emulador/dispositivo:
- Turismo en release → empty “Próximamente” sin chips ni cards demo.
- Turismo en DEBUG → 2 puntos mock con chips derivados de categorías reales.
- Mapa en release sin tenants geolocalizados → hint inferior, mapa funcional.
- Mapa con marker → bottom sheet Stitch y navegación a perfil comercio.
- QR → escaneo, overlay éxito con card cream y botón Continuar.

---

*Título de commit sugerido (no ejecutado):* **“Mejorar pantallas de turismo, mapa y mensaje de éxito del QR con el diseño Stitch”**
