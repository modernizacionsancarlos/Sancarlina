# HOTFIX-STITCH-NEWS — News Detail Demo Cleanup Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Tipo:** Hotfix mínimo — eliminar demo en `NewsDetailContent`

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Contenido demo eliminado?** | **Sí** |
| **¿NewsDetail queda navegable?** | **Sí** |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo** | **Bajo** — solo UI de una pantalla; sin cambios en Firebase, NavGraph ni ViewModels |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`NewsDetailContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/NewsDetailContent.kt) | Reemplazo de artículo demo por empty state Stitch | Eliminar mock visible en release |
| [`strings.xml`](app/src/main/res/values/strings.xml) | 4 strings: título, unavailable, mensaje, volver | Copy centralizado y honesto |

**Sin cambios:** `HomeContent.kt`, `NavGraph.kt`, `Screen.kt`, ViewModels.

---

## 3. Demo removido

| Contenido anterior | Qué se reemplazó | Estado nuevo |
|--------------------|------------------|--------------|
| Imagen URL hardcodeada (Google aida) | Eliminada | Sin `AsyncImage` ni URL demo |
| Título “Feria de Productores Locales” | Eliminado | `news_detail_unavailable_title` |
| Badge “EVENTO REGIONAL” | Eliminado | — |
| Fechas/horario/lugar fake | Eliminados (`DetailInfoItem`) | — |
| Cuerpo de noticia inventado | Eliminado | `news_detail_unavailable_message` |
| Botón “Ver ubicación en el mapa” | Eliminado | `SancarlinaPrimaryButton` “Volver” |

---

## 4. Estado runtime

| Verificación | Estado |
|--------------|--------|
| No hay noticia inventada | **Confirmado** |
| No hay imagen demo | **Confirmado** |
| No hay fecha/autor fake | **Confirmado** |
| Empty state honesto | **Confirmado** — “Novedad no disponible” + mensaje próximamente |
| Botón volver funcional | **Confirmado** — `onBack` en top bar y CTA |

**Diseño Stitch aplicado:** `SancarlinaBackground`, `SancarlinaTopBar`, `SancarlinaCard`, icono newspaper olive, `SancarlinaPrimaryButton`.

---

## 5. Navegación

| Verificación | Estado |
|--------------|--------|
| Home puede navegar sin crash | **Confirmado** — `onNavigateToNews` → `Screen.NewsDetail.route` sin cambios |
| NavGraph sin cambios | **Confirmado** — ruta `news_detail` intacta; `onNavigateToMap` conservado en firma (no usado) |
| Ruta NewsDetail no rompe | **Confirmado** — pantalla muestra empty en lugar de demo |

### Integración futura (documentado, no implementado)

- `HomeViewModel` ya carga `BannerItem` desde Firestore (`title`, `subtitle`, `imageUrl`).
- `BannerItem` no tiene `id` ni cuerpo de artículo; `Home` navega a detalle sin argumentos.
- **Implementación posterior posible:** ruta `news_detail/{newsId}` + colección Firestore `news` con body, o pasar `BannerItem` serializado como nav args cuando exista contenido real.

---

## 6. Otras pantallas (solo documentación)

| Archivo | Estado | Acción |
|---------|--------|--------|
| [`SearchContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/home/SearchContent.kt) | Stitch parcial; categorías sugeridas como labels UI | **No tocado** — sin demo de noticia/comercio inventado en release |
| [`UpdatesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/updates/UpdatesContent.kt) | No referenciado en `NavGraph.kt` | **No tocado** — código no alcanzable en runtime actual |

---

## 7. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **OK** | BUILD SUCCESSFUL |
| `.\gradlew.bat :app:lintDebug` | **OK** | Sin errores bloqueantes |
| `.\gradlew.bat :app:testDebugUnitTest` | **OK** | Tests pasaron |

---

## 8. Confirmación de alcance

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
| No mocks visibles en release (NewsDetail) | **Sí** |
| No commit | **Sí** |

---

## 9. Próximo paso recomendado

1. Generar APK debug: `.\gradlew.bat :app:assembleDebug`
2. Instalar en celular: `app/build/outputs/apk/debug/app-debug.apk`
3. Probar: Home → “Ver todo” (novedades) → debe mostrar empty “Novedad no disponible” + Volver
4. Continuar prueba visual general UI-STITCH en dispositivo físico

---

*Título de commit sugerido (no ejecutado):* **“Quitar noticia de ejemplo y mostrar aviso cuando no hay novedad disponible”**
