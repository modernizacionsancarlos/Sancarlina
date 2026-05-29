# Esquema Firestore compartido (web + app Android)

Proyecto: **sancarlina-99748**. La web ciudadana y el panel `/admin` leen y escriben las mismas colecciones que la app móvil.

## Colecciones

| Colección | Uso | Escritura admin |
|-----------|-----|-----------------|
| `tenants` | Comercios, bodegas, restaurantes | Sí |
| `areas` | Zonas turísticas (filtros) | Sí |
| `benefits` | Beneficios canjeables por puntos | Sí |
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
2. Tras guardar en `/admin`, la app móvil ve los cambios en la próxima lectura de Firestore (listeners o refresh).
3. No duplicar colecciones con otros nombres salvo acuerdo explícito en la app nativa.
4. **Puntos desde Android:** usar Cloud Function `awardPoints` (ver [MOBILE-ANDROID-SYNC.md](./MOBILE-ANDROID-SYNC.md)).
5. Constantes Kotlin: [android/FirestoreCollections.kt](./android/FirestoreCollections.kt).

## Fuente de nombres en código

[`src/config/firestoreCollections.js`](../src/config/firestoreCollections.js)
