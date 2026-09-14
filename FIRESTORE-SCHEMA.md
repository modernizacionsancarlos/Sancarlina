# Esquema Firestore compartido (web + app Android)

Proyecto: **sancarlina-99748**. La web ciudadana y el panel `/admin` leen y escriben las mismas colecciones que la app móvil.

## Colecciones

| Colección | Uso | Escritura admin |
|-----------|-----|-----------------|
| `tenants` | Comercios, bodegas, restaurantes | Sí |
| `areas` | Zonas turísticas (filtros) | Sí |
| `benefits` | Catálogo informativo de beneficios por puntos | Sí |
| `FormSchemas` | Formularios por comercio | Sí |
| `userProfiles` | Perfiles ciudadanos / admins | Sí (puntos, rol) |
| `Notifications` | Avisos a usuarios | Crear (admin) |
| `Submissions` | Envíos de formularios | Moderar (admin) |
| `AuditLogs` | Historial de canjes | Lectura admin |
| `superAdmins` | Acceso panel `/admin` | Sí |

## Campos clave — `tenants`

- `name`, `industry`, `status` (`active` / `inactive`)
- `tenantId`, `tenant_id` (municipio, ej. `tenant_sancarlos`)
- `area_id` (id de documento en `areas`)
- `geo_coordinates`: string `"lat,lng"` o `geoPoint`
- `description`, `contact_email`, `cover_url`, `image_url`, `rating`, `reviews_count`
- `products` / `catalog` (opcional, embebido)

## Campos clave — `userProfiles`

- Document id: preferentemente `uid` de Firebase Auth
- `email`, `user_name`, `phone`, `location`
- `points`, `points_balance` (app móvil puede usar cualquiera)
- `role`: `citizen` | `admin`
- `favoriteTenantIds[]`, `tenantId`, `status`

## Campos clave — `benefits`

- `title`, `cost` y/o `points_cost`, `icon`, `description`, `active`

## Sincronización con Android

1. Misma instancia Firebase (`VITE_FIREBASE_PROJECT_ID`).
2. Tras guardar en `/admin`, incrementar la versión correspondiente en `app_metadata/public_catalog`; la app refresca ese conjunto sin volver a escuchar la colección completa.
3. No duplicar colecciones con otros nombres salvo acuerdo explícito en la app nativa.
4. **Puntos desde Android:** usar únicamente la Cloud Function `awardPoints`; el cliente no tiene permiso para modificar saldo ni historial directamente.
5. Constantes Kotlin: [`app/src/main/java/com/sancarlina/app/data/remote/FirestoreCollections.kt`](./app/src/main/java/com/sancarlina/app/data/remote/FirestoreCollections.kt).

## Invalidación de caché

El documento `app_metadata/public_catalog` usa los campos numéricos `tenantsVersion`, `areasVersion`, `benefitsVersion`, `formsVersion` y `notificationsVersion`. Cada escritura administrativa debe incrementar sólo el campo del conjunto alterado y actualizar `updatedAt`.

## Fuente de nombres en código

[`src/config/firestoreCollections.js`](../src/config/firestoreCollections.js)
