package com.example.socialmediaapp.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequest {
    private int postId;
    private int userId;
}
