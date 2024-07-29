package com.example.socialmediaapp.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentGetResponse {
    private int id;
    private int postId;
    private int userId;
    private String userName;
    private String userLastName;
    private String avatarUser;
    private String description;
    private LocalDateTime create_at;
}

