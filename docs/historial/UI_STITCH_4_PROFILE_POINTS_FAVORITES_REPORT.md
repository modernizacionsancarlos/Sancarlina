# UI-STITCH-4 — Profile, Points and Favorites UI Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Fase:** UI-STITCH-4 — Perfil / puntos / favoritos

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Perfil rework aplicado?** | **Sí** |
| **¿Editar perfil rework aplicado?** | **Sí** |
| **¿Puntos/beneficios rework aplicado?** | **Sí** |
| **¿Historial puntos rework aplicado?** | **Sí** |
| **¿Favoritos rework aplicado?** | **Sí** |
| **¿QR rework aplicado?** | **Sí** |
| **¿Build OK?** | **Sí** |
| **¿Lint OK?** | **Sí** |
| **¿Unit tests OK?** | **Sí** |
| **Riesgo** | **Bajo** — UI solamente; delete account y cámara intactos |

---

## 2. Archivos modificados

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| [`ProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/ProfileContent.kt) | Hero card + action cards Stitch | Perfil usuario |
| [`EditProfileContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileContent.kt) | Top bar, avatar iniciales, `SancarlinaTextField`, sin placeholder URL | Editar perfil |
| [`BenefitsContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/BenefitsContent.kt) | Balance card, QR CTA, empty beneficios | Panel puntos |
| [`PointsHistoryContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/PointsHistoryContent.kt) | Top bar + empty Stitch | Historial |
| [`FavoritesContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/favorites/FavoritesContent.kt) | Top bar + empty card | Favoritos |
| [`QrScannerContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/points/QrScannerContent.kt) | Permiso en card, marco olive, CTA Stitch | Escáner QR |
| [`strings.xml`](app/src/main/res/values/strings.xml) | Textos perfil/puntos/favoritos/QR | Copy centralizado |
| **Nuevos** `profile/components/*` (3) | Hero, actions, avatar | Modularización |
| **Nuevos** `points/components/*` (3) | Balance, QR, history empty | Modularización |
| **Nuevos** `favorites/components/*` (1) | Empty state | Modularización |

---

## 3. Diseño Stitch aplicado

| Pantalla | Referencia Stitch | Implementación Compose | Estado |
|----------|-------------------|------------------------|--------|
| Perfil | `perfil_de_usuario/` | `ProfileHeroCard`, `ProfileActionCard` | Aplicado |
| Editar perfil | `editar_perfil/` | `EditProfileAvatarSection`, `SancarlinaTextField` | Aplicado |
| Panel puntos | `panel_de_puntos/` | `PointsBalanceCard`, `QrActionCard` | Aplicado |
| Historial | `historial_de_puntos/` | `PointsHistoryEmptyState`, `MovementCard` 24dp | Aplicado |
| Favoritos | `favoritos/` | `FavoritesEmptyState`, `SancarlinaTopBar` | Aplicado |
| QR | `esc_ner_qr/` | Marco 28dp olive, card permiso, `SancarlinaPrimaryButton` | Aplicado |

---

## 4. Componentes creados/reutilizados

| Componente | Archivo | Uso | Estado |
|------------|---------|-----|--------|
| `ProfileHeroCard` | `profile/components/ProfileHeroCard.kt` | Header perfil | Nuevo |
| `ProfileActionCard` | `profile/components/ProfileActionCard.kt` | Menú acciones 24dp | Nuevo |
| `EditProfileAvatarSection` | `profile/components/EditProfileAvatarSection.kt` | Avatar/iniciales | Nuevo |
| `PointsBalanceCard` | `points/components/PointsBalanceCard.kt` | Saldo puntos | Nuevo |
| `QrActionCard` | `points/components/QrActionCard.kt` | CTA escanear | Nuevo |
| `PointsHistoryEmptyState` | `points/components/PointsHistoryEmptyState.kt` | Historial vacío | Nuevo |
| `FavoritesEmptyState` | `favorites/components/FavoritesEmptyState.kt` | Favoritos vacío | Nuevo |
| `SancarlinaTopBar` | `ui/components/SancarlinaTopBar.kt` | Historial, favoritos, editar | Reutilizado |
| `SancarlinaTextField` | `ui/components/SancarlinaTextField.kt` | Form editar perfil | Reutilizado |
| `SancarlinaPrimaryButton` / `SecondaryButton` | `SancarlinaButtons.kt` | Guardar, logout, QR | Reutilizado |
| `SancarlinaCard` | `SancarlinaCard.kt` | Empty states | Reutilizado |

---

## 5. Datos y estados

| Pantalla/sección | Fuente de datos | Estado loading | Estado vacío/error | ¿Mock visible en release? |
|------------------|-----------------|----------------|---------------------|---------------------------|
| Perfil | `ProfileViewModel` / Auth | `userName` default "Cargando..." | Email vacío → mensaje login | **No** |
| Editar perfil | `EditProfileViewModel` | Spinner | Errores en banner | **No** — sin `via.placeholder.com` |
| Saldo puntos | `PointsViewModel` / Firestore | Spinner beneficios | Balance 0 si sin usuario | **No** |
| Beneficios | `benefitsRepository` | Spinner | `points_benefits_empty` | **No** |
| Historial | Lista vacía fija (backend pendiente) | N/A | `PointsHistoryEmptyState` | **No** |
| Favoritos | `emptyList()` (integración pendiente) | N/A | `FavoritesEmptyState` | **No** |
| QR éxito | `uiState.successPoints` real | Overlay loading | N/A | **No** — puntos reales del scan |

---

## 6. Seguridad / lógica sensible

| Verificación | Estado |
|--------------|--------|
| Delete account sigue usando reauth (diálogo contraseña) | **Intacto** |
| No se cambió lógica Firebase Auth | **Confirmado** |
| No se cambió lógica de cámara / ML Kit | **Confirmado** |
| No se cambió lógica de puntos (`PointsViewModel`) | **Confirmado** |
| No se inventaron movimientos/favoritos/beneficios | **Confirmado** |

---

## 7. Navegación

| Verificación | Estado |
|--------------|--------|
| Rutas `NavGraph` sin cambios | **Confirmado** |
| Perfil → editar perfil / favoritos / historial | **Intacto** |
| Puntos → QR scanner | **Intacto** |
| Favoritos → detalle (cuando haya datos) | **Intacto** |
| Bottom nav | **Sin cambios** |
| Offline route | **Sin cambios** |

---

## 8. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `.\gradlew.bat :app:assembleDebug` | **BUILD SUCCESSFUL** | ~50s |
| `.\gradlew.bat :app:lintDebug` | **BUILD SUCCESSFUL** | 0 errores |
| `.\gradlew.bat :app:testDebugUnitTest` | **BUILD SUCCESSFUL** | Sin fallos |

---

## 9. Confirmación de alcance

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
| No NavGraph modificado | Sí |
| No ViewModels modificados | Sí |

---

## 10. Próximo paso recomendado

### UI-STITCH-5 — Turismo / mapa / QR final

1. [`TurismoContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/turismo/TurismoContent.kt)
2. [`MapContent.kt`](app/src/main/java/com/sancarlina/app/ui/features/map/MapContent.kt)
3. Pulido final QR si hace falta tras prueba en dispositivo

### Hotfix

No requerido — build/lint/tests OK.

---

*Validación Gradle completada. Sin commit.*
