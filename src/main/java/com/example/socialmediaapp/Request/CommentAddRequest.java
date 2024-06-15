package com.example.socialmediaapp.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentAddRequest {
    private int postId;
    private int userId;
    private String description;
}
