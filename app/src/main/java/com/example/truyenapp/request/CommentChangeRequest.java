package com.example.truyenapp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentChangeRequest {
    private Integer commentId;
    private Integer state;
}
