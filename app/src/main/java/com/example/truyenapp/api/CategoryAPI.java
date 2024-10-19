package com.example.truyenapp.api;

import com.example.truyenapp.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryAPI {
    @GET("v1/categories")
    Call<List<CategoryResponse>> getAll();
}
