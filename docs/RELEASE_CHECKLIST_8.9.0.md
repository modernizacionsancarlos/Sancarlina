# Checklist de publicación — 8.9.0 (versionCode 79)

## Estado del código

- Puntos y Beneficios ya no usa claves duplicadas en la lista.
- El historial y el escáner QR reciben sus dependencias desde el contenedor de la app.
- El canje ficticio y la escritura directa de saldo/historial fueron eliminados.
- Catálogos, formularios y notificaciones usan caché, TTL, invalidación por versión y límites de consulta.
- Imágenes críticas: máximo 6 precargas a 640×360, caché de disco de 150 MB y carga de galerías bajo demanda.
- App Check usa Debug en compilaciones internas y Play Integrity en Release.

## Verificación ejecutada el 9 de septiembre de 2026

- 37/37 pruebas unitarias aprobadas.
- 32/32 pruebas de reglas Firestore aprobadas en emulador local.
- `lintDebug` aprobado sin errores bloqueantes.
- APK Debug y APK de instrumentación generados; Kotlin Release compiló correctamente.
- Prueba instrumental de Puntos y Beneficios aprobada en Pixel 9.
- Arranque en frío verificado sin errores fatales.
- No se generó el Bundle firmado ni se desplegaron reglas/configuración a Firebase.

## Obligatorio antes de publicar en Play

1. Desplegar `firestore.rules`. Las reglas locales no protegen producción hasta ese despliegue.
2. Confirmar en Firebase que `awardPoints` existe en `southamerica-east1` y valida, en servidor: usuario autenticado, comercio válido, rango de puntos, autorización del emisor e idempotencia. La implementación no está en este repositorio y no pudo auditarse.
3. Registrar el SHA-256 de la firma Release en Firebase/App Check y comprobar tokens de Play Integrity antes de activar enforcement.
4. Configurar alertas de presupuesto y revisar Firestore, Storage y Functions después del despliegue. Las alertas no son un tope duro.
5. Generar el Android App Bundle firmado en Android Studio y probarlo en un track interno antes de producción.

## Prueba rápida del Bundle firmado

- Abrir Inicio, Puntos y Beneficios, detalle de un beneficio e Historial.
- Probar QR inválido, permiso de cámara denegado y QR válido con una cuenta de prueba.
- Abrir un comercio con imágenes, volver atrás y reabrirlo para comprobar la caché.
- Abrir Formularios sin red y enviar uno al recuperar conexión.
- Confirmar que no aparecen cierres en Android Vitals / Crashlytics durante el track interno.
