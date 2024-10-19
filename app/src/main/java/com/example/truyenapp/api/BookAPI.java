package com.example.truyenapp.api;

import com.example.truyenapp.request.BookRequest;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.ChapterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookAPI {
    @GET("v1/books")
    Call<List<BookResponse>> getAllBooks();

    @GET("v1/books/{id}")
    Call<BookResponse> getBook(@Path("id") int id);

    @GET("v1/books/chapter/{id}")
    Call<BookResponse> getBookByChapterId(@Path("id") int id);

    @POST("v1/admin/books")
    Call<APIResponse<Void>> addNewBook(@Body BookRequest bookRequest);

    @PUT("v1/admin/books")
    Call<APIResponse<Void>> updateBook(@Body BookRequest bookRequest);

    @DELETE("v1/admin/books/{id}")
    Call<APIResponse<Void>> deleteBook(@Path("id") Integer id);

    @GET("v1/books/description/{idBook}")
    Call<APIResponse<BookResponse>> getDescriptionBook(@Path("idBook") int idBook);

    @GET("v1/chapters/book/{id}")
    Call<List<ChapterResponse>> getChaptersByBookId(@Path("id") int id);

    @GET("v1/books/{id}/comment")
    Call<APIResponse<Integer>> getAllComment(@Path("id") int id);
}
    