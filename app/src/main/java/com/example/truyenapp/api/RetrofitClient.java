package com.example.truyenapp.api;

import android.content.Context;

import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.SystemConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {
    private final static String BASE_URL = "http://10.0.0.228:8081/api/";

//    private final static String BASE_URL = "https://5e45-14-241-170-199.ngrok-free.app/api/";

    public static Retrofit getInstance(Context context) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        JWTToken jwtToken = SharedPreferencesHelper.getObject(context, SystemConstant.JWT_TOKEN, JWTToken.class);
        // Thêm Interceptor để thêm JWT vào header
        if (jwtToken != null) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + jwtToken.getToken())
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }

}
