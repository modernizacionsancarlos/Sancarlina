# Controles de costo y rendimiento de Firebase

## Qué queda implementado en la app

- Firestore conserva documentos en disco con un máximo de 200 MB y crea índices locales para acelerar consultas desde caché.
- Comercios, zonas y beneficios usan `stale-while-revalidate`: muestran caché inmediatamente y consultan al servidor sólo por vencimiento o por cambio de versión.
- Una única escucha a `app_metadata/public_catalog` reemplaza las escuchas completas que antes abría cada pantalla.
- Las consultas concurrentes del mismo catálogo se agrupan en una sola operación (`single-flight`).
- El perfil de cada usuario tiene una sola escucha compartida, aunque varias pantallas consuman saldo y favoritos.
- Formularios se guardan localmente, se consultan con máximo por comercio y se revalidan por versión/TTL.
- Notificaciones usan caché por usuario y se revalidan por versión/TTL; reseñas, historial de puntos, noticias y submissions administrativas tienen límites de resultados.
- Las reglas nuevas rechazan listados públicos sin límite para `tenants`, `areas`, `benefits`, `FormSchemas`, `banners`, `categories`, `commerces`, `products`, `turismo_points`, `Notifications`, `Reviews` e historial de puntos.
- App Check queda integrado: proveedor de depuración en Debug y Play Integrity en Release.
- La precarga de imágenes se limita a 6 recursos críticos a 640×360; no descarga logos alternativos ni galerías completas al iniciar. La caché de imágenes queda acotada a 150 MB.
- Los llamados de acreditación de puntos y conversiones tienen enfriamiento local para evitar dobles ejecuciones accidentales.
- Si una actualización remota falla, el catálogo conserva la caché y aplica una espera de 60 segundos antes de reintentar, evitando ráfagas por falta de red.

## Política de revalidación

| Datos | TTL de respaldo | Límite por consulta pública |
|---|---:|---:|
| Comercios | 6 horas | 500 |
| Zonas | 24 horas | 100 |
| Beneficios | 1 hora | 100 |
| Formularios | 1 hora | 50 por comercio / 100 total |
| Noticias | carga de pantalla | 50 |
| Notificaciones | 15 minutos / cambio de versión | 50 |
| Reseñas | escucha de detalle | 20 |
| Historial de puntos | escucha de pantalla | 50 |

El TTL no retrasa cambios si la versión se incrementa correctamente. Es la red de seguridad para modificaciones provenientes de herramientas que todavía no actualicen `app_metadata`.

## Contrato de invalidación para el panel web / Google AI Studio

Crear el documento `app_metadata/public_catalog` con campos numéricos:

```text
tenantsVersion
areasVersion
benefitsVersion
formsVersion
notificationsVersion
updatedAt
```

Después de cada escritura exitosa, el panel que administra el contenido debe incrementar el campo correspondiente y actualizar `updatedAt`. El panel administrativo incluido en Android ya lo hace. Si el panel web no adopta este contrato, la app se actualizará al vencer el TTL, no inmediatamente.

## Activación en Firebase que sigue siendo necesaria

Estos pasos cambian producción y no se ejecutaron automáticamente:

1. Desplegar `firestore.rules` antes de distribuir la versión. La suite local pasó 32/32 casos, pero las reglas todavía no se aplicaron en producción.
2. Configurar App Check para la app Android con Play Integrity y registrar el certificado SHA-256 de Release.
3. Registrar el token Debug sólo en entornos internos.
4. Monitorear las métricas de App Check y recién después activar enforcement para Firestore, Storage y Functions. Activarlo sin validar los tokens puede bloquear usuarios legítimos.
5. Configurar alertas de presupuesto en Google Cloud Billing. Una alerta avisa; no constituye un tope duro de gasto.
6. Verificar en Usage/Billing que bajen lecturas, egresos de Storage e invocaciones de Functions después del lanzamiento.
7. Verificar la implementación desplegada de `awardPoints`: no existe código de Cloud Functions en este repositorio, por lo que no se pudo confirmar autenticación, validación de montos, idempotencia ni controles server-side.

## Imágenes: optimización que debe completar el origen

La app ahora evita precargas masivas y reutiliza disco, pero eso no reduce el peso del archivo original servido por Storage. El panel de carga o un proceso de backend debe generar, como mínimo:

- miniatura de listado: 320 px de ancho;
- tarjeta destacada: 640 px de ancho;
- imagen de detalle: 1280 px de ancho como máximo razonable;
- formato WebP con compresión visualmente aceptable;
- URLs versionadas y `Cache-Control` prolongado para archivos inmutables.

No conviene reemplazar URLs actuales hasta verificar que todos los comercios tengan sus variantes: hacerlo parcialmente produciría imágenes rotas.

## Medición

`DataAccessMetrics` registra en Debug intentos de caché, respuestas remotas, documentos devueltos e inicios de listeners. Son contadores de diagnóstico, no una copia de la factura: la validación definitiva se realiza con las métricas de Firebase/Google Cloud.
