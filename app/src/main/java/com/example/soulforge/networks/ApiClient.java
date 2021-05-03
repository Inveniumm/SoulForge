package com.example.soulforge.networks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private static Retrofit retrofit = null;
    private static final int CALL_TIMEOUT = 1;
    private static final int CONNECT_TIMEOUT = 1;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 15;

    public static Retrofit getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.callTimeout(CALL_TIMEOUT, TimeUnit.MINUTES)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MINUTES)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//                .addInterceptor(new Interceptor() {
//                    @NotNull
//                    @Override
//                    public Response intercept(@NotNull Chain chain) throws IOException {
//                        Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json")
//                                .addHeader("Authorization","key=AAAAesuW7-0:APA91bF_lyhJMJnQup1AjcTUP6FscJTtIblhAEiyb40Twth5RQ3IhnvBm8P7DGmwfKHq0KJyRTLInlZVHPqbsB9zAeQdjMDAhRpf86zpPEkenT_HOccg--d7133LO5tO7jcujak6Gr3W").build();
//                        return chain.proceed(request);
//                    }
//                })
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient client = httpClient.build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(getGSONBuilder())).build();
        }

        return retrofit;
    }

    public static Gson getGSONBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Double.class,
                        (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                            if (src == src.longValue())
                                return new JsonPrimitive("" + src.longValue());
                            return new JsonPrimitive("" + src);
                        })
                .create();
    }
}
