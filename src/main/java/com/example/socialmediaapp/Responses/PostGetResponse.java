package com.example.socialmediaapp.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostGetResponse {
    private int id;
    private int userId;
    private String userName;
    private String userLastName;
    private String Description;
}

