package com.example.truyenapp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Integer point;
    private String role;
    private String avatar;
    private int rewardPoint;
    private int totalAttendanceDates;
    private int totalComments;
    private int totalBookReads;
    private int numberOfRatings;
}