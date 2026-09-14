# UI-STITCH-2 — Home and Main Navigation Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Fase:** UI-STITCH-2 — Home + navegación principal visual

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Home rework aplicado?** | **Sí** |
| **¿Navegación principal ajustada?** | **Sí** — top bar 72dp + logo 32dp (bottom nav sin cambios de rutas) |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo** | **Bajo** — solo UI; `HomeViewModel` y `NavGraph` intactos |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`HomeContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/HomeContent.kt) | Rework visual completo; `HomeContentBody` separado; preview | Layout Stitch + estados loading/vacío |
| [`MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) | Top bar 72dp, logo 32dp | Alinear header con Stitch `h-24` / `h-8` |
| [`strings.xml`](app/src/main/res/values/strings.xml) | Textos home (bienvenida, vacíos, secciones) | Copy honesto sin mocks |
| **Nuevos** `home/components/*.kt` | 7 componentes | Modularizar Home Stitch |

---

## 3. Diseño Stitch aplicado

| Elemento Stitch | Archivo/referencia | Implementación Compose | Estado |
|-----------------|-------------------|------------------------|--------|
| Fondo cream | `DESIGN.md` | `SancarlinaBackground` | Aplicado |
| Bienvenida | Solicitud UI-STITCH-2 | `HomeWelcomeHeader` | Aplicado |
| Search pill | `home/code.html` L139-147 | `HomeSearchBar` (56dp, `CircleShape`) | Aplicado |
| Ofertas carousel | `home/screen.png`, HTML L148-183 | `HomeBannerCarousel` + `HomeHeroCard` | Aplicado |
| Badge olive/burgundy/tertiary | HTML cards | `HomeHeroCard` badge por índice | Aplicado |
| Grid categorías 2×2 | HTML L184-221 | `HomeCategoryGrid` | Aplicado |
| Cards 24dp bento | DESIGN.md shapes | `SancarlinaCardShape` en tiles | Aplicado |
| Sección explorar | HTML "Explorar Sancarlina" | `home_explore_section` string | Aplicado |
| Top app bar | HTML L122-137 | `MainScaffold` altura/logo | Ajuste menor |
| Bottom nav pill | UI-STITCH-1 | Sin cambios en esta fase | Mantenido |

---

## 4. Componentes creados/reutilizados

| Componente | Archivo | Uso | Estado |
|------------|---------|-----|--------|
| `HomeWelcomeHeader` | `components/HomeWelcomeHeader.kt` | Saludo + subtítulo | Nuevo |
| `HomeSearchBar` | `components/HomeWelcomeHeader.kt` | CTA búsqueda | Nuevo |
| `HomeSectionHeader` | `components/HomeSectionHeader.kt` | Títulos + "Ver todo" | Nuevo |
| `HomeHeroCard` | `components/HomeHeroCard.kt` | Banner individual | Nuevo |
| `HomeBannerCarousel` | `components/HomeBannerCarousel.kt` | `LazyRow` banners | Nuevo |
| `HomeCategoryGrid` | `components/HomeCategoryGrid.kt` | Grid 2 columnas | Nuevo |
| `HomeFeaturedProductCard` | `components/HomeFeaturedProductCard.kt` | Producto destacado Firestore | Nuevo |
| `HomeEmptySection` | `components/HomeEmptySection.kt` | Vacíos por sección | Nuevo |
| `SancarlinaElevatedCard` | `ui/components/SancarlinaCard.kt` | Card destacado | Reutilizado |

---

## 5. Datos y estados

| Sección Home | Fuente de datos | Estado loading | Estado vacío | ¿Mock visible en release? |
|--------------|-----------------|----------------|--------------|---------------------------|
| Banners | `HomeViewModel` → Firestore `banners` | `LinearProgressIndicator` + spinner sección | `home_banners_empty` | **No** |
| Categorías | Firestore `categories` | Spinner sección | `home_categories_empty` | **No** |
| Destacado | Firestore `products` featured | Oculto si null | Sección no se muestra | **No** |
| Bienvenida | `strings.xml` (estático) | N/A | N/A | **No** (copy genérico) |
| Preview Compose | `previewHomeUiState()` privado | Solo `@Preview` | Solo `@Preview` | **No** en APK release |

---

## 6. Navegación

| Verificación | Estado |
|--------------|--------|
| Rutas `NavGraph` sin cambios | **Confirmado** |
| Categorías → `CategoryList` / `ServiciosSello` | **Intacto** (`onNavigateToCategory(it.name)`) |
| Producto destacado → `ProductDetail` | **Intacto** (`onNavigateToDetail(product.id)`) |
| Búsqueda → `Search` | **Intacto** |
| Novedades → `NewsDetail` | **Intacto** ("Ver todo") |
| Bottom nav tabs | **Sin cambios** (UI-STITCH-1) |
| Ruta offline automática | **Sin cambios** en `MainScaffold` |

---

## 7. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **BUILD SUCCESSFUL** | ~1m 09s |
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
| No mocks visibles en release | Sí |
| No commit | Sí |
| No ViewModels modificados | Sí |
| No NavGraph modificado | Sí |

---

## 9. Próximo paso recomendado

### UI-STITCH-3 — Categorías / comercios / productos

1. Aplicar `SancarlinaCard`, chips y top bar a [`CategoryListContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListContent.kt).
2. Alinear [`ProductDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailContent.kt) con `detalle_de_producto/screen.png`.
3. Reutilizar `HomeCategoryGrid` / `HomeHeroCard` patrones donde aplique.

### Hotfix

No requerido — build/lint/tests OK.

---

*Validación Gradle completada. Sin commit.*
