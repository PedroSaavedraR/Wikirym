# 07 · Interceptores, cabeceras y parámetros comunes

**Objetivo:** añadir con un interceptor de OkHttp lo que necesitan todas las peticiones a UESP, en lugar de repetirlo en cada método de la interfaz.

## Qué cambia

- **`UespApiInterceptor`** (`app.data.interceptor`). Se aplica a todas las peticiones del cliente:
  - Cabecera `User-Agent: Wikirym/<versión> (https://github.com/PedroSaavedraR/Wikirym)`, que UESP pide para identificar a los clientes de la API. Sustituye a la anotación `@Headers` de la interfaz.
  - Cabecera `Accept: application/json`.
  - Parámetros fijos `format=json` y `formatversion=2`. Los añade a la URL con `HttpUrl.newBuilder().setQueryParameter(...)`, porque MediaWiki los necesita en todas las llamadas.
- **`UespAPI`.** Pierde `@Headers` y los parámetros `format` y `formatversion`. Solo quedan los propios de la petición (`cmtitle`, `cmlimit`).
- **Orden de los interceptores.** `UespApiInterceptor` se añade **antes** que el de logging. Los interceptores se ejecutan en orden, así que el log ya muestra la petición definitiva, con las cabeceras y los parámetros añadidos. Si fuese al revés, el log enseñaría la petición sin ellos.
- **Personajes en vez de misiones.** La lista pide ahora `Category:Skyrim-NPCs` y marca las entradas como `EntryType.CHARACTER`.

## Para explicar

- **Anotación o interceptor.** `@Headers` y `@Query` sirven para una petición. Un interceptor sirve para todas las de un cliente.
- **Un interceptor puede cambiar la URL, no solo las cabeceras.** Es el sitio natural para parámetros comunes, como el formato de respuesta o una API key por *query string*.
- **API key.** UESP no necesita clave: basta con el `User-Agent`. Si la necesitara, se leería de `local.properties` (que no se sube a git), como hace el ejemplo con setlist.fm, y la añadiría este mismo interceptor.

## Cómo probarlo

Ejecuta la app y filtra Logcat por `OkHttp`:

```
--> GET https://en.uesp.net/w/api.php?action=query&list=categorymembers&cmtitle=Category%3ASkyrim-NPCs&cmlimit=50&format=json&formatversion=2
User-Agent: Wikirym/1.0 (https://github.com/PedroSaavedraR/Wikirym)
Accept: application/json
--> END GET
<-- 200 https://en.uesp.net/w/api.php?... (908ms)
```

La lista muestra ahora personajes de Skyrim.
