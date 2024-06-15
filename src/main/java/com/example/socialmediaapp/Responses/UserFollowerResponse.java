package com.example.socialmediaapp.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowerResponse {
    private int userId;
    private String name;
    private String lastName;
}
