# 09 · Detalle de una entrada con fragmentos

**Objetivo:** al pulsar una entrada de la lista, hacer la **segunda petición** a UESP y abrir una vista con su detalle: imagen, tipo y texto de introducción.

El enunciado dice que, en la solución perfecta, las dos vistas deberían ser **fragmentos**. A diferencia del ejemplo, que abre una segunda actividad (`GigsActivity`), aquí hay **una sola actividad** con dos fragmentos.

## Qué cambia

- **`MainActivity`.** Ya no contiene la lista. Solo:
  - aloja los fragmentos en un `FragmentContainerView` (`activity_main.xml`);
  - abre el detalle al seleccionar una entrada;
  - gestiona la action bar: flecha de volver solo en el detalle y, en la lista, el nombre de la app.
  - Implementa `EntryListFragment.OnEntrySelectedListener`.
- **`EntryListFragment`** (`presentation.main`). Es el código de la lista, la búsqueda y los chips de la feature 08, pasado a un fragmento. Su layout es `fragment_entry_list.xml`, el antiguo `activity_main.xml`.
- **`EntryDetailFragment`** (`presentation.detail`):
  - Recibe el `pageid`, el título y el tipo con `newInstance(WikiEntry)`, guardados en los *arguments*.
  - Pone el título en la action bar.
  - Pide el detalle de la página, `prop=extracts|pageimages`.
  - Gestiona los estados de cargando, contenido y error.
- **Segunda petición en `UespAPI`.** `getPageDetail(pageId)`:
  - `extracts` con `exintro=1&explaintext=1` devuelve la introducción en texto plano.
  - `pageimages` con `piprop=thumbnail&pithumbsize=480` devuelve la imagen principal en 480 px.
  - Pasa también por `UespApiInterceptor` (User-Agent, `format`, `formatversion`) y por el log.
- **Modelos.** `PageDetailResponse` (`query → pages`) y `PageDetail` (`pageid`, `title`, `extract`, `thumbnail.source` y `missing`, que indica que la página no existe).
- **`EntryAdapter.OnEntryClickListener`.** El adaptador avisa de la fila pulsada.
- **`BaseFragment<B extends ViewBinding>`.** Equivalente a `BaseActivity` para fragmentos. Infla el binding en `onCreateView` y lo pone a `null` en `onDestroyView`.
- **`EntryTypeLabels`.** El texto de cada tipo ("Personaje", "Misión"…) pasa del adaptador a una clase de presentación, porque ahora lo usan la lista y el detalle.
- **Glide** (`com.github.bumptech.glide:glide:4.16.0`). Descarga y muestra la imagen de la entrada. Se ata al ciclo de vida del fragmento, así que deja de cargar si se sale de la pantalla.

## Navegación con fragmentos

```java
getSupportFragmentManager().beginTransaction()
        .setReorderingAllowed(true)
        .replace(R.id.main_container, EntryDetailFragment.newInstance(entry))
        .addToBackStack(null)
        .commit();
```

- **`replace` + `addToBackStack`.** El detalle sustituye a la lista y la transacción se guarda en la pila. Así, el botón Atrás del sistema y la flecha de la action bar (`onSupportNavigateUp`) vuelven a la lista.
- **`addOnBackStackChangedListener`.** Actualiza la action bar según haya algo en la pila o no.
- **Action bar del tema.** Se mantiene el requisito de la feature 02: nada de `Toolbar` propia. El título y la flecha se cambian con `setTitle(...)` y `getSupportActionBar().setDisplayHomeAsUpEnabled(...)`.

## Para explicar

- **Por qué `newInstance` y *arguments*.** Android puede destruir y recrear un fragmento (al rotar o si mata el proceso) con su constructor vacío. Los parámetros de un constructor propio se perderían; los *arguments* se guardan y se restauran solos.
- **El fragmento no conoce a la actividad.** La lista avisa mediante la interfaz `OnEntrySelectedListener`, que la actividad implementa (se comprueba en `onAttach`). Así la misma lista podría usarse en otra pantalla, por ejemplo en una tableta con lista y detalle a la vez.
- **La vista vive menos que el fragmento.**
  - Cuando se abre el detalle, el fragmento de la lista sigue en la pila, pero su vista se destruye.
  - Por eso el `mBinding` de `BaseFragment` se pone a `null` en `onDestroyView`.
  - Los callbacks de Retrofit comprueban que la vista sigue existiendo, y las peticiones en curso se cancelan.
- **Volver sin repetir la petición.**
  - El adaptador pertenece al fragmento, no a su vista: al volver, la lista ya tiene sus datos y no se pide de nuevo.
  - El texto escrito y el chip seleccionado los restaura Android en `onViewStateRestored`.
  - Los *listeners* se añaden en ese momento, para que la restauración no lance peticiones de más.
- **Rotación.** `MainActivity` solo añade la lista si `savedInstanceState == null`. Después de rotar, el `FragmentManager` ya recrea él solo los fragmentos y la pila.
- **Cada fragmento crea su propio `Retrofit`.** Igual que en el ejemplo, en esta fase monolítica cada pantalla monta su cliente. Se refactoriza en las fases siguientes (servicios y *datasources*).

## Cómo probarlo

- Busca "af" en **Personajes** y pulsa **Afflicted**. Se abre el detalle:
  - la action bar muestra "Afflicted" y la flecha de volver;
  - debajo, la imagen, "Personaje" y el texto de la wiki.
- En Logcat (`OkHttp`) aparece la segunda petición:
  `--> GET https://en.uesp.net/w/api.php?action=query&prop=extracts|pageimages&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=480&pageids=109982&format=json&formatversion=2`
- Gira el móvil: el detalle sigue abierto.
- Pulsa la flecha o Atrás:
  - vuelves a la lista con el mismo texto y el mismo chip;
  - la action bar vuelve a mostrar "Wikirym";
  - la lista no se vuelve a pedir.
