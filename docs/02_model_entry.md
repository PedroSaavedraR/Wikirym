# 02 · Modelo WikiEntry

**Objetivo:** dejar de trabajar con `String` y representar cada entrada de la wiki con una clase del modelo que incluye su **tipo**. Así, más adelante, se podrá filtrar por personajes, misiones, objetos o lugares.

## Qué cambia

- Nueva clase `app.domain.WikiEntry` con `pageId`, `title` y `type`. El `pageId` corresponde al identificador de página de MediaWiki que devolverá la API. De momento los valores son provisionales (1…8).
- Nuevo enum `app.domain.EntryType` con `CHARACTER`, `QUEST`, `ITEM` y `PLACE`.
- `MainActivity` construye una `List<WikiEntry>`.
- `row_entry.xml` pasa a ser un `LinearLayout` vertical con dos `TextView`: el título y, debajo, el tipo.
- `EntryAdapter` trabaja con `WikiEntry` y el ViewHolder tiene un método `bind(WikiEntry)`. El texto del tipo sale de `strings.xml` (`entry_type_*`): el enum no sabe nada de la interfaz.

### Action bar de la ventana

En esta feature también se cambia el tema a `Theme.Material3.DayNight` en `values/themes.xml` y en `values-night/themes.xml`. Hasta ahora se usaba `Theme.Material3.DayNight.NoActionBar`, como en el ejemplo. Con el nuevo tema, la app muestra la **action bar del propio tema**. Por eso:

- Se elimina `toolbar.xml` y su `<include>` en `activity_main.xml`.
- `BaseActivity` ya no llama a `setSupportActionBar(...)`. Si lo hiciera con una ventana que ya tiene action bar, la app fallaría con *"This Activity already has an action bar supplied by the window decor"*.

## Para explicar

- **Separación entre datos y vista.** El adaptador recibe objetos del modelo y decide cómo pintarlos. El modelo no contiene textos ni recursos de Android.
- **`bind()` en el ViewHolder.** Encapsula cómo se muestra una fila y deja `onBindViewHolder` en una sola línea.
- **El tipo como enum.** Es más seguro que un `String` (no admite valores inventados) y en la feature 08 servirá para los chips de filtro.

## Cómo probarlo

Cada fila muestra el título de la entrada y, debajo, su tipo (Personaje, Misión, Objeto o Lugar). Arriba aparece la action bar con el nombre de la app.
