package com.example.truyenapp.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {
    private Integer id;
    private String name;
    private String author;
    private String description;
    private String thumbnail;
    private List<String> categoryNames;
    private String status;


}
