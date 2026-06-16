# PHASE 2B-3.4 — Firestore Rules Automated Tests Report

**Fecha:** 2026-06-12  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Firebase project:** `sancarlina-99748`  
**Ejecutor:** Modo Agente (Cursor)

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Tests creados?** | **Sí** |
| **¿Tests ejecutados?** | **No** — abortados por diferencia reglas local vs activas |
| **¿Todos pasaron?** | **N/A** (no ejecutados) |
| **Cantidad total** | 15 (T1–T15 definidos) |
| **Pasados** | 0 |
| **Fallidos** | 0 |
| **Riesgo** | **Crítico** — Las reglas **activas en Firebase** (456 líneas, app foro con `request.auth.token.role` directo) **no coinciden** con [`firestore.rules`](firestore.rules) local (108 líneas, GondolApp post-hotfix 2B-3.3). El deploy documentado en 2B-3.2/2B-3.3 **no está reflejado** en el release activo consultado vía Rules API. |

---

## 2. Archivos creados/modificados

| Archivo | Acción | Motivo |
|---------|--------|--------|
| [`package.json`](package.json) | Creado | Scripts npm y devDependencies para tests |
| [`package-lock.json`](package-lock.json) | Creado | Lock de dependencias (`npm install`) |
| [`jest.config.js`](jest.config.js) | Creado | Configuración Jest |
| [`rules-tests/firestore.rules.test.js`](rules-tests/firestore.rules.test.js) | Creado | 15 tests obligatorios T1–T15 |
| [`scripts/compare-firestore-rules.js`](scripts/compare-firestore-rules.js) | Creado | Comparación local vs rules activas (Rules API) |
| [`firebase.json`](firebase.json) | Modificado | Bloque `emulators.firestore` puerto 8080 |
| [`.gitignore`](.gitignore) | Modificado | `node_modules/`, `coverage/` |
| [`PHASE2B3_4_RULES_AUTOTEST_REPORT.md`](PHASE2B3_4_RULES_AUTOTEST_REPORT.md) | Creado | Este reporte |

**No modificado:** `firestore.rules`, Kotlin, Gradle, Storage, Functions, versiones app.

---

## 3. Herramientas

| Herramienta | Versión |
|-------------|---------|
| Node | v22.22.1 |
| npm | 11.15.0 |
| Firebase CLI (global) | 15.18.0 |

**Dependencias npm instaladas (dev):**

| Paquete | Versión instalada |
|---------|-------------------|
| `@firebase/rules-unit-testing` | 4.0.1 |
| `firebase` | 11.10.0 |
| `firebase-tools` | 15.20.0 |
| `jest` | 29.7.0 |

**Preflight `git status --short` (inicio):** limpio.  
**Tras implementación:** `.gitignore`, `firebase.json` modificados; resto archivos nuevos sin commit.

---

## 4. Verificación reglas locales vs reglas activas

| Criterio | Resultado |
|----------|-----------|
| **¿Se pudo comparar con rules activas?** | **Sí** |
| **Resultado** | **DIFIEREN — tests abortados** |
| **Ruleset activo (API)** | `projects/sancarlina-99748/rulesets/194b6bf6-2a9d-41b5-8348-7b5f8e5feabc` |
| **Líneas local** | 108 |
| **Líneas remoto** | 456 |
| **Archivo remoto** | `firestore.rules` |

**Resumen de diferencias:**

- **Local:** reglas GondolApp (2B-3.3 hotfix) con `authRole()` → `request.auth.token.get('role', '')`, colecciones `userProfiles`, `Submissions`, `tenants`, `products`, etc.
- **Remoto activo:** reglas de **otra aplicación** (foro escolar): helpers `userRole()` con `request.auth.token.role` directo, `isModerator()`, colecciones `users`, `threads`, `communities`, etc. (coincide con [`firestore.rules.console-backup.2026-06-12.rules`](firestore.rules.console-backup.2026-06-12.rules)).

**Primeras líneas remotas (API):**

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // ─── Auth / roles ─────────────────────────────────────────
    function userRole() {
      return request.auth.token.role;
    ...
```

**Conclusión:** Las reglas desplegadas en 2B-3.2/2B-3.3 **no son** las que Firebase expone como release activo de `cloud.firestore` en el momento de esta verificación. Posibles causas: rollback manual, deploy a otro recurso/proyecto, o release no actualizado. **No se ejecutó** `firebase deploy` en esta fase.

---

## 5. Resultados por test

| ID | Caso | Resultado esperado | Resultado obtenido | Estado | Observación |
|----|------|-------------------|-------------------|--------|-------------|
| T1 | Catálogo público tenants | Permitido | — | **No ejecutado** | Abort por diff rules |
| T2 | Catálogo público products | Permitido | — | **No ejecutado** | Abort por diff rules |
| T3 | Leer perfil propio | Permitido | — | **No ejecutado** | Abort por diff rules |
| T4 | Leer perfil ajeno | Denegado | — | **No ejecutado** | Abort por diff rules |
| T5 | Crear perfil propio válido | Permitido | — | **No ejecutado** | Abort por diff rules |
| T6 | Actualizar perfil campos seguros | Permitido | — | **No ejecutado** | Abort por diff rules |
| T7 | Intentar cambiar points | Denegado | — | **No ejecutado** | Abort por diff rules |
| T8 | Intentar cambiar role | Denegado | — | **No ejecutado** | Abort por diff rules |
| T9 | Crear submission propia | Permitido | — | **No ejecutado** | Abort por diff rules |
| T10 | Crear submission otro UID | Denegado | — | **No ejecutado** | Abort por diff rules |
| T11 | Leer Submissions sin role | Denegado, sin error role | — | **No ejecutado** | Abort por diff rules |
| T12 | Escribir categoría sin role | Denegado, sin error role | — | **No ejecutado** | Abort por diff rules |
| T13 | Escribir categoría como admin | Permitido | — | **No ejecutado** | Abort por diff rules |
| T14 | Leer AuditLogs sin role | Denegado, sin error | — | **No ejecutado** | Abort por diff rules |
| T15 | Catch-all deny | Denegado | — | **No ejecutado** | Abort por diff rules |

**Comando de tests (no ejecutado por política de abort):**

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
npm run test:rules
```

**Comando de comparación (ejecutado):**

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
npm run compare:rules
# Exit code: 1 — COMPARE_STATUS=diff
```

---

## 6. Fallos detectados

### Bloqueo principal — reglas local ≠ activas

| Campo | Detalle |
|-------|---------|
| **Test** | Preflight compare (antes de emulador) |
| **Error exacto** | `COMPARE_STATUS=diff` — 108 líneas local vs 456 remotas |
| **Regla involucrada** | Release completo `cloud.firestore` |
| **Posible causa** | Deploy 2B-3.2/2B-3.3 no persistió, rollback en consola, o consulta a release distinto del esperado |
| **Recomendación** | 1) Verificar en [Firebase Console → Firestore → Rules](https://console.firebase.google.com/project/sancarlina-99748/firestore/rules) qué ruleset está activo. 2) Si debe ser GondolApp: redeploy **aprobado** de [`firestore.rules`](firestore.rules). 3) Re-ejecutar `npm run compare:rules` hasta `COMPARE_STATUS=match`. 4) Luego `npm run test:rules`. |

### Tests de emulador

No hubo fallos de Jest porque **no se llegó a ejecutar** el emulador (política de abort del plan).

---

## 7. Confirmación de alcance

| Restricción | Cumplido |
|-------------|----------|
| No deploy | **Sí** |
| No Storage | **Sí** |
| No Functions | **Sí** |
| No carpeta `functions/` | **Sí** |
| No Kotlin / `app/src/main` | **Sí** |
| No Gradle Android | **Sí** |
| No versionCode / versionName | **Sí** |
| No keystore / AAB / firma | **Sí** |
| No assets Stitch | **Sí** |
| No commit | **Sí** |
| Solo emulador / local | **Sí** (infra creada; emulador no arrancado por abort) |

---

## 8. Próximo paso recomendado

1. **Urgente — alinear producción:** Confirmar en consola por qué el release activo sigue siendo el ruleset del foro (456 líneas). Si GondolApp debe regir, ejecutar deploy controlado de `firestore.rules` (fase aparte, con aprobación explícita).
2. **Re-validar paridad:** `npm run compare:rules` debe devolver `COMPARE_STATUS=match`.
3. **Ejecutar tests locales:** `npm run test:rules` — validará T1–T15 contra reglas locales en emulador (`demo-sancarlina-rules`).
4. Si compare OK y tests pasan: cerrar validación automática 2B-3.4 y continuar pruebas reales de app o fase 2B-4.
5. Si tests fallan tras alinear: preparar hotfix **solo** en `firestore.rules` sin deploy automático.

**Título de commit sugerido (cuando el usuario lo pida):**  
`Agregar tests automáticos locales para reglas de Firestore con emulador`

---

*Infraestructura de tests lista y reversible. Ejecución pendiente de alinear reglas activas con el repo local.*
