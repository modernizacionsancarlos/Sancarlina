# PHASE 2B-3.3 — Firestore Rules Hotfix Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Firebase project:** `sancarlina-99748`  
**Firebase CLI:** 15.18.0

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Hotfix aplicado?** | **Sí** |
| **¿Deploy ejecutado?** | **Sí** |
| **Proyecto destino** | `sancarlina-99748` |
| **Resultado CLI** | `+ firestore: released rules firestore.rules to cloud.firestore` — **Deploy complete!** |

---

## 2. Problema corregido

| Campo | Detalle |
|-------|---------|
| **Error original** | `Property role is undefined on object.` (Firebase Rules Simulator) |
| **Causa** | Acceso directo a `request.auth.token.role` cuando el usuario autenticado **no tiene** custom claim `role`; la evaluación falla en lugar de devolver denegación |
| **Archivo/línea antes** | [`firestore.rules:19-20`](firestore.rules) — `return isSignedIn() && request.auth.token.role == 'admin';` |
| **Archivo/línea después** | [`firestore.rules:15-28`](firestore.rules) — helpers `authRole()`, `hasRole()`, `isAdmin()` con `request.auth.token.get('role', '')` |

**Decisión sobre `estado`:** el backup foro previo usaba `role in ['admin', 'estado']`. El draft GondolApp solo reconoce **`admin`** como rol administrativo. **`estado` no se añadió** para no ampliar permisos sin validar el panel web; si el admin municipal usa claim `estado`, habrá que añadir `hasRole('estado')` en `isAdmin()` tras confirmación.

---

## 3. Cambios en firestore.rules

| Helper / bloque | Cambio | Motivo | Riesgo |
|-----------------|--------|--------|--------|
| `authRole()` | Nuevo — `request.auth.token.get('role', '')` si signed in | Evitar error cuando claim ausente | Bajo |
| `hasRole(r)` | Nuevo — compara `authRole() == r` | Reutilizable y seguro | Bajo |
| `isAdmin()` | Usa `hasRole('admin')` en lugar de acceso directo | Simulator: sin role → denegado, no error | Bajo |
| Resto de matches | Sin cambio | Comportamiento catálogo/userProfiles/Submissions intacto | Bajo |
| Comentario cabecera | Actualizado a Fase 2B-3.3 | Trazabilidad | N/A |

---

## 4. Validación

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `firebase deploy --only firestore:rules --project sancarlina-99748 --dry-run` | **OK** | `rules file firestore.rules compiled successfully` |
| `firebase deploy --only firestore:rules --project sancarlina-99748` | **OK** | Rules publicadas en `cloud.firestore` |

---

## 5. Confirmación de alcance

| Área | ¿Tocado? |
|------|----------|
| Kotlin `app/src/main` | **No** |
| Storage deploy | **No** |
| `storage.rules` | **No** |
| Functions | **No** |
| Gradle | **No** |
| versionCode / versionName | **No** |
| AAB / keystore | **No** |
| assets Stitch | **No** |
| Commit | **No** |

**Archivos modificados:** solo [`firestore.rules`](firestore.rules) + este reporte.

---

## 6. Tests manuales posteriores en Firebase Simulator

[Firestore Rules Simulator](https://console.firebase.google.com/project/sancarlina-99748/firestore/rules)

| # | Caso | Resultado esperado |
|---|------|-------------------|
| 1 | `get /databases/(default)/documents/tenants/test` sin auth | **Permitido** |
| 2 | `get /databases/(default)/documents/products/test` sin auth | **Permitido** |
| 3 | `get /databases/(default)/documents/userProfiles/test_uid_123` auth UID `test_uid_123` | **Permitido** |
| 4 | `get /databases/(default)/documents/userProfiles/otro_uid_999` auth UID `test_uid_123` | **Denegado** |
| 5 | `get /databases/(default)/documents/Submissions/test_submission_1` auth UID `test_uid_123` sin role | **Denegado, sin error** |
| 6 | `create /databases/(default)/documents/Submissions/test_submission_1` auth UID `test_uid_123`, `created_by == test_uid_123` | **Permitido** |
| 7 | `create /databases/(default)/documents/categories/test` auth UID `test_uid_123` sin role | **Denegado, sin error** |
| 8 | admin write catálogo con custom claim `role == admin` | **Permitido** (configurar claim en Simulator) |

---

## 7. Rollback

Backup pre-deploy GondolApp (reglas foro, distintas):

- [`firestore.rules.console-backup.2026-06-12.rules`](firestore.rules.console-backup.2026-06-12.rules)
- Instrucciones: [`FIRESTORE_RULES_CONSOLE_BACKUP_INSTRUCTIONS.md`](FIRESTORE_RULES_CONSOLE_BACKUP_INSTRUCTIONS.md)

Para revertir solo este hotfix: restaurar versión anterior de `isAdmin()` **no** es deseable (reintroduce el bug). Rollback completo = pegar backup en consola o redeploy del archivo backup.

---

## 8. Salida exacta del deploy

```
=== Deploying to 'sancarlina-99748'...

i  deploying firestore
i  firestore: ensuring required API firestore.googleapis.com is enabled...
i  cloud.firestore: checking firestore.rules for compilation errors...
+  cloud.firestore: rules file firestore.rules compiled successfully
i  firestore: uploading rules firestore.rules...
+  firestore: released rules firestore.rules to cloud.firestore

+  Deploy complete!
```
