package es.udc.ps.wikirym.app.data.interceptor;

import java.io.IOException;

import androidx.annotation.NonNull;
import es.udc.ps.wikirym.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UespApiInterceptor implements Interceptor {

    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String ACCEPT_HEADER = "Accept";

    // UESP asks API clients to identify themselves with a descriptive User-Agent
    private static final String USER_AGENT = "Wikirym/" + BuildConfig.VERSION_NAME + " (https://github.com/PedroSaavedraR/Wikirym)";
    private static final String JSON_RESPONSE = "application/json";

    // Parameters that every MediaWiki request needs: JSON with the modern (version 2) format
    private static final String FORMAT_PARAM = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String FORMAT_VERSION_PARAM = "formatversion";
    private static final String FORMAT_VERSION_VALUE = "2";

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {

        Request original = chain.request();

        HttpUrl url = original.url().newBuilder()
                .setQueryParameter(FORMAT_PARAM, FORMAT_VALUE)
                .setQueryParameter(FORMAT_VERSION_PARAM, FORMAT_VERSION_VALUE)
                .build();

        Request.Builder requestBuilder = original.newBuilder().url(url);

        requestBuilder.header(USER_AGENT_HEADER, USER_AGENT);
        requestBuilder.header(ACCEPT_HEADER, JSON_RESPONSE);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
