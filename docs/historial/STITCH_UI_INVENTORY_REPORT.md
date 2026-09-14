# STITCH UI — Inventory and Mapping Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Tipo:** Inventario y mapeo (solo lectura — sin cambios de código)

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Carpeta Stitch encontrada?** | **Sí** (dos ubicaciones) |
| **Ubicación** | [`app/src/main/assets/stitch/`](app/src/main/assets/stitch/) (36 carpetas, 71 archivos, ~11 MB APK) y [`docs/stitch/`](docs/stitch/) (41 carpetas, 78 archivos, ~8,7 MB referencia) |
| **¿Hay diseños suficientes para aplicar a la app?** | **Sí** — 36+ pantallas con `code.html` + `screen.png` + `DESIGN.md` M3 completo |
| **Estrategia recomendada** | Aplicar **theme global primero** (fuentes Be Vietnam Pro / Manrope, shapes 24dp, componentes base), luego **repintar pantallas por olas** usando `screen.png` como referencia visual y `code.html` solo como guía de layout (no WebView). Priorizar Home + navegación, luego catálogo, perfil/puntos, mapa/QR. |
| **Riesgo general** | **Medio** — colores ya alineados parcialmente; tipografía y componentes base desalineados; muchas pantallas Compose ya existen con lógica Firebase que no debe romperse. |

**Confirmado por archivos:** duplicación assets/docs; `Color.kt` ya usa paleta Stitch; `Type.kt` aún usa `FontFamily.SansSerif` placeholder (no Be Vietnam Pro / Manrope).

---

## 2. Archivos Stitch detectados

### Resumen por ubicación

| Ruta raíz | Carpetas | Archivos | Tamaño aprox. | Uso runtime |
|-----------|----------|----------|---------------|-------------|
| [`app/src/main/assets/stitch/`](app/src/main/assets/stitch/) | 36 | 71 | ~10,99 MB | **Empaquetado en APK**; **no referenciado** desde Kotlin (confirmado: sin `file:///android_asset` ni `stitch/` en `app/src/main/java`) |
| [`docs/stitch/`](docs/stitch/) | 41 | 78 | ~8,65 MB | **Solo referencia** para desarrollo; incluye 5 carpetas extra vs assets |

### Carpetas solo en `docs/stitch/` (no en assets)

| Carpeta | Contenido |
|---------|-----------|
| `16._di_logos_de_confirmaci_n` | HTML + PNG (variante logos éxito) |
| `buscador_avanzado` | HTML + PNG |
| `high_quality_professional_photography_of_a_sun_drenched_vineyard_in_san_carlos` | Solo PNG (imagen hero) |
| `img_5841.jpg_removebg_preview_logotipo.png` | Solo PNG (logotipo) |
| `img_5842.jpg_removebg_preview.png` | Solo PNG (símbolo) |

### Tipos de archivo por pantalla (patrón típico)

| Tipo | Ruta ejemplo | Qué contiene | Relevancia |
|------|--------------|--------------|------------|
| **DESIGN.md** | [`docs/stitch/sancarlina_mobile_experience/DESIGN.md`](docs/stitch/sancarlina_mobile_experience/DESIGN.md) | Tokens M3: colores, tipografía, spacing, shapes, componentes | **Alta** — fuente de verdad del design system |
| **code.html** | `docs/stitch/home/code.html` | Tailwind + Google Fonts + Material Symbols; layout responsive | **Media** — guía de estructura, no runtime |
| **screen.png** | `docs/stitch/home/screen.png` | Captura visual de referencia | **Alta** — validación visual rápida en Compose |
| **CSS** | Inline en HTML (Tailwind CDN) | No hay `.css` sueltos | Bajo — tokens están en `tailwind.config` del HTML |

### Inventario de pantallas Stitch (41 carpetas en docs)

| # | Carpeta Stitch | HTML | PNG |
|---|----------------|------|-----|
| 1 | `splash_screen` | Sí | Sí |
| 2 | `onboarding_descubre` | Sí | Sí |
| 3 | `login` | Sí | Sí |
| 4 | `registro` | Sí | Sí |
| 5 | `recuperar_contrase_a` | Sí | Sí |
| 6 | `home` | Sí | Sí |
| 7 | `novedades` | Sí | Sí |
| 8 | `detalle_de_noticia` | Sí | Sí |
| 9 | `buscador_avanzado` | Sí | Sí |
| 10 | `filtros_avanzados` | Sí | Sí |
| 11 | `artesan_as` | Sí | Sí |
| 12 | `detalle_de_producto` | Sí | Sí |
| 13 | `galer_a_de_fotos` | Sí | Sí |
| 14 | `mapa_interactivo` | Sí | Sí |
| 15 | `detalle_en_mapa_bottom_sheet` | Sí | Sí |
| 16 | `explora_turismo` | Sí | Sí |
| 17 | `favoritos` | Sí | Sí |
| 18 | `panel_de_puntos` | Sí | Sí |
| 19 | `historial_de_puntos` | Sí | Sí |
| 20 | `esc_ner_qr` | Sí | Sí |
| 21 | `di_logos_de_confirmaci_n` | Sí | Sí |
| 22 | `perfil_de_usuario` | Sí | Sí |
| 23 | `editar_perfil` | Sí | Sí |
| 24 | `perfil_de_comercio` | Sí | Sí |
| 25 | `calificar_comercio` | Sí | Sí |
| 26 | `rese_as_de_usuarios` | Sí | Sí |
| 27 | `notificaciones` | Sí | Sí |
| 28 | `ajustes_de_notificaciones` | Sí | Sí |
| 29 | `legal_y_privacidad` | Sí | Sí |
| 30 | `ayuda_y_soporte` | Sí | Sí |
| 31 | `informaci_n_institucional` | Sí | Sí |
| 32 | `sumar_emprendimiento` | Sí | Sí |
| 33 | `pantalla_de_xito` | Sí | Sí |
| 34 | `offline_error` | Sí | Sí |
| 35 | `servicios_sello_de_calidad` | Sí | Sí |
| 36 | `gu_a_r_pida` | Sí | Sí |
| 37 | `16._di_logos_de_confirmaci_n` | Sí | Sí |
| 38–41 | assets logo / foto hero | Parcial | Sí |

---

## 3. Línea visual detectada

Fuente primaria: [`docs/stitch/sancarlina_mobile_experience/DESIGN.md`](docs/stitch/sancarlina_mobile_experience/DESIGN.md) y HTML Tailwind (ej. [`docs/stitch/home/code.html`](docs/stitch/home/code.html)).

| Elemento | Descripción (Stitch) | Estado en app actual | Cómo trasladarlo a Compose |
|----------|-------------------|---------------------|----------------------------|
| **Colores primary** | Olive `#476500`, container `#5D7F13` | **Alineado** — [`Color.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Color.kt) L6-9 | Ya en `MaterialTheme`; revisar surface containers M3 |
| **Colores secondary** | Burgundy `#A33A47` | **Alineado** — `Color.kt` L11-14 | Accents, FAB, notificaciones |
| **Background** | Cream `#F9F9F6` | **Alineado** — `Color.kt` L21-24 | `MaterialTheme.colorScheme.background` |
| **Tipografía headlines** | Be Vietnam Pro 24–32px semibold/bold | **No alineado** — [`Type.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Type.kt) usa `FontFamily.SansSerif` placeholder | Añadir `.ttf` en `res/font/` + `FontFamily` en UI-STITCH-1 |
| **Tipografía body** | Manrope 14–16px | **No alineado** — placeholder DMSans/SansSerif | Idem |
| **Shapes** | Chips 8–12dp, cards/buttons 24dp, sheets 28dp top | **Parcial** — [`Shape.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Shape.kt) L7-11 (8–32dp) | Ajustar tokens + componentes |
| **Spacing** | 4dp baseline, 16dp margin mobile, stacks 8/16/24 | **Parcial** — hardcoded `dp` en pantallas | `Dimens.kt` opcional en UI-STITCH-1 |
| **Top app bar** | Medium M3, logo centrado gondolApp | **Parcial** — drawer + top bars por pantalla | `SancarlinaTopBar` componente |
| **Bottom navigation** | M3 con pill indicator olive | **Existe** — [`MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) L50-55, bottom nav | Reestilar indicator/colores Stitch |
| **Botones** | Primary olive 24dp radius, secondary burgundy outline | **Parcial** — Material3 defaults con colores Sancarlina | `SancarlinaButton` variants |
| **Cards** | Elevated 24dp, padding 16dp, sombra olive 10–15% | **Parcial** — `CommerceCard`, etc. con 20–24dp | Unificar en `SancarlinaCard` |
| **Inputs** | Filled, stroke inferior olive, top 12dp radius | **Parcial** — `OutlinedTextField` en auth | `SancarlinaTextField` |
| **Empty states** | Ilustración circular, título + CTA | **Parcial** — [`EmptyStateContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/common/EmptyStateContent.kt), empty states 2B-4.1 | Alinear con `favoritos` / `historial_de_puntos` Stitch PNG |
| **Map markers** | Icono circular + pin burgundy | **No revisado** — Google Maps markers en `MapContent` | Fase UI-STITCH-5 |
| **Iconografía** | Material Symbols Outlined | **Parcial** — `Icons.Default.*` Material | Migrar a `material-icons-extended` o vector assets si hace falta |

---

## 4. Pantallas Stitch detectadas

| Pantalla Stitch | Archivo/ruta | Equivalente en app | Estado |
|-----------------|--------------|-------------------|--------|
| Splash | `splash_screen/` | `SplashContent.kt` | Existe — estilo por pulir |
| Onboarding | `onboarding_descubre/` | `OnboardingContent.kt` | Existe |
| Login | `login/` | `LoginContent.kt` | Existe |
| Registro | `registro/` | `RegisterContent.kt` | Existe |
| Recuperar contraseña | `recuperar_contrase_a/` | `ForgotPasswordContent.kt` | Existe |
| Home | `home/` | `HomeContent.kt` | Existe — layout Stitch más rico (bento) |
| Búsqueda | `buscador_avanzado/` (solo docs) | `SearchContent.kt` | Existe — sin equivalente en assets |
| Novedades | `novedades/` | `NewsDetailContent.kt` (parcial) | Parcial |
| Detalle noticia | `detalle_de_noticia/` | `NewsDetailContent.kt` | Existe |
| Categoría / artesanías | `artesan_as/` | `CategoryListContent.kt` | Existe |
| Filtros | `filtros_avanzados/` | `CategoryListContent.kt` (bottom sheet) | Parcial |
| Detalle producto | `detalle_de_producto/` | `ProductDetailContent.kt` | Existe |
| Galería fotos | `galer_a_de_fotos/` | `ProductDetailContent.kt` (gallery) | Parcial |
| Mapa | `mapa_interactivo/` | `MapContent.kt` | Existe |
| Bottom sheet mapa | `detalle_en_mapa_bottom_sheet/` | `MapContent.kt` | Parcial |
| Turismo | `explora_turismo/` | `TurismoContent.kt` | Existe |
| Favoritos | `favoritos/` | `FavoritesContent.kt` | Existe |
| Panel puntos | `panel_de_puntos/` | `BenefitsContent.kt` | Existe |
| Historial puntos | `historial_de_puntos/` | `PointsHistoryContent.kt` | Existe |
| Escanear QR | `esc_ner_qr/` | `QrScannerContent.kt` | Existe |
| Perfil | `perfil_de_usuario/` | `ProfileContent.kt` | Existe |
| Editar perfil | `editar_perfil/` | `EditProfileContent.kt` | Existe |
| Perfil comercio | `perfil_de_comercio/` | `CommerceProfileContent.kt` | Existe |
| Calificar comercio | `calificar_comercio/` | `RateCommerceContent.kt` | Existe |
| Reseñas | `rese_as_de_usuarios/` | (sin pantalla dedicada) | **Gap** |
| Notificaciones | `notificaciones/` | `NotificationsContent.kt` | Existe |
| Ajustes notificaciones | `ajustes_de_notificaciones/` | `NotificationSettingsContent.kt` | Existe |
| Legal / privacidad | `legal_y_privacidad/` | `LegalContent.kt` + `SupportContent.kt` | Existe |
| Ayuda | `ayuda_y_soporte/` | `SupportContent.kt` | Existe |
| Institucional | `informaci_n_institucional/` | (sin ruta dedicada) | **Gap** |
| Sumar emprendimiento | `sumar_emprendimiento/` | `EmprendimientoContent.kt` | Existe |
| Éxito | `pantalla_de_xito/` | `SuccessContent.kt` | Existe |
| Logos éxito | `di_logos_de_confirmaci_n/` | `SuccessContent.kt` (modal) | Parcial |
| Offline | `offline_error/` | `OfflineContent.kt` | Existe |
| Sello calidad | `servicios_sello_de_calidad/` | `ServiciosSelloContent.kt` | Existe |
| Guía rápida | `gu_a_r_pida/` | `FeatureDiscovery.kt` | Existe (overlay) |

---

## 5. Mapeo contra app actual

| Pantalla app | Archivo Compose actual | Diseño Stitch equivalente | Acción recomendada | Riesgo |
|--------------|------------------------|---------------------------|-------------------|--------|
| Splash | [`SplashContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/splash/SplashContent.kt) | `splash_screen/` | Aplicar estilo global + logo assets docs | Bajo |
| Onboarding | [`OnboardingContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/auth/OnboardingContent.kt) | `onboarding_descubre/` | Repintar slides vs PNG | Bajo |
| Login / Register / Forgot | `auth/*Content.kt` | `login/`, `registro/`, `recuperar_*` | Inputs + layout Stitch; **no tocar** `AuthViewModel` | Medio (Firebase Auth) |
| Home | [`HomeContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/HomeContent.kt) | `home/` | **Rework visual** bento banners/categorías; mantener `HomeViewModel` | Medio |
| Search | [`SearchContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/SearchContent.kt) | `buscador_avanzado/` | Alinear search bar + resultados | Bajo |
| Category list | [`CategoryListContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/category/CategoryListContent.kt) | `artesan_as/`, `filtros_avanzados/` | Cards + filtros; mantener `CategoryListViewModel` | Medio |
| Product detail | [`ProductDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/product/ProductDetailContent.kt) | `detalle_de_producto/`, `galer_a_de_fotos/` | Hero + galería; mantener `ProductDetailViewModel` | Medio |
| Map | [`MapContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/MapContent.kt) | `mapa_interactivo/`, `detalle_en_mapa_bottom_sheet/` | UI filtros + bottom sheet; **cuidado** `MapViewModel` + Maps SDK | Alto |
| Turismo | [`TurismoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/turismo/TurismoContent.kt) | `explora_turismo/` | Cards turismo; datos Firestore pendientes | Medio |
| Favoritos | [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | `favoritos/` | Ya tiene empty state; alinear visual | Bajo |
| Puntos / beneficios | [`BenefitsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/BenefitsContent.kt) | `panel_de_puntos/` | QR CTA + grid beneficios; **cuidado** `PointsViewModel` | Medio |
| Historial puntos | [`PointsHistoryContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/PointsHistoryContent.kt) | `historial_de_puntos/` | Empty state Stitch | Bajo |
| QR Scanner | [`QrScannerContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/QrScannerContent.kt) | `esc_ner_qr/` | Overlay + permisos cámara intactos | Medio |
| Perfil | [`ProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/ProfileContent.kt) | `perfil_de_usuario/` | Menú opciones; mantener `ProfileViewModel` | Medio |
| Editar perfil | [`EditProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileContent.kt) | `editar_perfil/` | Form + avatar; **cuidado** `UserRepository` | Medio |
| Comercio / Rate | `CommerceProfileContent.kt`, `RateCommerceContent.kt` | `perfil_de_comercio/`, `calificar_comercio/` | Repintar; ViewModels con Firestore | Medio |
| Notificaciones | `NotificationsContent.kt`, `NotificationSettingsContent.kt` | `notificaciones/`, `ajustes_*` | Lista + toggles | Bajo |
| Legal / Support | `LegalContent.kt`, `SupportContent.kt` | `legal_y_privacidad/`, `ayuda_y_soporte/` | Texto + links | Bajo |
| Emprendimiento / Success | `EmprendimientoContent.kt`, `SuccessContent.kt` | `sumar_emprendimiento/`, `pantalla_de_xito/` | Form + confirmación | Bajo |
| Offline | [`OfflineContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/common/OfflineContent.kt) | `offline_error/` | Ilustración Stitch | Bajo |
| Servicios sello | [`ServiciosSelloContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/servicios/ServiciosSelloContent.kt) | `servicios_sello_de_calidad/` | Repintar | Bajo |
| Navegación principal | [`MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) | Home bottom nav en Stitch HTML | Bottom bar pill + drawer | Medio |
| **Sin ruta NavGraph** | [`UpdatesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/updates/UpdatesContent.kt) | `novedades/` (lista) | Wire o eliminar en fase aparte | Bajo |
| **Sin pantalla** | — | `rese_as_de_usuarios/`, `informaci_n_institucional/` | Crear o fusionar en comercio/support | Medio |

### Clasificación resumida

| Clasificación | Pantallas |
|---------------|-----------|
| **Existe en Stitch y en app** | ~32 |
| **Existe en Stitch, no en app** | `rese_as_de_usuarios`, `informaci_n_institucional`, assets logo standalone |
| **Existe en app, Stitch parcial** | `UpdatesContent` (no en NavGraph), galería producto, reseñas |
| **Estilo global sin rework completo** | Offline, Legal, Success, empty states, auth backgrounds |
| **Rework visual profundo** | Home (bento), Mapa + bottom sheet, Panel puntos, Home bottom nav |

---

## 6. Componentes reutilizables a crear

| Componente | Uso | Archivo sugerido | Prioridad |
|----------|-----|-----------------|-----------|
| `SancarlinaTopBar` | App bars medianas con back / título / acciones | `ui/components/SancarlinaTopBar.kt` | P0 |
| `SancarlinaPrimaryButton` / `SecondaryButton` | CTAs olive / burgundy 24dp | `ui/components/SancarlinaButtons.kt` | P0 |
| `SancarlinaCard` | Cards elevadas 24dp (comercio, producto, turismo) | `ui/components/SancarlinaCard.kt` | P0 |
| `SancarlinaTextField` | Inputs filled estilo Stitch | `ui/components/SancarlinaTextField.kt` | P1 |
| `SancarlinaChip` / `FilterChipRow` | Categorías home, filtros mapa/categoría | `ui/components/SancarlinaChips.kt` | P1 |
| `SancarlinaEmptyState` | Unificar empty states (ya existe `EmptyStateContent`) | Extender [`EmptyStateContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/common/EmptyStateContent.kt) | P1 |
| `SancarlinaBottomBar` | Pill indicator M3 | Refactor en [`MainScaffold.kt`](app/src/main/java/com/sancarlina/app/ui/components/MainScaffold.kt) | P1 |
| `SancarlinaBannerCarousel` | Home banners horizontales | `ui/components/SancarlinaBannerCarousel.kt` | P2 |
| `SancarlinaCategoryGrid` | Grid categorías home | `ui/components/SancarlinaCategoryGrid.kt` | P2 |
| Theme fonts | Be Vietnam Pro + Manrope | `res/font/` + [`Type.kt`](app/src/main/java/com/sancarlina/app/ui/theme/Type.kt) | **P0** |

---

## 7. Plan de implementación UI-STITCH

### UI-STITCH-1 — Theme global y componentes base

| Campo | Detalle |
|-------|---------|
| **Objetivo** | Fuentes, shapes, typography M3, botones/cards/top bar vacíos; **sin reescribir pantallas** |
| **Archivos probables** | `Type.kt`, `Shape.kt`, `Theme.kt`, `res/font/*`, `ui/components/Sancarlina*.kt` |
| **Riesgo** | Bajo |
| **Validación mínima** | `assembleDebug` + preview Compose / una pantalla smoke visual |

### UI-STITCH-2 — Home + navegación principal

| Campo | Detalle |
|-------|---------|
| **Objetivo** | `HomeContent` estilo bento Stitch; bottom nav pill; drawer header |
| **Archivos probables** | `HomeContent.kt`, `MainScaffold.kt`, nuevos componentes banner/grid |
| **Riesgo** | Medio — no romper `HomeViewModel` / navegación categorías |
| **Validación mínima** | Navegar tabs + abrir categoría + producto |

### UI-STITCH-3 — Categorías / comercios / productos

| Campo | Detalle |
|-------|---------|
| **Objetivo** | `CategoryListContent`, `CommerceCard`, `ProductDetailContent`, `SearchContent` |
| **Archivos probables** | `category/*`, `product/*`, `home/SearchContent.kt` |
| **Riesgo** | Medio — ViewModels Firestore |
| **Validación mínima** | Lista vacía + con datos reales; detalle producto not found |

### UI-STITCH-4 — Perfil / puntos / favoritos

| Campo | Detalle |
|-------|---------|
| **Objetivo** | `ProfileContent`, `BenefitsContent`, `PointsHistoryContent`, `FavoritesContent`, `EditProfileContent` |
| **Archivos probables** | `profile/*`, `points/*`, `favorites/*` |
| **Riesgo** | Medio — `PointsViewModel`, `ProfileViewModel`, QR |
| **Validación mínima** | Flujo perfil → historial → favoritos vacío |

### UI-STITCH-5 — Turismo / mapa / QR

| Campo | Detalle |
|-------|---------|
| **Objetivo** | `TurismoContent`, `MapContent`, `QrScannerContent`, bottom sheet mapa |
| **Archivos probables** | `turismo/*`, `map/*`, `points/QrScannerContent.kt` |
| **Riesgo** | **Alto** — Google Maps, permisos, `MapViewModel` |
| **Validación mínima** | Mapa vacío/con markers; QR en dispositivo real |

### UI-STITCH-6 — Pulido visual final

| Campo | Detalle |
|-------|---------|
| **Objetivo** | Auth screens, offline, success, notificaciones, legal; comparar con `screen.png` |
| **Archivos probables** | `auth/*`, `common/*`, `notifications/*`, `legal/*` |
| **Riesgo** | Bajo–medio |
| **Validación mínima** | Walkthrough completo login → home → mapa → puntos → perfil |

---

## 8. Prompt recomendado para UI-STITCH-1

Copiar en **Modo Agente** cuando se apruebe:

```
Proyecto: GondolApp / com.sancarlina.app — UI-STITCH-1 SOLO theme y componentes base.

Objetivo: Alinear design system Stitch (DESIGN.md + docs/stitch/) con Compose Material 3.
NO reescribir pantallas completas todavía.

Referencias obligatorias:
- docs/stitch/sancarlina_mobile_experience/DESIGN.md
- docs/stitch/home/screen.png (validación visual)
- app/src/main/java/.../ui/theme/Color.kt (ya alineado — no romper)

Permitido:
- res/font/ — añadir Be Vietnam Pro y Manrope (.ttf) si el usuario provee fuentes, o documentar descarga Google Fonts
- Type.kt, Shape.kt, Theme.kt
- ui/components/SancarlinaTopBar.kt, SancarlinaButtons.kt, SancarlinaCard.kt, SancarlinaTextField.kt (esqueletos)
- Preview composables en ui/theme/ o ui/components/

Prohibido:
- Cambiar lógica ViewModels, NavGraph rutas, Firebase
- Tocar pantallas *Content.kt salvo 1 Preview de demostración opcional
- Gradle versions, versionCode, keystore, assets/stitch borrar/mover
- firebase deploy

Tareas:
1. Actualizar Typography según DESIGN.md (Be Vietnam Pro headlines, Manrope body).
2. Ajustar Shapes (cards 24dp, chips 12dp, sheets 28dp top).
3. Crear componentes base reutilizables con MaterialTheme.
4. Verificar Color.kt cubre surface-container tokens M3; extender si falta sin cambiar valores hex existentes.
5. ./gradlew :app:assembleDebug :app:lintDebug

Reporte: UI_STITCH_1_THEME_REPORT.md
No commit.
```

---

## 9. Confirmación de alcance

| Área | ¿Tocado en esta tarea? |
|------|------------------------|
| Kotlin / Compose producción | **No** |
| Firebase / deploy | **No** |
| Gradle versions | **No** |
| versionCode / versionName | **No** |
| AAB / keystore | **No** |
| assets Stitch (borrar/mover) | **No** |
| commit | **No** |

**Solo se creó:** [`STITCH_UI_INVENTORY_REPORT.md`](STITCH_UI_INVENTORY_REPORT.md)

---

## Riesgos técnicos (resumen)

| Riesgo | Detalle | Mitigación |
|--------|---------|------------|
| HTML ≠ Compose | Tailwind/flex no mapea 1:1 | Usar `screen.png` como referencia; DESIGN.md para tokens |
| ViewModels acoplados | UI cambia estados loading/error | Cambios solo en capa `@Composable`; mismos `uiState` |
| Fuentes | Type.kt placeholder | UI-STITCH-1 con TTF locales |
| APK size | 11 MB stitch en assets | Fase UI-STITCH-6 o 2B-4.4: mover a docs solo (fuera de este inventario) |
| Pruebas largas | Usuario pide evitar | Validación por fase: 1 pantalla + assembleDebug |

---

*Inventario basado en lectura de archivos del repo. No se ejecutó build ni se modificó código.*
