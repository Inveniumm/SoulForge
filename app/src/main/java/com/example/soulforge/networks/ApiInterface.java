package com.example.soulforge.networks;

import com.example.soulforge.model.NotificationModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET()
    Call<Object> getCall(@Url String url);

    @POST()
    Call<Object> postCall(@Url String url);

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAesuW7-0:APA91bF_lyhJMJnQup1AjcTUP6FscJTtIblhAEiyb40Twth5RQ3IhnvBm8P7DGmwfKHq0KJyRTLInlZVHPqbsB9zAeQdjMDAhRpf86zpPEkenT_HOccg--d7133LO5tO7jcujak6Gr3W",
    })
    @POST()
    Call<Object> postCall(@Url String url, @Body NotificationModel params);

    @Headers("Content-Type: application/json")
    @POST()
    Call<Object> postCall(@Header("X-CSRF-TOKEN") String bearerToken, @Url String url, @QueryMap Map<String, String> params);

    @Headers("Content-Type: application/json")
    @POST()
    Call<Object> postCall(@Header("X-CSRF-TOKEN") String bearerToken, @QueryMap() Map<String, RequestBody> list, @Url String url, @QueryMap Map<String, String> params);

    @Headers("Content-Type: application/json")
    @POST()
    Call<Object> postCall(@Header("X-CSRF-TOKEN") String bearerToken, @Url String url);

    @Multipart
    @POST()
    Call<Object> postCall(@Url String url, @Part List<MultipartBody.Part> media, @QueryMap Map<String, String> params);
}
