# PHASE 2B-4.1 — Mocks Cleanup Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Alcance:** Solo limpieza de mocks peligrosos visibles (2B-4.1)

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Mocks peligrosos eliminados en release?** | **Sí** |
| **¿Empty states implementados?** | **Sí** |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** (0 errores) |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo residual** | **Bajo** — mocks conservados solo en `BuildConfig.DEBUG` para desarrollo local |

---

## 2. Archivos modificados

| Archivo | Cambio |
|---------|--------|
| [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) | Textos empty state / error |
| [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | Lista vacía → `EmptyFavorites` |
| [`PointsHistoryContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/PointsHistoryContent.kt) | Sin movimientos demo + `PointsHistoryEmptyState` |
| [`TurismoViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/TurismoViewModel.kt) | Sin mock en release; mock solo DEBUG |
| [`TurismoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/turismo/TurismoContent.kt) | Loading + `TurismoEmptyState` |
| [`CategoryListViewModel.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListViewModel.kt) | Mock solo DEBUG; vacío/error en release |
| [`CategoryListUiState.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListUiState.kt) | Campo `hasLoadError` |
| [`CategoryListContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListContent.kt) | `CategoryListEmptyState` |
| [`MapViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/MapViewModel.kt) | `loadMockMarkers` solo DEBUG; `clearMarkers` en release |
| [`ProductDetailViewModel.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailViewModel.kt) | `notFound` en release; mock solo DEBUG |
| [`ProductDetailUiState.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailUiState.kt) | Campo `notFound` |
| [`ProductDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailContent.kt) | `ProductNotFoundState` |
| [`HomeViewModel.kt`](app/src/main/java/com/sancarlina/app/viewmodel/HomeViewModel.kt) | Estado inicial vacío + `isLoading`; `seedFirestore` solo DEBUG |

---

## 3. Cambios por pantalla

| Pantalla | Antes | Después (release) |
|----------|-------|-------------------|
| **Favoritos** | 2 comercios hardcodeados siempre | Lista vacía → `EmptyFavorites` |
| **Historial puntos** | 4 movimientos demo | "Aún no hay movimientos" |
| **Turismo** | 2 puntos mock siempre | "Próximamente" |
| **Lista categoría** | Mock si Firestore vacío/error | Lista vacía + mensaje o error de carga |
| **Mapa** | Marcadores demo si sin tenants | Mapa sin marcadores |
| **Detalle producto** | "Miel Sancarlina (Demo)" | "Producto no encontrado" |
| **Home** | Banners/categorías/producto ficticios al iniciar | Loading → datos Firestore o vacío |

---

## 4. Comandos ejecutados

| Comando | Resultado |
|---------|-----------|
| `./gradlew :app:assembleDebug` | **OK** |
| `./gradlew :app:lintDebug` | **OK** (0 errores) |
| `./gradlew :app:testDebugUnitTest` | **OK** |

---

## 5. Confirmación de alcance

| Restricción | Cumplido |
|-------------|----------|
| Solo 2B-4.1 mocks | **Sí** |
| No 2B-4.2 docs | **Sí** |
| No 2B-4.3 lint | **Sí** |
| No 2B-4.4 Stitch | **Sí** |
| No Firebase deploy/rules | **Sí** |
| No Gradle versions | **Sí** |
| No versionCode/versionName | **Sí** |
| No keystore/AAB | **Sí** |
| No commit | **Sí** |

---

## 6. Próximo paso

1. Integrar favoritos reales desde `userProfiles.favoriteTenantIds` cuando el backend esté listo.
2. Conectar historial de puntos a `AuditLogs` o colección dedicada.
3. Cargar turismo desde Firestore cuando exista colección/schema.
4. Continuar con **2B-4.2** (docs) o **privacy_policy_url** municipal antes de Play.

**Título de commit sugerido:**  
`Quitar datos de prueba visibles y mostrar pantallas vacías en la app`
