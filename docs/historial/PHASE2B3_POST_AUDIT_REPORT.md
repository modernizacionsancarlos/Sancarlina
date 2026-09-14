# PHASE 2B-3.1 — Post Audit Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Commit auditado:** `f7474ed` — *Agregar borrador local de reglas Firebase (sin deploy)*  
**Modo:** Solo lectura — sin modificaciones de código Kotlin ni deploy.

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Draft aceptable?** | **Sí** — borrador coherente con operaciones cliente Android confirmadas en código |
| **¿Se puede cerrar 2B-3.1?** | **Sí** — entregables completos; validación en Simulator queda para **2B-3.2** |
| **Bloqueadores** | Ninguno en el draft local; **no desplegar** sin Simulator (2B-3.2) |
| **Riesgos** | (1) `isAdmin()` depende de custom claim `role == 'admin'` — **no verificable desde repo** (`firestore.rules:19-20`). (2) `userProfiles` usa `set(merge)` no `create` explícito — Firestore mapea a create/update según existencia del doc (`UserRepository.kt:31`). (3) Web admin no tiene regla explícita de write en `userProfiles` (solo Admin SDK / CF). (4) `Notifications` read para cualquier auth (`firestore.rules:87-88`) — app Android aún mock (`NotificationsViewModel.kt:21`). (5) Reglas prod en consola **desconocidas** desde repo. |

---

## 2. Diff auditado

**Comandos ejecutados:**

```
git status --short   → (vacío — working tree clean)
git diff --stat      → (vacío — sin cambios sin commitear)
```

**Commit `f7474ed` — archivos incluidos (5 archivos, +263 líneas):**

| Archivo | Estado | Observación |
|---------|--------|-------------|
| `firestore.rules` | Creado (+100 líneas) | Borrador Firestore; comentarios DRAFT — NO DEPLOY (`firestore.rules:1-4`) |
| `storage.rules` | Creado (+14 líneas) | Borrador Storage (`storage.rules:1-4`) |
| `firebase.json` | Creado (+8 líneas) | Solo paths firestore + storage |
| `.firebaserc` | Creado (+5 líneas) | Proyecto `sancarlina-99748` |
| `PHASE2B3_IMPLEMENTATION_REPORT.md` | Creado (+136 líneas) | Reporte implementación 2B-3.1 |

**Confirmación alcance diff:** Solo los 5 archivos anteriores. **No** aparecen `app/src/main`, `functions/`, Gradle, keystore ni Stitch.

---

## 3. firestore.rules

| Bloque | Estado | Riesgo | Recomendación |
|--------|--------|--------|---------------|
| Sintaxis `rules_version = '2'` | OK | Bajo | Mantener |
| Helpers `isSignedIn` / `isOwner` | OK (`firestore.rules:10-16`) | Bajo | Mantener |
| Helper `isAdmin` | OK sintaxis; claim **no verificado** (`firestore.rules:19-20`) | **Medio** | Validar en consola cómo web admin obtiene `role`; alternativa doc `superAdmins` |
| **userProfiles read** | OK owner (`firestore.rules:25`) | Bajo | Coincide `EditProfileViewModel.kt:35`, `ProfileViewModel.kt:33` |
| **userProfiles create** | OK condiciones vs registro (`firestore.rules:28-31`) | **Medio** | `UserRepository.kt:14-27` envía `role=citizen`, `points=0`, `points_balance=0` solo si doc no existe; probar en Simulator caso `set(merge)` doc nuevo |
| **userProfiles update** | OK bloqueo `role`/`points`/`points_balance`/`uid` (`firestore.rules:34-36`) | Bajo | `EditProfileViewModel.kt:72-76` solo `user_name`, `phone`, `location` — permitido |
| **userProfiles delete** | OK owner (`firestore.rules:39`) | Bajo | `EditProfileViewModel.kt:149` |
| **Submissions create** | OK auth + `created_by` (`firestore.rules:44-45`) | Bajo | Campo **`created_by` confirmado** en payload (`SubmissionsRepository.kt:17`) |
| **Submissions read/update/delete** | OK admin-only (`firestore.rules:46`) | Bajo | No público |
| Catálogo read público | OK (`firestore.rules:50-81`) | Bajo | Cubre lecturas Android |
| Catálogo write admin | OK (`firestore.rules:52,56,...`) | Bajo | Bloquea `seedFirestore` cliente (`HomeViewModel.kt:134,146`) |
| **AuditLogs** | OK admin-only (`firestore.rules:84-86`) | Bajo | App no escribe; constante `FirestoreCollections.kt:8` |
| **Notifications** | read auth / write admin (`firestore.rules:87-89`) | Bajo | App usa mock, no Firestore aún |
| **superAdmins** | OK admin-only (`firestore.rules:91-93`) | Medio | Bootstrap admin requiere claim fuera de rules |
| Catch-all deny | OK (`firestore.rules:96-98`) | Bajo | Deniega colecciones no listadas |

### Hallazgos cruzados userProfiles (detalle)

| Regla | Código | ¿Coincide? |
|-------|--------|------------|
| create `role == 'citizen'` | `UserRepository.kt:14` | **Sí** |
| create `points == 0` | `UserRepository.kt:26` (solo doc nuevo) | **Sí** |
| create `points_balance == 0` | `UserRepository.kt:27` | **Sí** |
| update permite `user_name`, `phone`, `location` | `EditProfileViewModel.kt:72-76` | **Sí** — no están en lista bloqueada |
| Operación real registro | `UserRepository.kt:31` `set(..., SetOptions.merge())` | **Nota:** en doc inexistente = operación **create** en rules; validar en Simulator |

### Hallazgos cruzados Submissions (detalle)

| Campo rules | Campo código | Línea |
|-------------|--------------|-------|
| `created_by == auth.uid` | `put("created_by", uid)` | `SubmissionsRepository.kt:17` |
| Extra `form_id`, `created_at` | `SubmissionsRepository.kt:16-18` | Permitidos (rules no restringen campos extra) |
| Emprendimiento payload | `EmprendimientoViewModel.kt:57-64` | Compatible con create rule |

---

## 4. storage.rules

| Bloque | Estado | Riesgo | Recomendación |
|--------|--------|--------|---------------|
| App Android usa Storage | **No** — grep sin `FirebaseStorage` en `app/src/main` | N/A | Correcto para V59 Android |
| read público | OK (`storage.rules:10`) | Bajo | Razonable logos/banners web (`firebase_security_audit.txt:27-28`) |
| write admin claim | OK (`storage.rules:11`) | Medio | Mismo riesgo claim `role` no verificado |
| Bucket | No referenciado en rules | Info | Bucket en `app/google-services.json:5` — deploy Storage usa proyecto Firebase |

---

## 5. firebase.json / .firebaserc

| Ítem | Valor | Estado |
|------|-------|--------|
| Project ID | `sancarlina-99748` | OK — `.firebaserc:3`, coincide `app/google-services.json:4` |
| Firestore rules path | `firestore.rules` | OK — `firebase.json:2-4` |
| Storage rules path | `storage.rules` | OK — `firebase.json:5-7` |
| hosting | Ausente | OK — sin hosting |
| functions | Ausente | OK — sin functions en config |

---

## 6. Riesgos antes de deploy

Validar en **Firebase Rules Simulator** (Fase 2B-3.2 — **no ejecutar deploy directo**):

| # | Caso de prueba | Motivo |
|---|----------------|--------|
| 1 | Registro nuevo → `userProfiles/{uid}` `set(merge)` doc inexistente | Confirmar que rules tratan como **create** y pasan con `role`, `points`, `points_balance` |
| 2 | Update perfil `user_name`, `phone`, `location` | `EditProfileViewModel.kt:80` |
| 3 | Update intentando cambiar `points` o `role` | Debe **denegar** (SEC-02) |
| 4 | Delete `userProfiles` owner | `EditProfileViewModel.kt:149` |
| 5 | Submissions `.add()` con `created_by == uid` | `SubmissionsRepository.kt:17,23` |
| 6 | Submissions read sin admin | Debe **denegar** |
| 7 | Read público `tenants`, `products`, `banners` | Home/Search flows |
| 8 | Write cliente en `categories` / `commerces` | Debe **denegar** (`HomeViewModel.kt:134,146`) |
| 9 | Admin write catálogo con token `role=admin` | Verificar claim real en consola |
| 10 | `awardPoints` Cloud Function | **No verificable en repo** — confirmar en consola que CF usa Admin SDK |
| 11 | Comparar draft vs rules **actuales en consola** | Estado prod no verificable desde repo |

---

## 7. Cambios fuera de alcance

| Área | ¿Modificado en f7474ed? |
|------|-------------------------|
| Kotlin `app/src/main` | **No** |
| `functions/` | **No** |
| `firebase deploy` | **No** |
| Gradle / `build.gradle.kts` | **No** |
| versionCode / versionName | **No** |
| keystore / AAB / firma | **No** |
| assets Stitch | **No** |

---

## 8. Próximo paso recomendado

**Cerrar 2B-3.1** — draft local completo y alineado con cliente Android.

**Siguiente fase (aprobación separada):**

- **2B-3.2** — Validación manual en Firebase Rules Simulator con casos de la sección 6; exportar baseline de rules actuales en consola; **NO deploy directo**.
- **2B-3.3 / 2B-3.4** — Delete account robusto y `functions/` — no aprobadas.

**Si el Simulator detecta fallos en registro o Submissions**, usar prompt Modo Agente para corregir **solo** `firestore.rules` / `storage.rules` local (sin deploy, sin Kotlin).

---

## Anexo — Referencias código clave

| Operación | Archivo:línea |
|-----------|---------------|
| Merge perfil registro | `UserRepository.kt:11-31` |
| Update perfil | `EditProfileViewModel.kt:72-80` |
| Delete perfil | `EditProfileViewModel.kt:149` |
| Submission create | `SubmissionsRepository.kt:15-23` |
| Award puntos (CF) | `PointsRepository.kt:19-21` |
| Constantes colecciones | `FirestoreCollections.kt:4-14` |
