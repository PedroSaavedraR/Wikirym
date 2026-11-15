# Wikirym

Aplicación Android para la asignatura PSI. Consulta la wiki de Skyrim y permite buscar entradas filtrando por tipo: personajes, misiones, objetos, lugares…

Sigue la misma evolución paso a paso que el proyecto de ejemplo de clase (PSICleanArch_v2): de una aplicación **monolítica** con Retrofit a una **arquitectura en capas (Clean)**.

## Tecnologías

- Android Gradle Plugin 9.4, Gradle 9.6 (Kotlin DSL y *version catalog*).
- Java 11, `compileSdk`/`targetSdk` 37, `minSdk` 23.
- ViewBinding, Material 3, RecyclerView y Retrofit (Gson).

## Estructura de ramas (gitflow)

- `master`: versiones publicadas.
- `develop`: integración de todas las features, con un *merge* `--no-ff` por feature.
- `feature/NN_*`: cada paso de la evolución, con su explicación en `docs/`.

| Rama | Fase | Contenido |
|------|------|-----------|
| [`feature/01_lista_de_entradas`](docs/01_lista_de_entradas.md) | Monolítica | RecyclerView y ViewBinding con una lista fija de entradas |
| [`feature/02_model_entry`](docs/02_model_entry.md) | Monolítica | Modelo `WikiEntry` y enum `EntryType`; tema con action bar |
| [`feature/03_retrofit_1`](docs/03_retrofit_1.md) | Monolítica | Retrofit síncrono contra la API de UESP (`NetworkOnMainThreadException`) |
| [`feature/04_retrofit_async`](docs/04_retrofit_async.md) | Monolítica | Llamada asíncrona con `enqueue` y `Toast` de error |
| [`feature/05_models`](docs/05_models.md) | Monolítica | Modelos `CategoryMembersResponse` y `WikiEntry` que encajan con el JSON de MediaWiki |
