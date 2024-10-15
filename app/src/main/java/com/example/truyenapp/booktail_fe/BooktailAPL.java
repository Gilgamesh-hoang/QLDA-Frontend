package com.example.truyenapp.booktail_fe;
import com.example.truyenapp.response.BookeResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface BooktailAPI {
    @GET("v1/categories")
    Call<List<BooktailResponse>> getAll();
}