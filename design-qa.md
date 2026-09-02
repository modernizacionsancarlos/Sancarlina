# Design QA — tema claro y oscuro automático

## Alcance y evidencia

- Fuente visual oscura: `.codex-remote-attachments/01a05d00-7a83-7d31-a405-037a5c25bbbb/ec6fdd60-f08e-4563-ba29-7d1a334bd185/1-Photo-1.jpg`.
- Fuente visual clara: `.codex-remote-attachments/01a05d00-7a83-7d31-a405-037a5c25bbbb/ec6fdd60-f08e-4563-ba29-7d1a334bd185/5-Photo-5.jpg`.
- Implementación oscura: `tmp/theme-qa/gondolapp-dark-settled.png`.
- Implementación clara: `tmp/theme-qa/gondolapp-light-settled.png`.
- Comparación conjunta: `tmp/theme-qa/theme-reference-comparison.png`.
- Dispositivo: emulador Pixel 9, Android 15, viewport físico 1080 × 2424 px, densidad 420 dpi.
- Estado: pantalla Inicio estabilizada, datos remotos vacíos, barra inferior y controles persistentes visibles.
- Dimensiones: referencias 1280 × 850 px y 1280 × 853 px; capturas 1080 × 2424 px.
- Normalización: las referencias son un moodboard con tres dispositivos, no una pantalla 1:1. La comparación conserva cada fuente completa y ajusta proporcionalmente las imágenes dentro de un tablero común; no se evaluó fidelidad geométrica 1:1.

## Comparación visual

La implementación oscura reproduce la dirección solicitada: fondo casi negro, superficies carbón escalonadas, contornos discretos, texto claro y verde lima reservado para selección, filtros, progreso y navegación. La variante clara conserva la identidad crema, las superficies blancas y el oliva existente. En ambos estados se mantiene la misma jerarquía, contenido, fotografía/hero y estructura de navegación.

Regiones verificadas en la comparación conjunta:

- Cabecera y barras del sistema: iconografía clara sobre negro y oscura sobre crema, sin barras de color incongruentes.
- Hero y buscador: fotografía/gradiente preservados; buscador y filtro cambian de superficie y contraste con el sistema.
- Tarjetas y estados vacíos: superficies, bordes, iconos y texto responden al tema sin paneles claros aislados en oscuro.
- Navegación inferior: fondo, iconos, etiquetas, selección y acción central responden al esquema semántico.

No fue necesario un recorte adicional: las cuatro regiones críticas son legibles en las capturas completas a 1080 × 2424 px y en el tablero comparativo.

## Superficies de fidelidad requeridas

- Tipografía: Be Vietnam Pro y Manrope se conservan; pesos, tamaños, saltos de línea y jerarquía no cambian al alternar el sistema.
- Espaciado y ritmo: no se modificaron márgenes, radios, alturas ni composición; no se observa recorte u overflow nuevo.
- Colores y tokens: los colores de fondo, superficie, contenido, contorno, acciones y estados ahora derivan de `MaterialTheme.colorScheme`; el oscuro usa base `#080B09` y primario `#B8F34A`.
- Imágenes: se preservaron logo, hero e iconos existentes; no se introdujeron placeholders ni dibujos sustitutos.
- Texto y contenido: el contenido real y los estados vacíos se conservaron sin inventar datos.

## Findings

- No quedan diferencias P0, P1 o P2 dentro del alcance del tema automático.
- P3: el logotipo oficial marrón/verde pierde algo de presencia sobre la cabecera negra. No se alteró porque no existe una variante clara oficial en los recursos inspeccionados y la identidad debía preservarse.

## Interacciones y ejecución

- Cambio de `uimode` del dispositivo entre claro y oscuro.
- Cierre y relanzamiento de la app en ambos modos.
- Llegada correcta a Inicio después del splash.
- Sin excepciones fatales observadas en Logcat durante la verificación.
- Compilación, pruebas unitarias y Android Lint completados correctamente. Lint conserva 62 advertencias preexistentes/no bloqueantes y 0 errores.

## Historial de comparación

- Primera captura: se tomó durante el splash y no era válida para evaluar la pantalla final.
- Corrección: se esperó la estabilización de Inicio y se recapturaron ambos temas con el mismo emulador, viewport y estado de datos.
- Resultado posterior: barras del sistema, cabecera, buscador, tarjetas, estados vacíos y navegación presentan contraste y adaptación consistentes.

## Checklist de implementación

- [x] Tema claro como identidad principal.
- [x] Selección automática mediante el tema del sistema.
- [x] Esquema oscuro negro/carbón con acento lima.
- [x] Barras del sistema sincronizadas.
- [x] Componentes y pantallas migrados a colores semánticos.
- [x] Compilación, pruebas, lint y comparación visual.

final result: passed
