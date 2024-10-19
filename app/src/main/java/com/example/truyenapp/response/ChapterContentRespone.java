package com.example.truyenapp.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterContentRespone {
    private Integer id;
    private Integer chapterId;
    private String linkImage;
}
