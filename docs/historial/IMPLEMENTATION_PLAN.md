# Plan de implementación — GondolApp Premium Final v1

| Fase | Alcance | Estado |
|---|---|---|
| 0 | Instrucciones internas, Git, arquitectura y build base | Completada |
| 1 | Inventario Stitch/Android y mapeo de rutas/datos | Completada |
| 2 | DESIGN.md, variantes finales y preservación de fuentes | Completada |
| 3 | Tokens, tipografía, formas y dimensiones Android | Completada |
| 4 | Top bar, navegación inferior, Inicio y componentes compartidos | Completada |
| 5 | Propagación por pantallas existentes mediante componentes/tokens | Completada |
| 6 | Tests, lint, R8, emulador, responsive y navegación | Completada |
| 7 | `versionCode`, informe y notas Google Play | Completada |

## Restricciones

- No cambiar contratos, rutas, modelos, repositorios, Firebase ni reglas de negocio.
- No eliminar acciones para simplificar diseños.
- No descartar el árbol de trabajo existente.
- No agregar funcionalidades de backend por aparecer en una referencia visual.
- No generar AAB firmado; la firma final se realiza en Android Studio.

## Validación mínima

1. `testDebugUnitTest`.
2. `assembleDebug`.
3. `lintDebug`.
4. `minifyReleaseWithR8`.
5. Recorrido en emulador por Inicio, Turismo, Mapa, Puntos y Perfil.
6. Verificación de acceso administrativo y pantallas secundarias representativas.
7. Capturas en teléfono estándar y ancho compacto; revisión de tablet mediante preview/configuración cuando sea viable.

## Resultado final

- `testDebugUnitTest`, `assembleDebug` y `lintDebug`: correctos.
- Recorrido en Pixel 9: Inicio, Turismo, Mapa, Puntos y Perfil sin cierres.
- Compatibilidad de recursos corregida para API 24, 27 y 31+.
- Versión preparada: `versionCode 69`, `versionName 8.5.0`.
