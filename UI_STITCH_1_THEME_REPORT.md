# UI-STITCH-1 — Theme and Base Components Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Fase:** UI-STITCH-1 — Theme global + componentes base + bottom nav

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Theme actualizado?** | **Sí** |
| **¿Componentes base creados?** | **Sí** (5 archivos nuevos) |
| **¿Cambio visual global aplicado?** | **Sí** — `GondolappBottomBar` en `MainScaffold.kt` |
| **¿Build OK?** | **Sí** — `assembleDebug` exitoso |
| **¿Lint OK?** | **Sí** — `lintDebug` exitoso (warnings deprecation preexistentes/nuevos menores) |
| **¿Unit tests OK?** | **Sí** — `testDebugUnitTest` exitoso |
| **Riesgo** | **Bajo** — sin cambios en ViewModels, NavGraph, Firebase ni pantallas completas |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`app/src/main/java/com/sancarlina/app/ui/theme/Color.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Color.kt) | Tokens M3 completos Stitch + `SancarlinaNavIndicator` | Paleta DESIGN.md |
| [`app/src/main/java/com/sancarlina/app/ui/theme/Type.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Type.kt) | Escala tipográfica Be Vietnam Pro / Manrope (fallback SansSerif) | Alinear jerarquía Stitch |
| [`app/src/main/java/com/sancarlina/app/ui/theme/Shape.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Shape.kt) | Chips 12dp, cards 24dp, sheets 28dp, bottom bar 24dp top | Hyper-roundedness Stitch |
| [`app/src/main/java/com/sancarlina/app/ui/theme/Theme.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Theme.kt) | `lightColorScheme` con surface-container/error; status + nav bar cream | Material 3 completo |
| [`app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) | Bottom nav pill olive, fondo surface-container, esquinas superiores | Primer cambio visual global |
| [`app/src/main/java/com/sancarlina/app/ui/components/SancarlinaButtons.kt`](app/src/main/java/com/sancarlina/app/ui/components/SancarlinaButtons.kt) | **Nuevo** | Botones primary/secondary reutilizables |
| [`app/src/main/java/com/sancarlina/app/ui/components/SancarlinaCard.kt`](app/src/main/java/com/sancarlina/app/ui/components/SancarlinaCard.kt) | **Nuevo** | Cards 24dp tonal y elevada |
| [`app/src/main/java/com/sancarlina/app/ui/components/SancarlinaTopBar.kt`](app/src/main/java/com/sancarlina/app/ui/components/SancarlinaTopBar.kt) | **Nuevo** | Top bar centrada con back opcional |
| [`app/src/main/java/com/sancarlina/app/ui/components/SancarlinaChips.kt`](app/src/main/java/com/sancarlina/app/ui/components/SancarlinaChips.kt) | **Nuevo** | Chip y filter chip 12dp |
| [`app/src/main/java/com/sancarlina/app/ui/components/SancarlinaTextField.kt`](app/src/main/java/com/sancarlina/app/ui/components/SancarlinaTextField.kt) | **Nuevo** | Input filled estilo Stitch |

---

## 3. Tokens Stitch aplicados

| Token | Valor / descripción | Archivo | Estado |
|-------|---------------------|---------|--------|
| Primary olive | `#476500` | `Color.kt` | Confirmado (ya existía) |
| Primary container | `#5D7F13` | `Color.kt` | Confirmado |
| Secondary burgundy | `#A33A47` | `Color.kt` | Confirmado |
| Background cream | `#F9F9F6` | `Color.kt`, `Theme.kt` | Confirmado |
| Surface container | `#EEEEEB` | `Color.kt`, bottom nav | **Aplicado** |
| Surface container highest | `#E2E3E0` | `Color.kt` | **Nuevo** |
| Error M3 | `#BA1A1A` | `Color.kt`, `Theme.kt` | **Nuevo** |
| Primary fixed dim | `#ADD461` | `Color.kt` | **Nuevo** — pill nav |
| Chip radius | 12dp | `Shape.kt` | **Aplicado** |
| Card/button radius | 24dp | `Shape.kt` | **Aplicado** |
| Sheet top radius | 28dp | `Shape.kt` | **Definido** (uso en fases posteriores) |
| Headline lg mobile | Be Vietnam Pro 28/34 | `Type.kt` | **Aplicado** (fallback) |
| Body md | Manrope 14/20 | `Type.kt` | **Aplicado** (fallback) |
| Label nav | Manrope 11sp | `Type.kt`, bottom nav | **Aplicado** |
| Nav pill indicator | Olive 35% alpha | `SancarlinaNavIndicator` | **Aplicado** en bottom bar |

---

## 4. Fuentes

| Pregunta | Respuesta |
|----------|-----------|
| **¿Be Vietnam Pro encontrada localmente?** | **No** — búsqueda en repo: 0 archivos `.ttf`/`.otf`/`.woff` |
| **¿Manrope encontrada localmente?** | **No** |
| **¿Se agregaron fuentes a res/font?** | **No** — no hay binarios locales válidos |
| **Fallback usado** | `FontFamily.SansSerif` para `BeVietnamPro` y `Manrope` en `Type.kt` |
| **Observación** | Para tipografía real Stitch: añadir TTF a `app/src/main/res/font/` en fase posterior (sin descarga automática en UI-STITCH-1). |

---

## 5. Componentes creados

| Componente | Archivo | Uso esperado | Estado |
|------------|---------|--------------|--------|
| `SancarlinaPrimaryButton` | `SancarlinaButtons.kt` | CTAs olive 24dp | Listo + Preview |
| `SancarlinaSecondaryButton` | `SancarlinaButtons.kt` | Acciones burgundy outline | Listo + Preview |
| `SancarlinaCard` | `SancarlinaCard.kt` | Listas, home, categorías | Listo + Preview |
| `SancarlinaElevatedCard` | `SancarlinaCard.kt` | Cards con sombra | Listo + Preview |
| `SancarlinaTopBar` | `SancarlinaTopBar.kt` | Pantallas secundarias | Listo (sin Preview) |
| `SancarlinaChip` | `SancarlinaChips.kt` | Tags categorías | Listo + Preview |
| `SancarlinaFilterChip` | `SancarlinaChips.kt` | Filtros mapa/categoría | Listo + Preview |
| `SancarlinaTextField` | `SancarlinaTextField.kt` | Auth, búsqueda, forms | Listo (sin Preview) |

**Nota:** Componentes creados pero **aún no conectados** a pantallas existentes (alcance UI-STITCH-1).

---

## 6. Cambio visual global

### MainScaffold / `GondolappBottomBar`

| Aspecto | Antes | Después |
|---------|-------|---------|
| Fondo barra | `SancarlinaSurfaceContainerLowest` (blanco) + sombra 16dp | `MaterialTheme.colorScheme.surfaceContainer` (cream `#EEEEEB`) |
| Esquinas | Rectas | `SancarlinaBottomBarShape` — 24dp arriba |
| Ítem activo | Pill 10% primary alpha + `SancarlinaPrimary` | Pill `SancarlinaNavIndicator` (olive `#ADD461` 35%) + primary |
| Ítem inactivo | `SancarlinaOutline` | `onSurfaceVariant` (más legible M3) |
| Tipografía labels | `11.sp` hardcoded | `MaterialTheme.typography.labelSmall` |
| Altura | 80dp | 72dp + padding vertical 8dp |
| Navegación | Intacta (`popUpTo`, `saveState`, `restoreState`) | **Sin cambios** |

**Riesgo:** Bajo — solo capa visual; rutas y lógica offline intactas.

**Theme global adicional:** `Theme.kt` ahora pinta status bar y navigation bar del sistema con cream/surface-container (visible al abrir cualquier pantalla con `SancarlinaTheme`).

---

## 7. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **BUILD SUCCESSFUL** | ~1m 55s |
| `.\gradlew.bat :app:lintDebug` | **BUILD SUCCESSFUL** | Reporte HTML en `app/build/reports/lint-results-debug.html` |
| `.\gradlew.bat :app:testDebugUnitTest` | **BUILD SUCCESSFUL** | Sin fallos |

**Warnings compile (no bloqueantes):**
- `centerAlignedTopAppBarColors` deprecated → `topAppBarColors` (MainScaffold, SancarlinaTopBar)
- `statusBarColor` / `navigationBarColor` deprecated en API 35+

---

## 8. Confirmación de alcance

| Restricción | Cumplida |
|-------------|----------|
| No WebView | Sí |
| No HTML runtime | Sí |
| No Firebase | Sí |
| No Firebase deploy | Sí |
| No Firestore Rules | Sí |
| No Storage | Sí |
| No Functions | Sí |
| No Gradle versions | Sí |
| No dependencies | Sí |
| No versionCode/versionName | Sí |
| No AAB/keystore | Sí |
| No applicationId/package | Sí |
| No assets Stitch borrados/movidos | Sí |
| No commit | Sí |
| No reescritura pantallas completas | Sí |
| No ViewModels tocados | Sí |

---

## 9. Próximo paso recomendado

### Si todo está OK → **UI-STITCH-2: Home + navegación principal**

1. Reemplazar layout de [`HomeContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/HomeContent.kt) según `docs/stitch/home/screen.png`.
2. Usar `SancarlinaCard`, `SancarlinaChip`, `SancarlinaElevatedCard`.
3. Alinear top bar del scaffold con diseño Stitch (logo centrado ya existe).
4. Opcional: añadir TTF Be Vietnam Pro + Manrope a `res/font/` antes de UI-STITCH-2 para tipografía visible.

### Hotfix mínimo (solo si hiciera falta)

- Ninguno requerido tras build/lint/tests OK.
- Deprecation warnings de TopAppBar pueden corregirse en UI-STITCH-2 al tocar `MainScaffold` top bar.

---

*Reporte generado tras validación Gradle. Sin commit.*
