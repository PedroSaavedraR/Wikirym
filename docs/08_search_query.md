# 08 · Búsqueda por texto y filtro por tipo con chips

**Objetivo:** que el usuario elija qué tipo de entradas ver (personajes, misiones, objetos o lugares) y pueda buscar por texto dentro de ese tipo.

## Qué cambia

- **Layout.** `activity_main.xml` añade:
  - un `TextInputLayout`/`TextInputEditText` (Material 3) con `imeOptions="actionSearch"` y sin autocorrección (`textNoSuggestions`), porque los nombres de Skyrim no están en el diccionario;
  - un botón **Buscar**;
  - un `ChipGroup` de selección única y obligatoria con cuatro chips de filtro: Personajes, Misiones, Objetos y Lugares;
  - una vista vacía (`main_empty_view`) para los mensajes;
  - un indicador de progreso.
- **Dos peticiones en `UespAPI`:**
  - `getCategoryMembers` (la de antes): lista una categoría completa.
  - `searchPages` (nueva): `list=search` con `srsearch` y `srlimit`. La ruta fija `srnamespace=134` (el espacio de nombres de Skyrim) y `srprop=` (no pide fragmentos ni tamaños, solo título e id).
- **`SearchResponse`.** Modelo de la respuesta de búsqueda: `query → search`, con objetos `{ns, title, pageid}` que encajan con `WikiEntry`.
- **`UespCategories`.** Relaciona cada `EntryType` con su categoría de la wiki.
- **`MainActivity`:**
  - **Sin texto:** lista la categoría del chip seleccionado. Al abrir la app aparecen los personajes.
  - **Con texto:** busca por **prefijo en el título**, solo dentro del tipo elegido. Cada palabra escrita debe ser el principio de una palabra del título: "af" encuentra "Afflicted" y "aela t" encuentra "Aela the Huntress". La consulta queda así: `intitle:aela* intitle:t* incategory:Skyrim-NPCs`.
  - **Busca mientras se escribe:** un `TextWatcher` relanza la búsqueda 400 ms después de la última tecla (con `Handler.postDelayed`), para no hacer una petición por cada letra cuando se escribe rápido.
  - También lanza la carga al cambiar de chip, al pulsar **Buscar** y al pulsar la tecla de búsqueda del teclado.
  - Gestiona tres estados: cargando, lista y mensaje (sin resultados o error).
  - Oculta el teclado con `WindowInsetsControllerCompat`.
  - Cancela la petición anterior antes de lanzar otra, para que una respuesta lenta no sustituya a la última.

## Diferencias con el ejemplo

- **El texto vacío no es un error.** En el ejemplo, buscar sin texto muestra un error en el campo. Aquí, con los chips, tiene sentido: sin texto se listan todas las entradas del tipo elegido.
- **Búsqueda por prefijo y mientras se escribe.** La búsqueda normal de MediaWiki es de texto completo y por palabras enteras: "af" no encuentra "Afflicted", sino páginas que contienen la palabra "af". Con `intitle:` y el comodín `*` se busca el principio de las palabras del título, que es lo que espera alguien que va escribiendo un nombre. Antes de montar la consulta, de cada palabra se quitan los caracteres que no son letras, números, apóstrofos ni guiones, para que el usuario no pueda escribir sintaxis de búsqueda.
- **Filtro por categorías.** MusicBrainz busca con una consulta Lucene (`artist:"texto"`). MediaWiki usa CirrusSearch, y el filtro es `incategory:`, que **no entra en las subcategorías**. Casi todos los objetos (espadas, armaduras, artefactos…) están en subcategorías de `Skyrim-Items`, así que para Objetos se buscan varias a la vez con `|`: `incategory:Skyrim-Items|Skyrim-Items-Artifacts|Skyrim-Weapons-Swords|…`.
- **`srnamespace=134` es obligatorio.** Sin él, `incategory:` no devuelve nada.

## Para explicar

- **Estados de la UI.** Cargando, contenido, vacío y error se muestran y ocultan desde la actividad. En la fase MVP pasarán a ser métodos de la vista.
- **Dos endpoints, el mismo modelo.** `categorymembers` y `search` devuelven JSON distintos, pero sus elementos tienen `pageid` y `title`, así que los dos se convierten en `WikiEntry`.
- **Cancelar peticiones.** `Call.cancel()` evita mostrar resultados viejos, algo que importa mucho al buscar mientras se escribe. En `onFailure` se comprueba `call.isCanceled()` para no enseñar un error por una petición cancelada a propósito. En `onDestroy` se cancelan la petición en curso y la búsqueda pendiente del `Handler`.
- **Esperar antes de buscar (*debounce*).** Cada tecla reprograma la búsqueda: solo se lanza cuando el usuario deja de escribir un momento. Así no se satura el servidor.

## Cómo probarlo

- Al abrir la app aparecen los personajes. Pulsa **Lugares**: aparece la lista de lugares.
- Pulsa **Personajes** y escribe letra a letra "afflicted":
  - con "a" aparecen nombres con palabras que empiezan por A (Aela the Huntress, Adril Arano…);
  - con "af" ya aparecen Afflicted y Afflicted Refugee;
  - con "afflicted" solo quedan esos dos.
  - No hace falta pulsar **Buscar**.
- Escribe "aela t": aparece Aela the Huntress.
- Pulsa **Objetos** y escribe "dawn": aparecen Dawnbreaker y el equipo de la Guardia del Alba. Dawnbreaker está en la subcategoría `Skyrim-Weapons-Swords`.
- Escribe un texto sin sentido: aparece "No hay resultados".
- Borra el texto: vuelve la lista completa del tipo seleccionado.
