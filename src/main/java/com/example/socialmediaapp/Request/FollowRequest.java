package com.example.socialmediaapp.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowRequest {
    private int userId;
    private int followingId;
}
