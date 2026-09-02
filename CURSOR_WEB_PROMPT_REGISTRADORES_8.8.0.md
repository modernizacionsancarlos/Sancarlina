# Prompt para Cursor — Web administrativa compatible con GondolApp Android 8.8.0

Quiero que revises e implementes en la aplicación Web administrativa de GondolApp el circuito completo de usuarios registradores y recepción de formularios, manteniendo compatibilidad exacta con la app Android 8.8.0 (`versionCode 74`). No hagas una solución paralela ni cambies nombres de colecciones o campos sin migración. Primero inspecciona la arquitectura, Firebase, Cloud Functions, autenticación, roles, formularios y panel de respuestas existentes; luego integra los cambios mínimos y coherentes. No dejes TODOs, mocks ni placeholders.

## Objetivo

Desde la Web, un administrador debe poder crear o habilitar cuentas de registradores municipales, asignarles formularios y ver en tiempo real las respuestas que envía Android, tanto las enviadas inmediatamente con internet como las almacenadas offline y sincronizadas al reconectarse. El administrador debe poder revisar, aprobar o rechazar cada respuesta sin mostrar claves internas como `field_...` al usuario.

## Contrato ya implementado por Android

1. Colección de esquemas: `FormSchemas`.
2. Colección de respuestas: `Submissions`.
3. Cada respuesta usa como ID de documento el mismo UUID estable de `client_submission_id`. Una corrección desde Android actualiza ese mismo documento; nunca debe interpretarse como una respuesta nueva ni crear un duplicado.
4. Campos relevantes de una respuesta:
   - `client_submission_id`: UUID estable e igual al ID del documento.
   - `form_id`: ID del esquema.
   - `created_by`: UID del registrador.
   - `created_at`: fecha original, que no cambia al corregir.
   - `client_updated_at`: fecha de la última edición hecha en Android.
   - `status`: Android envía o vuelve a dejar la respuesta en `pending` después de una corrección.
   - Datos respondidos: pueden estar en el mapa `data` o como campos dinámicos heredados. La Web debe normalizar ambos formatos.
5. Estados que la Web debe comprender: `pending`, `sending`, `sent`, `error`, y estados administrativos heredados como `published`, `approved` o `rejected`. La interfaz debe traducirlos a textos legibles en español.
6. Los adjuntos se suben a Firebase Storage con rutas deterministas asociadas al UID, al UUID de la respuesta y al campo. La Web debe leer las URLs/metadatos existentes y mostrar imágenes o enlaces descargables.
7. Android permite estos roles operativos: `registrar`, `registrador`, `field_registrar`, `staff` y `admin`. Usa `registrar` como rol canónico para las nuevas cuentas.
8. Un formulario aparece en Registro en calle solamente si está publicado, acepta respuestas y cumple sus restricciones de asignación.

## Usuarios registradores

Implementa una sección administrativa para crear, listar, habilitar/deshabilitar y asignar formularios a registradores.

- La creación de otra cuenta Firebase Auth debe hacerse mediante una Cloud Function protegida o backend con Firebase Admin SDK. No uses `createUserWithEmailAndPassword` desde la sesión del administrador porque cerraría o reemplazaría su sesión.
- Al crear o modificar un registrador, mantén sincronizados:
  - custom claim `role: "registrar"`;
  - documento `userProfiles/{uid}` con `role: "registrar"`;
  - `assigned_form_ids: string[]` con los formularios habilitados para esa persona.
- Si el proyecto ya tiene una colección/perfil equivalente, conserva compatibilidad y agrega una lectura normalizada, sin duplicar fuentes de verdad innecesariamente.
- Después de cambiar custom claims, informa que el registrador debe volver a iniciar sesión o renovar el token para recibir el nuevo rol.
- Solo administradores autenticados pueden ejecutar estas operaciones. Valida autorización en el backend, no solo ocultando botones.
- Nunca muestres contraseñas existentes. Para altas, permite una contraseña temporal segura o un flujo de restablecimiento por correo, según la infraestructura actual.

## Asignación y publicación de formularios

En el editor Web de cada documento de `FormSchemas`, soporta y persiste:

- `is_public: boolean`
- `accepts_responses: boolean`
- `field_registration_enabled: boolean`
- `allowed_roles: string[]`
- `assigned_user_ids: string[]`

La Web debe permitir asignar por rol, por usuarios concretos y desde el perfil mediante `assigned_form_ids`. Mantén lectura compatible con variantes camelCase existentes, pero escribe una forma canónica coherente con los documentos actuales. Agrega una explicación visible: un formulario que nunca fue descargado en Android no podrá abrirse offline; por eso debe publicarse y abrirse o precargarse antes del operativo.

## Recepción en tiempo real y sin duplicados

- Suscríbete a `Submissions` con `onSnapshot` o la abstracción en tiempo real que ya use el proyecto.
- Maneja correctamente cambios `added`, `modified` y `removed`.
- Indexa el estado de UI por ID de documento/`client_submission_id`. Un evento `modified` debe actualizar la tarjeta existente, no agregar otra.
- Ordena por `client_updated_at` y usa `created_at` como respaldo.
- Cuando una respuesta offline se sincroniza más tarde, debe aparecer automáticamente igual que una respuesta inmediata.
- Cuando Android corrige una respuesta enviada, la Web debe reflejar los datos nuevos, marcarla nuevamente como pendiente de revisión y conservar su historial/identidad.
- Si implementas notificaciones, genera una para nuevas respuestas y otra distinguible para correcciones. Hazlo de forma idempotente para no duplicar avisos por reconexiones o reintentos.

## Panel de respuestas y aprobación

Rediseña la vista móvil y de escritorio para que cada respuesta sea concreta y legible:

- Obtén el esquema por `form_id` y reemplaza cada ID técnico del campo por su `label`.
- Oculta metadatos internos (`client_submission_id`, marcas técnicas, URLs internas y claves `field_...`) del cuerpo principal.
- Muestra registrador, formulario, fecha original, última actualización, estado y adjuntos en un encabezado claro.
- Presenta respuestas como pares etiqueta/valor, con `Sí/No` para booleanos, formatos locales para fechas y miniaturas/enlaces para adjuntos.
- Incluye búsqueda y filtros por formulario, registrador, fecha y estado.
- Permite aprobar o rechazar con confirmación. Esos cambios administrativos deben respetar las reglas existentes y no deben ser sobrescritos salvo que el registrador edite nuevamente; en ese caso vuelve a `pending`.
- Evita modales extremadamente largos: usa página de detalle, panel lateral o modal de alto completo con scroll interno y acciones fijas.

## Seguridad y compatibilidad

- Revisa las reglas actuales de Firestore y Storage del repositorio Android antes de proponer cambios. Android ya depende de que el propietario pueda crear y corregir su propia respuesta conservando `created_by`, `client_submission_id`, `form_id` y `created_at`, y dejando `status = "pending"`.
- Los registradores no pueden aprobar, publicar, cambiar propietario ni modificar respuestas de otros usuarios.
- Los administradores sí deben poder listar y moderar respuestas.
- No despliegues reglas, Functions o índices sin mostrar el diff y ejecutar primero emuladores/pruebas locales.
- Mantén compatibilidad con respuestas y esquemas históricos.

## Verificación obligatoria

Ejecuta y documenta:

1. Alta de un registrador sin cerrar la sesión del administrador.
2. Asignación por rol y por usuario.
3. Visibilidad correcta de formularios permitidos y ocultamiento de no asignados.
4. Recepción en tiempo real de una respuesta inmediata.
5. Aparición al reconectarse de una respuesta creada offline.
6. Corrección del mismo documento sin duplicado.
7. Visualización de imágenes/archivos.
8. Aprobación y rechazo con permisos correctos.
9. Pruebas de reglas, lint, typecheck, tests y build de producción.
10. Revisión completa del diff sin alterar trabajo ajeno.

Al terminar, entrega: resumen de arquitectura, archivos modificados, migraciones o índices necesarios, resultado exacto de cada verificación, instrucciones de despliegue y riesgos reales pendientes. Si algún nombre o estructura real difiere de este contrato, no lo adivines: muestra la evidencia encontrada en el código y adapta la implementación conservando compatibilidad con Android 8.8.0.
