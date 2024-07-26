package com.example.socialmediaapp.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAddRequest {
    private int userId;
    private String contentPost;
    private String titlePost;
    private List<String> urlImagePost;
    private Date create_at;
}
