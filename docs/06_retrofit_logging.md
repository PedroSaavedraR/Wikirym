# 06 · Logging de Retrofit

**Objetivo:** ver en Logcat cada petición y respuesta HTTP que se hace a UESP.

## Qué cambia

- Dependencia `com.squareup.okhttp3:logging-interceptor` (OkHttp 5.5.1, declarada en `gradle/libs.versions.toml`).
- La actividad crea un `OkHttpClient` con un `HttpLoggingInterceptor` y se lo pasa a Retrofit con `.client(...)`.
- El nivel es `BODY` en las compilaciones *debug* y `NONE` en *release* (`BuildConfig.DEBUG`).

## Para explicar

- **Retrofit y OkHttp.** Retrofit delega el transporte en OkHttp. Los *interceptors* de OkHttp ven, y pueden modificar, cada petición y cada respuesta.
- **Qué se ve con `BODY`.**
  - La URL completa, con los parámetros `@Query` ya codificados (`cmtitle=Category%3ASkyrim-Quests`).
  - Las cabeceras de la petición, entre ellas el `User-Agent` de `@Headers`.
  - El código y las cabeceras de la respuesta (UESP está detrás de Cloudflare y Varnish).
  - El JSON y el tiempo que ha tardado.
- **No registrar en producción.** En *release* no se registra nada: el log puede contener datos personales o credenciales.

## Cómo probarlo

Ejecuta la app y filtra Logcat por la etiqueta `OkHttp`:

```
--> GET https://en.uesp.net/w/api.php?action=query&list=categorymembers&cmtitle=Category%3ASkyrim-Quests&cmlimit=50&format=json&formatversion=2
User-Agent: Wikirym/1.0 (https://github.com/PedroSaavedraR/Wikirym)
--> END GET
<-- 200 https://en.uesp.net/w/api.php?... (1917ms)
...
{"batchcomplete":true,"continue":{"cmcontinue":"page|…"},"query":{"categorymembers":[…]}}
<-- END HTTP (1919ms, 3668-byte body)
```
