# UI-STITCH-3 — Catalog and Product UI Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Fase:** UI-STITCH-3 — Categorías / comercios / productos

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Categorías rework aplicadas?** | **Sí** |
| **¿Detalle producto rework aplicado?** | **Sí** |
| **¿Perfil comercio rework aplicado?** | **Sí** |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo** | **Bajo** — solo UI; ViewModels y NavGraph intactos |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`CategoryListContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListContent.kt) | Rework Stitch + componentes | Lista categoría/comercios |
| [`ProductDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailContent.kt) | Hero, info, barra CTA sticky | Detalle producto Stitch |
| [`CommerceProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/CommerceProfileContent.kt) | Hero + info real; **eliminados mocks** | Perfil comercio honesto |
| [`strings.xml`](app/src/main/res/values/strings.xml) | Textos catálogo/producto/comercio | Copy sin datos falsos |
| [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | Import `CommerceCard` actualizado | Compilación (alias) |
| **Nuevos** `category/components/*` | 3 archivos | Header, filtros, cards |
| **Nuevos** `product/components/*` | 3 archivos | Hero, info, action bar |
| **Nuevos** `map/components/*` | 2 archivos | Hero e info comercio |

---

## 3. Diseño Stitch aplicado

| Pantalla | Referencia Stitch | Implementación Compose | Estado |
|----------|-------------------|------------------------|--------|
| Lista categoría | `artesan_as/screen.png` | `CategoryHeader`, `CategoryFilterBar`, `CommerceListCard` | Aplicado |
| Filtros avanzados | `filtros_avanzados/screen.png` | `AdvancedFiltersBottomSheet` + `SancarlinaSheetShape` | Aplicado |
| Detalle producto | `detalle_de_producto/screen.png` | `ProductHeroSection`, overlay back/fav | Aplicado |
| Galería producto | `galer_a_de_fotos/screen.png` | Thumbs 24dp en `ProductInfoSection` | Aplicado |
| CTA consultar | Stitch burgundy bottom bar | `ProductActionBar` secondary | Aplicado |
| Perfil comercio | `perfil_de_comercio/screen.png` | `CommerceProfileHero`, `CommerceInfoCard` | Aplicado |

---

## 4. Componentes creados/reutilizados

| Componente | Archivo | Uso | Estado |
|------------|---------|-----|--------|
| `CategoryHeader` | `category/components/CategoryHeader.kt` | Top bar + filtros | Nuevo |
| `CategoryFilterBar` | `category/components/CategoryFilterBar.kt` | `SancarlinaFilterChip` ubicaciones | Nuevo |
| `CommerceListCard` | `category/components/CommerceListCard.kt` | Card comercio 24dp | Nuevo |
| `CommerceCard` | alias en mismo archivo | Compat. Favoritos | Nuevo |
| `ProductHeroSection` | `product/components/ProductHeroSection.kt` | Imagen hero + overlay | Nuevo |
| `ProductInfoSection` | `product/components/ProductInfoSection.kt` | Tags, precio, descripción, galería | Nuevo |
| `ProductActionBar` | `product/components/ProductActionBar.kt` | CTA sticky inferior | Nuevo |
| `CommerceProfileHero` | `map/components/CommerceProfileHero.kt` | Hero comercio | Nuevo |
| `CommerceInfoCard` | `map/components/CommerceInfoCard.kt` | Datos tenant reales | Nuevo |
| `SancarlinaTopBar` | `ui/components/SancarlinaTopBar.kt` | Header categoría | Reutilizado |
| `SancarlinaFilterChip` | `ui/components/SancarlinaChips.kt` | Chips ubicación | Reutilizado |
| `SancarlinaCard` | `ui/components/SancarlinaCard.kt` | Empty/error states | Reutilizado |
| `SancarlinaPrimaryButton` | `ui/components/SancarlinaButtons.kt` | Filtros / not found | Reutilizado |

---

## 5. Datos y estados

| Pantalla/sección | Fuente de datos | Estado loading | Estado vacío/error | ¿Mock visible en release? |
|------------------|-----------------|----------------|---------------------|---------------------------|
| Lista comercios | `CategoryListViewModel` / Firestore | `LinearProgressIndicator` + spinner | `CategoryListEmptyState` + error | **No** |
| Filtros ubicación | `uiState.locations` | N/A | Oculto si lista vacía | **No** |
| Detalle producto | `ProductDetailViewModel` | `CircularProgressIndicator` | `ProductNotFoundState` | **No** |
| Galería producto | `product.galleryImages` | N/A | Sección oculta si vacía | **No** |
| CTA WhatsApp | `product.phone` | N/A | Botón deshabilitado si sin teléfono | **No** |
| Perfil comercio | `CommerceProfileViewModel` / Tenant | Spinner | Error si tenant null | **No** |
| Productos comercio | *(no hay en ViewModel)* | N/A | `commerce_products_empty` | **No** — removidos "Miel Pura" / "$4500" |
| Rating comercio | `tenant.rating` / `reviewsCount` | N/A | Oculto si rating ≤ 0 | **No** — removido "4.9 (120)" fijo |
| Ubicación comercio | `tenant.areaId` | N/A | Chip solo si hay dato | **No** — removido "Eugenio Bustos" fijo |
| Preview producto | `preview` privado `@Preview` | Solo IDE | Solo IDE | **No** en APK |

---

## 6. Navegación

| Verificación | Estado |
|--------------|--------|
| Rutas `NavGraph` sin cambios | **Confirmado** |
| Lista categoría → detalle (`onNavigateToDetail(commerce.id)`) | **Intacto** |
| Home destacado → detalle producto | **Intacto** (sin cambios NavGraph) |
| Perfil comercio → producto (`onNavigateToProduct`) | **Intacto** (callback preservado) |
| Bottom nav | **Sin cambios** |
| Ruta offline | **Sin cambios** |

---

## 7. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **BUILD SUCCESSFUL** | Tras fix alias `CommerceCard` |
| `.\gradlew.bat :app:lintDebug` | **BUILD SUCCESSFUL** | 0 errores |
| `.\gradlew.bat :app:testDebugUnitTest` | **BUILD SUCCESSFUL** | Sin fallos |

---

## 8. Confirmación de alcance

| Restricción | Cumplida |
|-------------|----------|
| No Firebase | Sí |
| No deploy | Sí |
| No Firestore Rules | Sí |
| No Storage | Sí |
| No Functions | Sí |
| No Gradle versions | Sí |
| No dependencies | Sí |
| No versionCode/versionName | Sí |
| No AAB/keystore | Sí |
| No applicationId/package | Sí |
| No WebView | Sí |
| No HTML runtime | Sí |
| No assets Stitch borrados/movidos | Sí |
| No mocks visibles en release | Sí — **limpieza explícita en CommerceProfile** |
| No commit | Sí |
| No NavGraph modificado | Sí |
| No ViewModels modificados | Sí |

---

## 9. Próximo paso recomendado

### UI-STITCH-4 — Perfil / puntos / favoritos

1. [`ProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/ProfileContent.kt) vs `perfil_de_usuario/screen.png`
2. [`BenefitsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/BenefitsContent.kt) vs `panel_de_puntos/screen.png`
3. [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) — alinear header/empty con Stitch (ya usa `CommerceCard`)

### Hotfix

No requerido — build/lint/tests OK.

---

*Validación Gradle completada. Sin commit.*
