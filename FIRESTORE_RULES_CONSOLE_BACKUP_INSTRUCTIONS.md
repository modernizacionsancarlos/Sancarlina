# Firestore Rules — Backup de consola (referencia)

**Proyecto:** `sancarlina-99748`  
**Fecha backup:** 2026-06-12  
**Método:** exportación automática vía Firebase Rules API (firebase-tools + `requireAuth`)

## Archivo de backup

- [`firestore.rules.console-backup.2026-06-12.rules`](firestore.rules.console-backup.2026-06-12.rules)

## Restaurar manualmente (rollback)

1. Abrir [Firebase Console → Firestore → Rules](https://console.firebase.google.com/project/sancarlina-99748/firestore/rules)
2. Copiar el contenido completo de `firestore.rules.console-backup.2026-06-12.rules`
3. Pegar en el editor → **Publish**

## Restaurar vía CLI (alternativa)

```powershell
# Temporalmente reemplazar firestore.rules por el backup, luego:
$env:NODE_USE_SYSTEM_CA = "1"
& "$env:APPDATA\npm\firebase.cmd" deploy --only firestore:rules --project sancarlina-99748
# Restaurar firestore.rules local del draft GondolApp después
```

## Nota importante

Las reglas de consola previas al deploy **no correspondían** al esquema GondolApp (`userProfiles`, `tenants`, etc.); incluían colecciones de otra aplicación (`users`, `threads`, `communities`, etc.). Conservar este backup es **crítico** para rollback.
