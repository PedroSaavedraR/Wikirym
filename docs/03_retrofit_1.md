# 03 · Acceso a la wiki de UESP con Retrofit (síncrono)

**Objetivo:** obtener las entradas de la wiki de Skyrim desde la API de [UESP](https://en.uesp.net/w/api.php) (MediaWiki) con Retrofit.

## Qué cambia

- **Dependencias.** Se añaden `retrofit`, `converter-gson` y `gson` (Retrofit 3.0.0 y Gson 2.14.0, declarados en `gradle/libs.versions.toml`). Retrofit 3 y OkHttp 5 funcionan con `minSdk 23` y Java 11, así que no hace falta subir ninguno de los dos.
- **BuildConfig.** Se activa `buildFeatures.buildConfig` para poder usar `BuildConfig.VERSION_NAME`.
- **Permiso.** Se añade el permiso `INTERNET` en el `AndroidManifest.xml`.
- **`UespAPI`.** Declara la petición con anotaciones:
  - `@GET("api.php?action=query&list=categorymembers")`: los parámetros fijos de la acción van en la propia ruta.
  - `@Query` para `cmtitle` (la categoría), `cmlimit`, `format` y `formatversion`.
  - Una cabecera `@Headers` con un `User-Agent` propio, porque UESP pide que los clientes de la API se identifiquen.
- **`MainActivity`.** Crea el `Retrofit` con la URL base `https://en.uesp.net/w/` y ejecuta la llamada con `call.execute()`, pidiendo las 50 primeras misiones (`Category:Skyrim-Quests`).

## La API de UESP

Es una wiki MediaWiki y Skyrim tiene su propio espacio de nombres (`Skyrim:`, número 134). Las categorías de cada tipo, comprobadas en el navegador, son:

| Tipo | Categoría |
|------|-----------|
| Personajes | `Category:Skyrim-NPCs` |
| Misiones | `Category:Skyrim-Quests` |
| Objetos | `Category:Skyrim-Items` |
| Lugares | `Category:Skyrim-Places` |

## El error que se busca mostrar

`execute()` es **síncrono** y se ejecuta en el hilo principal (UI). Android lo prohíbe y lanza `NetworkOnMainThreadException`: la app captura la excepción, la escribe en el log y muestra la lista vacía.

Además, la interfaz todavía no encaja con el JSON. La respuesta de MediaWiki no es una lista, sino un objeto anidado:

```json
{"batchcomplete": true,
 "continue": {"cmcontinue": "...", "continue": "-||"},
 "query": {"categorymembers": [{"pageid": 148578, "ns": 134, "title": "Skyrim:..."}]}}
```

- La siguiente feature resuelve el hilo con una llamada asíncrona.
- La feature 05 resuelve el modelo de la respuesta.

## Cómo probarlo

Ejecuta la app y busca en Logcat la etiqueta `MainActivity`: aparece `NetworkOnMainThreadException` y la lista sale vacía.
