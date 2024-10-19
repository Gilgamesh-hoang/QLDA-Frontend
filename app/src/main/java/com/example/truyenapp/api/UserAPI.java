package com.example.truyenapp.api;

import com.example.truyenapp.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserAPI {

    @GET("v1/users/info")
    Call<UserResponse> getUserInfo(@Query("token") String token);

}
