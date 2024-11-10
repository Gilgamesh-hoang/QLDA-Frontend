package com.example.truyenapp.request;

import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.response.DataListResponse;

import lombok.Data;

@Data
public class CommentResponseOverall {
    private Integer totalComment;
    private DataListResponse<CommentResponse> data;
}
