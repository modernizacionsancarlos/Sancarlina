# Google Play Console - versión 8.8.2 (76)

## es-419

Mejoramos el registro de comercios en calle. Al escribir una dirección, el mapa ahora busca y centra automáticamente el punto indicado, manteniendo la ubicación del dispositivo y el ajuste manual como alternativas. También corregimos el bloqueo que solicitaba una autorización no visible al guardar formularios y reforzamos la compatibilidad de preguntas de tipo sí/no.

## Cambios técnicos de la entrega

- `versionCode`: 76
- `versionName`: 8.8.2
- Geocodificación reactiva con espera breve para evitar búsquedas por cada tecla.
- Dirección contextualizada con localidad, San Carlos, Mendoza y Argentina.
- Autorización general de cuenta aplicada a esquemas antiguos sin volver a solicitarla en cada relevamiento.
- Campos booleanos ordinarios renderizados como controles visibles.
- GPS y selección manual conservados como respaldo o corrección del punto.
