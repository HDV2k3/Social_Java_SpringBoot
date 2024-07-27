package com.example.socialmediaapp.Responses;


import com.example.socialmediaapp.Models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostGetResponse {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String contentPost;
    private String titlePost;
    private List<String> urlImagePost;
    private Date create_at;
}

