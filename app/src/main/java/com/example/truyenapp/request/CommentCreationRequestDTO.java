package com.example.truyenapp.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreationRequestDTO {
    private Integer chapterId;
    private String content;
}
