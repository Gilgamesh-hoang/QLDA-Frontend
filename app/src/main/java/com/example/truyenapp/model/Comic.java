package com.example.truyenapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comic {
    private int id;
    private String nameStory, author, describe, category, linkImage;
    private String status;
    private String keySearch;
}