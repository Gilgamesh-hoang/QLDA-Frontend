package com.example.truyenapp.response;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Integer id;
    private String name;
    private String author;
    private String description;
    private Integer view;
    private Double rating;
    private String thumbnail;
    private Integer quantityChapter;
    private List<String> categoryNames;
    private Date publishDate;
    private String status;
}
