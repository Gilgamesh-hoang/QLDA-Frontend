package com.example.truyenapp.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataListResponse<T> {
    List<T> data;
    Integer currentPage;
    Integer totalPages;
}
