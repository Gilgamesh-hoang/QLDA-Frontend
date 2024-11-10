package com.example.truyenapp.response;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreationResponseDTO {
    private String content;
    private Date createdAt;
}
