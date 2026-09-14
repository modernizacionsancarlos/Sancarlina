# GondolApp Premium Final v1 — Sistema visual

## Dirección

Experiencia cálida, natural, elegante y contemporánea para descubrir San Carlos y Mendoza. La fotografía y el contenido real son protagonistas; la interfaz acompaña con superficies crema, controles suaves y jerarquía editorial.

## Tokens

| Rol | Valor |
|---|---|
| Background | `#F7F4EA` |
| Surface | `#FFFDF7` |
| Surface low | `#F4F0E5` |
| Surface container | `#EEE8DA` |
| Surface high / arena | `#E9E1CF` |
| Text primary | `#1F241C` |
| Text secondary | `#5E6257` |
| Primary / oliva | `#66743B` |
| Secondary / borgoña | `#9E334A` |
| Secondary deep | `#800020` |
| Tertiary / dorado accesible | `#A87810` |
| Error | `#BA1A1A` |

El oliva identifica navegación, selección y acciones generales. El borgoña destaca acciones de alto valor. El dorado se limita a valoración, premios o acentos informativos.

### Tema oscuro automático

- El tema claro sigue siendo la identidad principal.
- La app respeta el modo claro/oscuro configurado en el dispositivo.
- En oscuro se usan fondos casi negros (`#080B09`), superficies carbón y verde lima (`#B8F34A`) para acciones y selección.
- Texto, contornos, estados y barras del sistema cambian mediante roles semánticos; no se invierten colores de forma manual por pantalla.

## Tipografía

- Títulos: Be Vietnam Pro, 600–800.
- Cuerpo, etiquetas y controles: Manrope, 400–600.
- Hero móvil: 40–48sp según ancho disponible.
- H1: 30sp; H2: 24sp; H3: 20sp.
- Cuerpo: 16sp/24sp y 14sp/20sp.
- Caption: mínimo 12sp cuando sea información funcional.

## Forma, espacio y profundidad

- Ritmo base: 4dp; agrupación principal: 8/16/24/32dp.
- Margen móvil: 16dp; tablet: 24dp.
- Tarjetas: 24dp; tarjetas inmersivas: 28dp.
- Botones y chips: pill.
- Bottom sheets: 28dp en esquinas superiores.
- Área táctil mínima: 48×48dp.
- Sombras ambientales de 2–8dp, sin negro duro.

## Componentes compartidos

- Top bar: avatar/menú, marca centrada y notificaciones.
- Bottom navigation: cinco destinos; Mapa es la acción central elevada.
- Hero: fotografía real disponible, gradiente oscuro y ubicación institucional.
- Search bar: superficie clara, acción primaria y acceso a filtros/búsqueda avanzada.
- Section header: título editorial y acción borgoña.
- Cards: experiencia, comercio, categoría, producto y estados.
- Formularios: label persistente, error textual y no sólo por color.
- Estados: loading, vacío, error, offline y éxito con la misma identidad.

## Reglas de implementación

- Usar datos reales; nunca inventar horarios, precios, valoraciones o disponibilidad.
- Si no existe una función de recorrido/reserva, no mostrar un CTA falso.
- Respetar system insets y no dibujar controles bajo barras del sistema.
- Conservar navegación, ViewModels, repositorios, Firebase y permisos.
- En tablet, limitar ancho o usar grillas; no estirar tarjetas sin criterio.
