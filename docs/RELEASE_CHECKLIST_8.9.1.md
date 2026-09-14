# Checklist de publicación — 8.9.1 (versionCode 80)

Release correctiva. Resuelve que los relevamientos cargados por los registradores
municipales no llegaban al panel web, e incorpora la telemetría y el endurecimiento
de seguridad pendientes desde la 8.9.0.

## Qué cambia

### Envío de relevamientos

- La app ya no exige que Android marque la red como validada para intentar un envío.
  Con señal móvil débil dentro de un local esa validación falla o tarda, y los
  relevamientos quedaban sin intentarse. Ahora se intenta, y si la red no sirve la
  cola reintenta.
- El trabajo de sincronización es acelerado (`setExpedited`), de modo que el Ahorro
  de datos de Android ya no lo difiere hasta encontrar Wi-Fi.
- Ante un rechazo por permisos, la app renueva el ID token y reintenta una vez antes
  de dar el envío por fallido.
- En el primer intento ya no se lee el documento remoto antes de escribirlo: el
  identificador es un UUID recién generado y no puede existir en el servidor. Ahorra
  una lectura facturada por relevamiento y elimina un modo de falla.

### Visibilidad del error

- "Registro en calle" muestra una tarjeta de aviso cuando hay relevamientos que el
  servidor rechazó, con el motivo y un botón para reintentar todos.
- Cada registro reciente muestra su propio motivo de error y su botón de reintento.
- Aviso específico cuando el Ahorro de datos está activo, con acceso directo a los
  ajustes del sistema.
- Los mensajes de error explican qué hacer y aclaran que el dato sigue guardado en
  el teléfono.

### Sesión

- Tras iniciar sesión la app fuerza la renovación del ID token. Un registrador recién
  habilitado ya no arrastra hasta una hora con un token sin su claim de rol.

### Telemetría y seguridad

- Se incorporan Crashlytics, Performance Monitoring y Remote Config. La recolección
  queda desactivada en compilaciones debug.
- Se elimina `usesCleartextTraffic` y se agrega configuración de seguridad de red.
  El tráfico sin cifrar queda prohibido en release.
- Las reglas de copia de seguridad excluyen token de notificaciones, preferencias
  cifradas, intereses, itinerario y metadatos de caché.
- `superAdmins` pasa a solo lectura desde el cliente y `AuditLogs` a solo escritura
  incremental. Requiere desplegar `firestore.rules`.

## Antes de generar el Bundle

1. Crear `keystore.properties` en la raíz del proyecto, con ruta absoluta y barras
   normales. No se versiona.
2. Crear `.env` en la raíz con la `MAPS_API_KEY` real. Sin ese archivo el plugin de
   secrets cae en `.env.example` y el mapa queda inutilizable.
3. Verificar en Firestore que ningún `image_url` ni `cover_url` de `tenants` empiece
   con `http://`. El tráfico sin cifrar ahora está bloqueado y esas imágenes no
   cargarían.
4. Confirmar que `~/.gradle/gradle.properties` contiene
   `systemProp.javax.net.ssl.trustStoreType=Windows-ROOT`. Sin esa línea, la
   inspección TLS de Avast impide que Gradle descargue dependencias y el build falla.

## Generación del Bundle

En Android Studio: **Build → Generate Signed App Bundle / APK → Android App Bundle**,
variante `release`.

Si el build falla en la tarea de subida del mapping de R8 a Crashlytics, poner
`mappingFileUploadEnabled = false` en el bloque `release` de `app/build.gradle.kts`,
generar el Bundle y volver a activarlo después. Sin esa subida los stack traces de
producción llegan ofuscados, así que es una medida temporal, no definitiva.

## Verificación ejecutada el 14 de septiembre de 2026

- 36/36 pruebas unitarias aprobadas.
- `lintDebug` sin errores; 83 advertencias, todas preexistentes.
- APK debug generado y empaquetado correctamente con los plugins nuevos.
- No se generó el Bundle firmado.
- No se probó en dispositivo físico.
- No se desplegaron las reglas de Firestore desde este repositorio.

## Obligatorio antes de publicar

1. Desplegar `firestore.rules`. Las reglas en producción ya diferían del repositorio
   antes de estos cambios: revisar el diff con `npm run compare:rules` antes de
   ejecutar el despliegue, para no pisar una edición hecha por consola.
2. Registrar el SHA-256 de la firma release en Firebase y App Check.
3. Verificar que Crashlytics recibe eventos del track interno.

## Prueba en track de pruebas internas

Publicar primero en **pruebas internas** con los ocho registradores. Es un grupo
cerrado y conocido, y son las únicas personas que pueden confirmar el arreglo.

Antes de instalar, avisarles que **no desinstalen la app ni borren sus datos**: los
relevamientos pendientes viven en el almacenamiento interno y se perderían.

Recorrido de prueba:

- Abrir la app con datos móviles, sin Wi-Fi, dentro de un local con señal débil.
- Cargar un relevamiento y confirmar que sube sin intervención.
- Verificar en "Envíos de formularios" que los relevamientos atascados de días
  anteriores pasan a "Enviado".
- Confirmar en el panel web que esos documentos aparecen en `Submissions`.
- Con el Ahorro de datos activado, comprobar que aparece la tarjeta de aviso y que
  el botón abre los ajustes del sistema.
- Cerrar sesión, volver a entrar y enviar un relevamiento.
- Revisar Crashlytics y Android Vitals durante 48 horas.

## Promoción a producción

Solo después de que al menos un registrador confirme que un relevamiento sube con
datos móviles en el mismo negocio donde antes fallaba. Despliegue gradual al 20 %.
