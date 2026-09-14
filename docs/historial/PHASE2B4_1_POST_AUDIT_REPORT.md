# PHASE 2B-4.1 — Post Audit Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Tipo:** Auditoría post-implementación (solo lectura + Gradle)  
**Referencia:** [`PHASE2B4_1_MOCKS_CLEANUP_REPORT.md`](PHASE2B4_1_MOCKS_CLEANUP_REPORT.md)

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Implementación aceptable?** | **Sí** |
| **¿Se puede cerrar 2B-4.1?** | **Sí** |
| **Bloqueadores** | Ninguno |
| **Riesgos menores** | (1) `EditProfileContent.kt:113` — placeholder de imagen preexistente (`via.placeholder.com`), fuera del alcance 2B-4.1. (2) `TurismoViewModel.kt:15-17` — `tenantsRepository`/`areasRepository` inyectados pero no usados (código muerto, sin impacto release). (3) Probar con `assembleDebug` sigue teniendo `BuildConfig.DEBUG=true`; los mocks DEBUG solo desaparecen en variant **release** (`assembleRelease`). (4) Favoritos e historial de puntos siempre vacíos hasta integrar backend — comportamiento esperado, no bug. |

---

## 2. Estado de comandos

| Comando | Resultado | Warnings relevantes |
|---------|-----------|---------------------|
| `git status --short` | 13 archivos `.kt`/`strings.xml` modificados + `?? PHASE2B4_1_MOCKS_CLEANUP_REPORT.md` | Sin cambios fuera de alcance |
| `./gradlew :app:assembleDebug` | **OK** (BUILD SUCCESSFUL) | — |
| `./gradlew :app:lintDebug` | **OK** | **0 errores**; warnings preexistentes (ej. `DefaultLocale` en `PointsViewModel.kt:74`, `RedundantLabel` en `AndroidManifest.xml:34`, `UnusedResources` en `colors.xml`) — no introducidos por 2B-4.1 |
| `./gradlew :app:testDebugUnitTest` | **OK** | — |

---

## 3. Revisión por pantalla

| Pantalla | Archivo | Estado release | Estado debug | Riesgo | Recomendación |
|----------|---------|----------------|--------------|--------|---------------|
| **Favoritos** | `FavoritesContent.kt` L35-36, L61-62 | Lista vacía → `EmptyFavorites` L82-125 | Igual (sin mock) | Bajo | Integrar `userProfiles.favoriteTenantIds` en fase futura |
| **Historial puntos** | `PointsHistoryContent.kt` L27-28, L52-53 | `PointsHistoryEmptyState` L134-163 | Igual | Bajo | Conectar a `AuditLogs` o colección dedicada |
| **Turismo** | `TurismoViewModel.kt` L34-39; `TurismoContent.kt` L108-116 | `points` vacío → `TurismoEmptyState` | Mock en L49-66 vía `BuildConfig.DEBUG` | Bajo | Cargar desde Firestore cuando exista schema |
| **Lista categoría** | `CategoryListViewModel.kt` L50-55, L70-81; `CategoryListContent.kt` L101-102, L128-161 | Vacío/error sin mock | Mock `(Debug)` L100-134 | Bajo | OK para pre-Play |
| **Mapa** | `MapViewModel.kt` L74-77, L81-84, L90-98 | `clearMarkers()` — mapa vacío | `loadMockMarkers()` L102-137 | Bajo | OK |
| **Detalle producto** | `ProductDetailViewModel.kt` L33-36, L40-43; `ProductDetailContent.kt` L52-53, L227-262 | `notFound=true` → UI error | Mock L49-60 | Bajo | OK |
| **Home** | `HomeViewModel.kt` L24-25, L32-89 | Inicio `isLoading=true`, sin datos ficticios iniciales | `seedFirestore()` bloqueado L95 | Bajo | OK |

---

## 4. Mocks restantes

| Archivo | Mock restante | Protegido por `BuildConfig.DEBUG` | Visible en release | Estado |
|---------|---------------|-----------------------------------|--------------------|--------|
| `TurismoViewModel.kt` L49-66 | `loadDebugMockPoints()` — 2 puntos turísticos | **Sí** (L34) | **No** | Aceptable |
| `CategoryListViewModel.kt` L100-134 | `loadMockData()` — 2 comercios `(Debug)` | **Sí** (L51, L70) | **No** | Aceptable |
| `MapViewModel.kt` L102-137 | `loadMockMarkers()` — 2 marcadores `(Demo)` | **Sí** (L74, L81) | **No** | Aceptable |
| `ProductDetailViewModel.kt` L49-60 | `loadMockProduct()` — producto `(Debug)` | **Sí** (L33, L40) | **No** | Aceptable |
| `HomeViewModel.kt` L96-113 | `seedFirestore()` — categories + commerces | **Sí** (L95) | **No** | Aceptable (sin callers UI) |
| `FavoritesContent.kt` L36 | `emptyList()` — no mock | N/A | **No** datos ficticios | OK |
| `PointsHistoryContent.kt` L28 | `emptyList()` — no mock | N/A | **No** datos ficticios | OK |
| `EditProfileContent.kt` L113 | URL `via.placeholder.com` (preexistente) | **No** | **Sí** (solo avatar vacío) | Menor — fuera 2B-4.1 |

**Confirmado por código:** todos los mocks de negocio (comercios, productos, turismo, mapa) están detrás de `BuildConfig.DEBUG` o fueron eliminados.

---

## 5. Empty states

| Pantalla | Empty state implementado | Texto visible | Riesgo UX |
|----------|-------------------------|---------------|-----------|
| Favoritos | **Sí** — `EmptyFavorites` | "Aún no tienes favoritos" + CTA explorar (hardcoded L103-124) | Bajo — claro para usuario |
| Historial puntos | **Sí** — `PointsHistoryEmptyState` | `points_history_empty_title` / `_message` (`strings.xml` L19-20) | Bajo |
| Turismo | **Sí** — `TurismoEmptyState` | `turismo_empty_title` / `_message` (`strings.xml` L21-22) | Bajo — "Próximamente" honesto |
| Categorías | **Sí** — `CategoryListEmptyState` | `category_list_empty_*` o `category_list_error_message` (`strings.xml` L23-25) | Bajo |
| Producto | **Sí** — `ProductNotFoundState` | `product_not_found_*` (`strings.xml` L26-27) | Bajo |
| Mapa | **Implícito** — sin marcadores | Mapa vacío (sin mensaje dedicado) | Medio-bajo — usuario puede no entender por qué está vacío; mejora futura opcional |
| Home | **Implícito** — listas vacías tras load | Sin banners/categorías si Firestore vacío | Bajo |

**Loading/error:** `TurismoContent.kt` L108-114 (loading), `CategoryListContent.kt` L97-100 (loading), `ProductDetailContent.kt` L48-51 (loading), `CategoryListUiState.hasLoadError` — **no rotos**.

**Listas vacías:** no hay `items()` sobre listas no inicializadas; uso de `emptyList()` seguro — **sin crashes evidentes por revisión estática**.

---

## 6. Cambios fuera de alcance

| Área | ¿Tocado? |
|------|----------|
| Firebase rules / deploy | **No** |
| Functions | **No** |
| Gradle versions (`build.gradle.kts`, `libs.versions.toml`) | **No** |
| versionCode / versionName | **No** |
| keystore / AAB / firma | **No** |
| assets Stitch | **No** |
| commits | **No** |

**Archivos modificados (solo 2B-4.1):** 12 Kotlin + `strings.xml` — confirmado por `git diff --name-only`.

---

## 7. Próximo paso recomendado

**Cerrar 2B-4.1** — la implementación cumple el objetivo pre-Play de no mostrar datos demo engañosos en **release**.

Siguiente fase (cuando el usuario lo autorice):

1. **2B-4.2** — documentación técnica (`docs/DEV.md`, reglas npm/JDK).
2. Antes de Play: reemplazar `privacy_policy_url` en `strings.xml:5` (operativo municipal).
3. Opcional post-2B-4.1: empty state en mapa vacío; integrar favoritos/historial reales.

**No se requiere prompt de corrección** salvo que se quiera abordar `EditProfileContent.kt:113` o empty state del mapa en una micro-fase aparte.

---

*Auditoría estática + Gradle. No se modificó código de la app durante este reporte.*
