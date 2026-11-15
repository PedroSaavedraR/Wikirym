# 01 · Lista de entradas de la wiki

**Objetivo:** mostrar una lista fija de entradas de la wiki de Skyrim (personajes, misiones, objetos y lugares) con un `RecyclerView`, accediendo a las vistas con **ViewBinding**.

## Qué cambia

- `app/build.gradle.kts` activa `buildFeatures.viewBinding` y añade `androidx.recyclerview`. La versión se declara en `gradle/libs.versions.toml` (version catalog).
- `BaseActivity<B extends ViewBinding>` infla el binding, llama a `setContentView(mBinding.getRoot())`, configura la toolbar y aplica los *insets* de las barras del sistema (edge-to-edge, obligatorio desde Android 15).
- `MainActivity` pasa al paquete `app.presentation.main` y rellena el `RecyclerView` con una lista de `String`.
- `EntryAdapter` crea cada fila con `RowEntryBinding.inflate(...)`, de modo que el ViewHolder guarda el binding y no usa `findViewById`.

## Por qué ViewBinding

ViewBinding lo genera el plugin de Android:

- Crea una clase por layout (`activity_main.xml` → `ActivityMainBinding`, `row_entry.xml` → `RowEntryBinding`).
- Es *null-safe* y *type-safe*.
- No necesita librerías externas (a diferencia de ButterKnife, ya obsoleto).

## Cómo probarlo

Ejecuta la app: aparece la lista de ocho entradas bajo la toolbar.
