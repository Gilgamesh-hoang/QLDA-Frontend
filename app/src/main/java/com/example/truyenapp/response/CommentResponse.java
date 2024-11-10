package com.example.truyenapp.response;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Integer id;
    private UserCommentDTO user;
    private String bookName;
    private String chapterName;
    private String content;
    private Date createdAt;
    private Integer state;
    private String thumbnail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCommentDTO {
        private String username;
        private String email;
        private String avatar;
    }
}
