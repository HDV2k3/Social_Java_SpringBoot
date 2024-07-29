package com.example.socialmediaapp.Responses;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostFollowedGetResponse {
    private int postId;
    private int userId;
    private String userName;
    private String userAvatar; // Add this field
    private String postDescription;
    private String titlePost;
    private List<String> postImages;
    private Date postCreatedAt;
}
