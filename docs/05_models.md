# 05 · Modelos CategoryMembersResponse y WikiEntry

**Objetivo:** que los modelos coincidan con el JSON que devuelve la API de MediaWiki de UESP.

```json
{ "batchcomplete": true,
  "continue": { "cmcontinue": "page|…", "continue": "-||" },
  "query": { "categorymembers": [ { "pageid": 148578, "ns": 134, "title": "Skyrim:Aftershock" } ] } }
```

## Qué cambia

- **`CategoryMembersResponse`.** Representa la respuesta completa. Como el JSON está anidado un nivel más que en MusicBrainz, tiene una clase interna `Query` con la lista `categorymembers`.
- **`WikiEntry`.**
  - Se anotan `pageid` y `title` con `@SerializedName`, porque los campos Java llevan el prefijo `m`.
  - El tipo (`mType`) no viene en el JSON. La actividad lo asigna con `setType(...)` según la categoría pedida.
  - Nuevo método `getDisplayTitle()`, que quita el prefijo de espacio de nombres `Skyrim:` para mostrarlo en la lista. `getTitle()` mantiene el título completo, que hará falta para pedir el detalle.
- **`UespAPI`.** Devuelve `Call<CategoryMembersResponse>`, y la actividad usa `response.body().getQuery().getCategoryMembers()`.

## Para explicar

- **Gson y los atributos.** Gson rellena los campos por nombre. `@SerializedName` desacopla el nombre Java (`mPageId`) del nombre en el JSON (`pageid`).
- **JSON anidado.** Cada objeto del JSON es una clase. Con una clase interna se ve claramente la estructura `query → categorymembers`.
- **No hace falta mapear todo el JSON.** Los campos que no se declaran (`batchcomplete`, `continue`, `ns`) se ignoran. El token `continue` servirá para paginar.
- **Datos que no vienen del servidor.** El tipo de la entrada se deduce de la petición (la categoría), no de la respuesta.

## Cómo probarlo

Al abrir la app aparece la lista real de las 50 primeras misiones de Skyrim (`Category:Skyrim-Quests`), cada una con el tipo "Misión".
