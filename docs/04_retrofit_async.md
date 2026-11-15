# 04 · Acceso a red asíncrono con Retrofit

**Objetivo:** hacer la petición a UESP fuera del hilo principal.

## Qué cambia

- **`call.enqueue(new Callback<...>())`.** Retrofit ejecuta la petición en un hilo de OkHttp y entrega `onResponse` / `onFailure` en el hilo principal de Android.
- **Adaptador.** `EntryAdapter` se crea vacío y recibe los datos más tarde con `setItems(...)` + `notifyDataSetChanged()`.
- **Error.** Nuevo recurso `error_general`, que se muestra en un `Toast` si la petición falla.

## Para explicar

- **Diferencia entre `execute()` y `enqueue()`.** El primero bloquea el hilo que lo llama. El segundo no bloquea y devuelve el resultado en un callback.
- **Un dato asíncrono llega "más tarde".** La UI se pinta primero vacía y se actualiza cuando llega la respuesta.
- **El fallo esperado.** La respuesta de MediaWiki es un objeto (`{"batchcomplete":…,"query":{…}}`) y no encaja con `List<WikiEntry>`. Gson falla con `Expected BEGIN_ARRAY but was BEGIN_OBJECT`, salta `onFailure` y aparece el `Toast` de error. La siguiente feature crea el modelo correcto.

## Cómo probarlo

Ejecuta la app:

- La petición ya no lanza `NetworkOnMainThreadException`.
- Aparece el `Toast` "Ups, ha ocurrido un error".
- En Logcat, con la etiqueta `MainActivity`, aparece `Expected BEGIN_ARRAY but was BEGIN_OBJECT`.
