package com.example.truyenapp.response;

import java.sql.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterResponse {
    private Integer id;
    private String name;
    private Date publishDate;
    private Integer view;
    private Float rating;
    private List<ChapterContentRespone> chapterContent;
}
