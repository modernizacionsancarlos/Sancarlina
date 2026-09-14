# PHASE 2B-3.2 — Firestore Rules Deploy Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Firebase project:** `sancarlina-99748`  
**Ejecutor:** Modo Agente  
**Firebase CLI:** 15.18.0  
**Cuenta:** zorrofranco87@gmail.com

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Deploy ejecutado?** | **Sí** |
| **Proyecto destino** | `sancarlina-99748` |
| **Archivo desplegado** | [`firestore.rules`](firestore.rules) (repo local, 101 líneas) |
| **Resultado** | `+ firestore: released rules firestore.rules to cloud.firestore` — **Deploy complete!** |
| **Riesgo** | **Alto operativo** — las reglas previas en consola eran de **otra aplicación** (foro: `users`, `threads`, `communities`, etc.). El deploy las **reemplazó** por reglas GondolApp (`userProfiles`, `tenants`, `Submissions`, etc.). Rollback disponible vía backup. Web admin debe validar claim `role == admin`. |

---

## 2. Backup

| Criterio | Resultado |
|----------|-----------|
| **¿Backup automático creado?** | **Sí** |
| **Archivo** | [`firestore.rules.console-backup.2026-06-12.rules`](firestore.rules.console-backup.2026-06-12.rules) (~16 458 bytes) |
| **Método** | Firebase Rules API vía `firebase-tools` (`listAllReleases` + `getRulesetContent`) |
| **Instrucciones rollback** | [`FIRESTORE_RULES_CONSOLE_BACKUP_INSTRUCTIONS.md`](FIRESTORE_RULES_CONSOLE_BACKUP_INSTRUCTIONS.md) |

**Hallazgo crítico del backup:** las rules activas antes del deploy **no eran** reglas Sancarlina/GondolApp; incluían colecciones de foro escolar (`threads`, `comments`, `communities`, `schools`, etc.). El backup preserva ese estado para rollback.

---

## 3. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `git status --short` | OK (vacío al inicio) | Sin cambios Kotlin |
| `firebase.cmd --version` | `15.18.0` | OK |
| `firebase.cmd login:list` | `zorrofranco87@gmail.com` | OK |
| `firebase.cmd projects:list` | `sancarlina-99748 (current)` | OK |
| `firebase.cmd use` | `sancarlina-99748` | OK |
| Backup API (node + requireAuth) | OK | Escrito `firestore.rules.console-backup.2026-06-12.rules` |
| `firebase deploy --only firestore:rules --project sancarlina-99748 --dry-run` | OK | `rules file firestore.rules compiled successfully` |
| `firebase deploy --only firestore:rules --project sancarlina-99748` | **OK** | `released rules firestore.rules to cloud.firestore` |

**No ejecutado (prohibido):**

- `firebase deploy` (completo)
- `firebase deploy --only storage`
- Cualquier deploy de Functions

---

## 4. Confirmación de alcance

| Área | ¿Tocado? |
|------|----------|
| Storage deploy | **No** |
| Functions | **No** |
| Kotlin `app/src/main` | **No** |
| Gradle | **No** |
| AAB / keystore / firma | **No** |
| assets Stitch | **No** |
| Commit | **No** |

**Archivos nuevos locales (sin commit):**

- `firestore.rules.console-backup.2026-06-12.rules`
- `FIRESTORE_RULES_CONSOLE_BACKUP_INSTRUCTIONS.md`
- `PHASE2B3_2_FIRESTORE_RULES_DEPLOY_REPORT.md`

---

## 5. Pruebas posteriores obligatorias

Ejecutar en [Firebase Console → Firestore → Rules → Simulator](https://console.firebase.google.com/project/sancarlina-99748/firestore/rules):

| # | Operación | Auth | Resultado esperado |
|---|-----------|------|------------------|
| 1 | `get /databases/(default)/documents/tenants/test` | Sin auth | **Permitido** |
| 2 | `get /databases/(default)/documents/products/test` | Sin auth | **Permitido** |
| 3 | `get /databases/(default)/documents/userProfiles/{uid}` | Auth uid = `{uid}` | **Permitido** |
| 4 | `update /databases/(default)/documents/userProfiles/{uid}` cambiando `points` o `role` | Auth owner | **Denegado** |
| 5 | `create /databases/(default)/documents/Submissions/x` con `created_by == uid` | Auth | **Permitido** |
| 6 | `get /databases/(default)/documents/Submissions/x` | Auth ciudadano (sin admin) | **Denegado** |

**Prueba adicional recomendada:** registro nuevo → `set` merge en `userProfiles/{uid}` con `role=citizen`, `points=0` (`UserRepository.kt:22-31`).

---

## 6. Rollback

### Opción A — Consola (rápida)

1. Abrir [Firestore Rules](https://console.firebase.google.com/project/sancarlina-99748/firestore/rules)
2. Pegar contenido de [`firestore.rules.console-backup.2026-06-12.rules`](firestore.rules.console-backup.2026-06-12.rules)
3. **Publish**

### Opción B — CLI

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
# Copiar backup sobre firestore.rules temporalmente, o ajustar firebase.json
& "$env:APPDATA\npm\firebase.cmd" deploy --only firestore:rules --project sancarlina-99748
# Restaurar firestore.rules GondolApp local después
```

---

## 7. Resumen pre-deploy (ejecutado)

| Campo | Valor |
|-------|-------|
| Proyecto destino | `sancarlina-99748` |
| Archivo | `firestore.rules` |
| Comando | `firebase deploy --only firestore:rules --project sancarlina-99748` |
| Storage | NO desplegado |
| Functions | NO desplegado |

---

## 8. Salida exacta del deploy

```
=== Deploying to 'sancarlina-99748'...

i  deploying firestore
i  firestore: ensuring required API firestore.googleapis.com is enabled...
i  firestore: ensuring required API firestore.googleapis.com is enabled...
i  cloud.firestore: checking firestore.rules for compilation errors...
+  cloud.firestore: rules file firestore.rules compiled successfully
i  firestore: uploading rules firestore.rules...
+  firestore: released rules firestore.rules to cloud.firestore

+  Deploy complete!

Project Console: https://console.firebase.google.com/project/sancarlina-99748/overview
```
