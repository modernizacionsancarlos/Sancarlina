# Google Play Console — versión 8.7.0 (73)

## es-419

Ahora podés completar formularios aunque no tengas conexión. Las respuestas y sus adjuntos quedan guardados de forma segura en el dispositivo y se envían automáticamente al recuperar internet. También incorporamos una sección para revisar el estado de los envíos, detectar errores y sincronizar manualmente. Mejoramos la confiabilidad ante conexiones intermitentes y evitamos respuestas duplicadas durante los reintentos.

## Cambios técnicos de la entrega

- `versionCode`: 73
- `versionName`: 8.7.0
- Cola persistente local para respuestas y adjuntos.
- Sincronización automática con WorkManager y acción manual.
- IDs idempotentes entre SQLite, Firestore y Storage.
- Caché persistente de definiciones de formularios.
