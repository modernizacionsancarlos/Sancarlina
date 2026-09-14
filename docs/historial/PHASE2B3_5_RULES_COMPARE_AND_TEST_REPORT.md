# PHASE 2B-3.5 — Rules Compare and Test Report

**Fecha:** 2026-06-16  
**Proyecto:** GondolApp Android (`com.sancarlina.app`)  
**Firebase project:** `sancarlina-99748`  
**Ejecutor:** Modo Agente (Cursor)

---

## 1. Veredicto

| Criterio | Resultado |
|----------|-----------|
| **¿Compare corregido?** | **Sí** |
| **¿Rules locales y activas coinciden?** | **Sí** (release `cloud.firestore` default) |
| **¿Se ejecutó redeploy?** | **No** — no era necesario |
| **¿Tests ejecutados?** | **Sí** |
| **¿Todos pasaron?** | **Sí** (15/15) |
| **Riesgo** | **Bajo** para GondolApp. **Medio** operativo: existe un release paralelo `cloud.firestore/estudiantina` con reglas del foro (456 líneas) que no afecta la DB default pero puede confundir diagnósticos futuros. |

---

## 2. Diagnóstico del compare script

| Hallazgo | Archivo/línea | Causa | Corrección aplicada |
|----------|---------------|-------|---------------------|
| Falso diff local vs remoto | [`scripts/compare-firestore-rules.js`](scripts/compare-firestore-rules.js) L28-31 (antes) | Usaba `rulesApi.getLatestRulesetName()`, que internamente hace `releases.find(r => r.name.startsWith('projects/.../releases/cloud.firestore'))`. Con releases ordenados por `createTime` descendente, el **primer match** era `cloud.firestore/estudiantina` (DB nombrada, reglas foro 456 líneas), no el release default `cloud.firestore`. | Reemplazado por búsqueda **exacta** del release `projects/sancarlina-99748/releases/cloud.firestore` vía `getDefaultFirestoreRelease()`. |
| Reporte 2B-3.4 indicaba producción con rules viejas | Mismo script | El deploy 2B-3.2/2B-3.3 **sí** estaba activo en el release correcto; el script consultaba el release equivocado. | Sin redeploy. Compare corregido devuelve `COMPARE_STATUS=match`. |
| Salida sin nombre de release | Script compare | No se logueaba qué release se comparaba | Añadido `RELEASE=...` en salida match/diff. |

**Conclusión:** Opción **B** confirmada — el script leía mal. Opción **A** (producción con rules viejas) aplica solo al release `estudiantina`, no a la DB default de GondolApp.

---

## 3. Estado de rules remotas

| Release | Ruleset | Coincide con local | Observación |
|---------|---------|-------------------|-------------|
| `projects/sancarlina-99748/releases/cloud.firestore` | `projects/sancarlina-99748/rulesets/bc7642ab-8b81-4a76-b2b1-bdf4c090e91a` | **Sí** | Release activo Firestore **default** (GondolApp, 108 líneas, hotfix `get('role','')`). `updateTime`: 2026-06-16. |
| `projects/sancarlina-99748/releases/cloud.firestore/estudiantina` | `projects/sancarlina-99748/rulesets/194b6bf6-2a9d-41b5-8348-7b5f8e5feabc` | **No** | DB nombrada `estudiantina` — reglas foro escolar (456 líneas, `request.auth.token.role` directo). **No es** el target de GondolApp. |
| `projects/sancarlina-99748/releases/firebase.storage/sancarlina-99748.firebasestorage.app` | (storage) | N/A | Release Storage; fuera de alcance. |

**Total releases en proyecto:** 3.

---

## 4. Comandos ejecutados

| Comando | Resultado | Observación |
|---------|-----------|-------------|
| `git status --short` | OK (vacío al inicio; tras fix: `M scripts/compare-firestore-rules.js`) | Sin cambios Kotlin/Gradle |
| Listado releases (diagnóstico API) | OK | Identificados 2 releases `cloud.firestore` |
| `firebase use` | `sancarlina-99748` | Proyecto correcto |
| `firebase deploy --only firestore:rules --project sancarlina-99748 --dry-run` | OK | `rules file firestore.rules compiled successfully` |
| `npm run compare:rules` (tras fix) | **OK** exit 0 | `COMPARE_STATUS=match`, 3181 bytes local = remoto |
| `npm run test:rules` (JDK 17) | **Fallo** | `firebase-tools no longer supports Java version before 21` |
| `npm run test:rules` (JAVA_HOME → JDK 25) | **OK** | 15/15 tests passed, emulador Firestore v1.21.0 |
| `firebase deploy --only firestore:rules` | **No ejecutado** | No requerido — rules ya coincidían |

---

## 5. Resultados T1–T15

| ID | Caso | Esperado | Obtenido | Estado |
|----|------|----------|----------|--------|
| T1 | Catálogo público tenants | Permitido | Permitido | **PASS** |
| T2 | Catálogo público products | Permitido | Permitido | **PASS** |
| T3 | Leer perfil propio | Permitido | Permitido | **PASS** |
| T4 | Leer perfil ajeno | Denegado | Denegado (PERMISSION_DENIED) | **PASS** |
| T5 | Crear perfil propio válido | Permitido | Permitido | **PASS** |
| T6 | Actualizar perfil campos seguros | Permitido | Permitido | **PASS** |
| T7 | Intentar cambiar points | Denegado | Denegado (L41 update) | **PASS** |
| T8 | Intentar cambiar role | Denegado | Denegado (L41 update) | **PASS** |
| T9 | Crear submission propia | Permitido | Permitido | **PASS** |
| T10 | Crear submission otro UID | Denegado | Denegado (L51 create) | **PASS** |
| T11 | Leer Submissions sin role | Denegado, sin error role | Denegado (sin `Property role is undefined`) | **PASS** |
| T12 | Escribir categoría sin role | Denegado, sin error role | Denegado (L79 create) | **PASS** |
| T13 | Escribir categoría como admin | Permitido | Permitido | **PASS** |
| T14 | Leer AuditLogs sin role | Denegado, sin error | Denegado | **PASS** |
| T15 | Catch-all deny | Denegado | Denegado (L104) | **PASS** |

**Resumen:** 15 pasados, 0 fallidos. Tiempo Jest: ~8.6 s (emulador incluido ~31 s total).

---

## 6. Fallos detectados

No hubo fallos de reglas de negocio en los tests.

**Infraestructura (resuelto para esta ejecución):**

| Test / paso | Error exacto | Regla involucrada | Posible causa | Recomendación |
|-------------|--------------|-------------------|---------------|---------------|
| `npm run test:rules` con JAVA_HOME=17 | `firebase-tools no longer supports Java version before 21` | N/A (emulador) | Firebase Emulator Firestore requiere JDK ≥21; el PATH por defecto usa JDK 17 (Gradle Android). | Para CI/local: `JAVA_HOME` apuntar a JDK 21+ solo al correr `npm run test:rules`. JDK 17 sigue válido para APK. |

---

## 7. Confirmación de alcance

| Restricción | Cumplido |
|-------------|----------|
| No Kotlin | **Sí** |
| No Gradle Android | **Sí** |
| No Storage deploy | **Sí** |
| No Functions | **Sí** |
| No AAB/keystore | **Sí** |
| No versionCode/versionName | **Sí** |
| No assets Stitch | **Sí** |
| No commit | **Sí** |
| Deploy firestore:rules | **No ejecutado** (no requerido) |

**Archivo modificado:** [`scripts/compare-firestore-rules.js`](scripts/compare-firestore-rules.js)  
**Archivo creado:** [`PHASE2B3_5_RULES_COMPARE_AND_TEST_REPORT.md`](PHASE2B3_5_RULES_COMPARE_AND_TEST_REPORT.md)

---

## 8. Próximo paso recomendado

1. **Cerrar validación Firestore Rules (2B-3.4 / 2B-3.5):** compare OK + 15/15 tests en emulador.
2. **Avanzar a fase 2B-4** o pruebas reales de app contra Firestore default.
3. **Opcional — release `estudiantina`:** si esa DB nombrada ya no se usa, evaluar en consola si conviene alinear o eliminar reglas huérfanas (fuera de alcance GondolApp).
4. **Opcional — DX:** documentar en README o script que `npm run test:rules` requiere `JAVA_HOME` con JDK 21+ (en esta PC: `C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot`).
5. **Commit sugerido (cuando el usuario lo pida):**  
   `Corregir comparación de reglas Firestore y validar 15 tests en emulador`

---

*El falso positivo de 2B-3.4 quedó explicado: mismo proyecto, dos releases Firestore; el script tomaba el de la DB nombrada `estudiantina` en lugar del default `cloud.firestore`.*
