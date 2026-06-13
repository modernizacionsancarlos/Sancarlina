# PHASE 2B-3.1 — Firebase Rules Draft (Implementation Report)

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Firebase project:** `sancarlina-99748`  
**Alcance:** Solo draft local — **Fase 2B-3.1**

---

## Advertencias obligatorias

| Aviso | Detalle |
|-------|---------|
| **NO DEPLOY** | No se ejecutó `firebase deploy`. Estos archivos **no están activos** en producción. |
| **Reglas son borrador** | `firestore.rules` y `storage.rules` requieren revisión y prueba antes de cualquier uso. |
| **Rules Simulator pendiente** | Validar en Firebase Console → Firestore → Rules → Simulator (**Fase 2B-3.2**, no aprobada aún). |
| **Consola no verificable desde repo** | El estado real de rules/functions en Firebase Console **no pudo confirmarse** desde este repositorio. |
| **`awardPoints` no verificable desde repo** | Cliente invoca callable en `PointsRepository.kt:19-21`; **no existe** carpeta `functions/` en repo. |
| **Delete account robusto** | Sin cambios en cliente; estrategia server-side queda para **fase posterior** (2B-3.3 / 2B-3.4). |

---

## 1. Archivos creados

| Archivo | Descripción |
|---------|-------------|
| [`firestore.rules`](firestore.rules) | Borrador Firestore Security Rules |
| [`storage.rules`](storage.rules) | Borrador Storage Security Rules |
| [`firebase.json`](firebase.json) | Configuración paths rules (sin hosting/functions) |
| [`.firebaserc`](.firebaserc) | Proyecto default `sancarlina-99748` |

**No creados (fuera de alcance 2B-3.1):** `functions/`, `PHASE2B3_PLAN_REPORT.md`, cambios en `app/src/main`.

---

## 2. Operaciones cliente cubiertas por el draft

| Operación | Colección | Regla draft | Evidencia código |
|-----------|-----------|-------------|------------------|
| create/merge perfil (registro) | `userProfiles` | create owner + `role==citizen` + puntos 0 | [`UserRepository.kt:22-31`](app/src/main/java/com/sancarlina/app/data/repository/UserRepository.kt) |
| read perfil propio | `userProfiles` | read owner | [`EditProfileViewModel.kt:35`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileViewModel.kt), [`ProfileViewModel.kt:33`](app/src/main/java/com/sancarlina/app/viewmodel/ProfileViewModel.kt) |
| update perfil (nombre, tel, location) | `userProfiles` | update owner sin `role`/`points`/`points_balance` | [`EditProfileViewModel.kt:72-80`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileViewModel.kt) |
| delete perfil (baja cuenta) | `userProfiles` | delete owner | [`EditProfileViewModel.kt:149`](app/src/main/java/com/sancarlina/app/ui/features/profile/EditProfileViewModel.kt) |
| create submission | `Submissions` | create auth + `created_by == uid` | [`SubmissionsRepository.kt:22-23`](app/src/main/java/com/sancarlina/app/data/repository/SubmissionsRepository.kt) |
| read tenants | `tenants` | read público | [`TenantsRepository.kt:18`](app/src/main/java/com/sancarlina/app/data/repository/TenantsRepository.kt) |
| read areas | `areas` | read público | [`AreasRepository.kt:22`](app/src/main/java/com/sancarlina/app/data/repository/AreasRepository.kt) |
| read benefits | `benefits` | read público | [`BenefitsRepository.kt:23`](app/src/main/java/com/sancarlina/app/data/repository/BenefitsRepository.kt) |
| read FormSchemas | `FormSchemas` | read público | [`FormsRepository.kt:17-24`](app/src/main/java/com/sancarlina/app/data/repository/FormsRepository.kt) |
| read banners/categories/products/commerces | varias | read público; write admin | [`HomeViewModel.kt:76,89,101`](app/src/main/java/com/sancarlina/app/viewmodel/HomeViewModel.kt), [`SearchViewModel.kt:50,66`](app/src/main/java/com/sancarlina/app/ui/features/home/SearchViewModel.kt) |
| award puntos (runtime) | — | **Cloud Function** (bypass rules vía Admin SDK) | [`PointsRepository.kt:19-21`](app/src/main/java/com/sancarlina/app/data/repository/PointsRepository.kt) |

### Escrituras bloqueadas para cliente (intencional)

| Escritura | Motivo |
|-----------|--------|
| `points` / `points_balance` / `role` en update | SEC-02 — manipulación de saldo/rol |
| `categories` / `commerces` via `seedFirestore` | Solo admin; `HomeViewModel.kt:128-146` sin callers en UI |
| `AuditLogs`, `superAdmins` | Admin-only |
| Catch-all `/{document=**}` | Deny por defecto |

---

## 3. Resumen `firestore.rules`

- Helpers: `isSignedIn`, `isOwner`, `isAdmin` (claim `role == 'admin'` — **validar contra web admin**)
- `userProfiles`: CRUD owner con restricciones en create/update
- `Submissions`: create ciudadano; read/update/delete admin
- Catálogo público: `tenants`, `areas`, `benefits`, `FormSchemas`, `banners`, `categories`, `commerces`, `products`
- Admin: `AuditLogs`, `Notifications`, `superAdmins`
- Fallback deny all

---

## 4. Resumen `storage.rules`

- Lectura pública (logos/banners web)
- Escritura solo `request.auth.token.role == 'admin'`
- App Android **no usa** Firebase Storage en `app/src/main` (confirmado en auditoría 2B-3)

---

## 5. Fuera de alcance (confirmado)

| Ítem | ¿Tocado? |
|------|----------|
| `firebase deploy` | **No** |
| `functions/` / Cloud Functions | **No** |
| `awardPoints` / `deleteUserAccount` implementación | **No** |
| `app/src/main` Kotlin | **No** |
| versionCode / versionName | **No** |
| keystore / AAB / firma | **No** |
| assets Stitch | **No** |
| 2B-3.2 Rules Simulator | **No** |
| 2B-3.3 delete account changes | **No** |
| 2B-3.4 functions/backend | **No** |

---

## 6. Delete account — estado actual (sin cambios)

Documentado para fase posterior:

1. Reauth email/password — `EditProfileViewModel.kt:146-147`
2. Firestore delete — `EditProfileViewModel.kt:149`
3. Auth delete — `EditProfileViewModel.kt:150`
4. Riesgo residual: Auth huérfano si falla paso 3 tras borrar Firestore

---

## 7. Próximos pasos (requieren aprobación separada)

| Fase | Acción |
|------|--------|
| **2B-3.2** | Exportar rules consola + Rules Simulator con casos reales |
| **2B-3.3** | Estrategia delete account (sin cambiar código hasta aprobar) |
| **2B-3.4** | `functions/` + verificar `awardPoints` + callable delete |

---

## 8. Validación local

Comando esperado post-implementación:

```bash
git status --short
git diff --stat
```

**Resultado esperado:** solo archivos Firebase + este reporte en el diff.

**Deploy (PROHIBIDO en 2B-3.1):**

```bash
# NO EJECUTAR hasta aprobar 2B-3.2+
# firebase deploy --only firestore:rules,storage
```
