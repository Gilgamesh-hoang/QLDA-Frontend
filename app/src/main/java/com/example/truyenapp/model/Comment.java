package com.example.truyenapp.model;

import java.sql.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Comment {
    private int id, idChapter, idAccount;
    private String content, postingDay;
    private int state;
    private String bookName;
    private String chapterName;
    private Date createdAt;
    private String thumbnail;
    private String email;
    private String username;
    private String avatar;
}
