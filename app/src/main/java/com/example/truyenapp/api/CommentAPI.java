package com.example.truyenapp.api;

import com.example.truyenapp.request.CommentChangeRequest;
import com.example.truyenapp.request.CommentCreationRequestDTO;
import com.example.truyenapp.request.CommentResponseOverall;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.response.DataListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentAPI {
    @GET("v1/admin/comment")
    public Call<APIResponse<DataListResponse<CommentResponse>>> getComments(@Query("page") int page, @Query("size") int size);

    @POST("v1/admin/comment")
    public Call<APIResponse> changeState(@Body CommentChangeRequest request);

    @GET("v1/admin/comment/detail/{id}")
    public Call<APIResponse<CommentResponse>> getCommentDetail(@Path("id") Integer id);

    @GET("v1/comment/chapter")
    public Call<APIResponse<DataListResponse<CommentResponse>>> getComments(@Query("type") String type, @Query("id") Integer chapterId, @Query("page") int page, @Query("size") int size);

    @POST("v1/comment/create")
    public Call<APIResponse<CommentResponse>> create(@Body CommentCreationRequestDTO request);

    @GET("v1/comment/user")
    public Call<APIResponse<CommentResponseOverall>> getCommentOverall(@Query("page") int page, @Query("size") int size);

}
